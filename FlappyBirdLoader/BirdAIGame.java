package FlappyBirdLoader;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author harryhe
 */
public class BirdAIGame extends Canvas implements Runnable, KeyListener {
	public static final int WIDTH = 800, HEIGHT = 600;
	private Thread thread;
	private boolean running = false;

	public static final int population = 500;
	public static int cycles = 1;

	ArrayList<Bird> AIBirds;
	ArrayList<String> paths;
	Bird player;
	public static boolean [] renders;
	int lastElim;
	ArrayList<Pipe> pipes;

	long frameCount;
	int pipeSpacing = 70;
	
	public static boolean AIimmunity = false;
	public static boolean playerImmunity = false;
	public static boolean renderOnlyPlayer = false;
	
	public static final int gravityImmuneDef = 15;
	public static int gravityImmunity;
	
	int pipesCrossed;
	Font renderFont;
	
	int smallWins;
	int largeWins;
	int v3Wins;
	int totalWins;
	int [] birdScores;
	final int firstToGames = 200;
	int highestValue;
	int highestAchiever;

	public BirdAIGame() {
		this.setBackground(new Color(0, 0, 0));

		paths = new ArrayList<>();
//		paths.add("LaterBlueBird.txt");
//		paths.add("Survivor.txt");
//		paths.add("AllTimeBest.txt");
//		paths.add("LaterRedBrown.txt");
//		paths.add("MidBlueBird.txt");
//		paths.add("MidMuddyBird.txt");
//		paths.add("AllTimeBest9.txt");
		
		paths.add("Survivor.txt");
//		paths.add("BestGreenBrown.txt");
//		paths.add("AllTimeBest13.txt");
//		paths.add("LaterRedBrown.txt");
//		paths.add("LaterBlueBird.txt");
//		paths.add("MidBlueBird.txt");
		
//		paths.add("SophisticatedBest11.txt");
//		paths.add("SophisticatedBest13.txt");
//		paths.add("SophisticatedBest14.txt");
//		paths.add("SophisticatedBest65.txt");
//		paths.add("SophisticatedBest111.txt");
		paths.add("SophisticatedBest118.txt");
		
//		paths.add("L_V3_47.txt");
//		paths.add("L_V3_55.txt");
//		paths.add("L_V3_101.txt");
//		paths.add("L_V3_130.txt");
//		paths.add("L_V3_Latest.txt");
		paths.add("L_V3_178.txt");
		
		AIBirds = new ArrayList<>();
		for(int i = 0; i < paths.size(); i++) {
			AIBirds.add(loadBird(paths.get(i)));
		}
		
//		bird = loadBird(brainPath1);
//		bird2 = loadBird(brainPath2);
		player = new Bird(WIDTH, HEIGHT);
		renders = new boolean[1 + AIBirds.size()];
		for(int i = 0; i < renders.length; i++) {
			renders[i] = true;
		}
		lastElim = -1;

		this.pipes = new ArrayList<>();
		
		pipesCrossed = 0;
		renderFont = new Font("arial", Font.TRUETYPE_FONT, 40);
		gravityImmunity = gravityImmuneDef;
		
		/*
		 * Remove later
		 */
		AIBirds.add(new Bird(WIDTH, HEIGHT, true));
		paths.add("Null Bird");
		renders = new boolean[1 + AIBirds.size()];
		for(int i = 0; i < renders.length; i++) {
			renders[i] = true;
		}
		
		birdScores = new int[paths.size()];
		highestValue = 0;

		addKeyListener(this);
		new Window(WIDTH, HEIGHT, "Flappy Bird", this);
	}
	
	public Bird loadBird(String path) {
		if(path.startsWith("Sophisticated") || path.startsWith("L_")) {
			int [] temp = {1};
			LargeNeuralNetwork bird_brain = new LargeNeuralNetwork(0, temp, 0);
			String [] colors = {"255", "255", "255"};
			try {
				bird_brain.deserialize(path);
				Scanner inputFile = new Scanner(new File(path));
				String previous = "";
				while(inputFile.hasNextLine()) {
					previous = inputFile.nextLine();
				}
				colors = previous.split(" ");
				if(colors.length != 3) {
					colors = new String[3];
					colors[0] = "255";
					colors[1] = "255";
					colors[2] = "255";
				}
				System.out.println("Loaded Sophisticated Bird Brain");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return new Bird(WIDTH, HEIGHT, bird_brain, Double.parseDouble(colors[0]),
					Double.parseDouble(colors[1]), Double.parseDouble(colors[2]));
		} else {
			NeuralNetwork bird_brain = new NeuralNetwork(0, 0, 0);
			String [] colors = {"255", "255", "255"};
			try {
				bird_brain.deserialize(path);
				Scanner inputFile = new Scanner(new File(path));
				String previous = "";
				while(inputFile.hasNextLine()) {
					previous = inputFile.nextLine();
				}
				colors = previous.split(" ");
				if(colors.length != 3) {
					colors = new String[3];
					colors[0] = "255";
					colors[1] = "255";
					colors[2] = "255";
				}
				System.out.println("Loaded Bird Brain");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return new Bird(WIDTH, HEIGHT, bird_brain, Double.parseDouble(colors[0]),
					Double.parseDouble(colors[1]), Double.parseDouble(colors[2]));
		}
	}
	
	public void resetBirds() {
		for(int i = 0; i < AIBirds.size(); i++) {
			AIBirds.get(i).reset();
		}
		player.reset();
		pipes.clear();
		frameCount = 0;
		pipesCrossed = 0;
		gravityImmunity = gravityImmuneDef;
		for(int i = 0; i < renders.length; i++) {
			renders[i] = true;
		}
	}

	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 40.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running) {
				render();
				if (delta < 1) {
					try {
						Thread.sleep((long) (1000 / amountOfTicks - 500 / amountOfTicks));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}

	public void tick() {
		for (int n = 0; n < cycles; n++) {
			if (frameCount % pipeSpacing == 0) {
				pipes.add(new Pipe(WIDTH, HEIGHT));
			}
			frameCount++;

			for(int i = 0; i < AIBirds.size(); i++) {
				if(renders[i + 1]) {
					AIBirds.get(i).think(pipes);
					AIBirds.get(i).tick();
				}
			}
			
			player.tick();
			if(gravityImmunity > 0 && player.velocity > 0) {
				player.velocity -= player.gravity;
				gravityImmunity--;
			}
			
			boolean reset = true;
			for (int i = pipes.size() - 1; i >= 0; i--) {
				pipes.get(i).tick();
				
				if ((pipes.get(i).hits(player) || player.outOfBounds()) && !playerImmunity && renders[0]) {
					System.out.println("Player is eliminated");
					renders[0] = false;
					lastElim = 0;
				}

				for(int j = 0; j < AIBirds.size(); j++) {
					if ((pipes.get(i).hits(AIBirds.get(j)) || AIBirds.get(j).outOfBounds()) && !AIimmunity && renders[j + 1]) {
						System.out.println("Bird " + (j + 1) + " is eliminated");
						renders[j + 1] = false;
						lastElim = j + 1;
					}
				}

				if (pipes.get(i).offscreen()) {
					pipes.remove(i);
				}
				if(pipes.get(i).x + pipes.get(i).w < player.x && !pipes.get(i).passed) {
					pipesCrossed++;
					pipes.get(i).passed = true;
				}
			}
			for(int i = 0; i < renders.length; i++) {
				if(renders[i]) {reset = false; break; }
			}
			if(reset) {
				if(lastElim == 0) {
					System.out.println("Player is the winner");
				} else {
					birdScores[lastElim - 1]++;
					totalWins++;
					System.out.println("Bird " + lastElim + " is the winner (" + paths.get(lastElim - 1) + ")");
					
					for(int i = 0; i < birdScores.length; i++) {
						if(birdScores[i] >= firstToGames) {
							System.out.println("The WINNER IS: " + paths.get(i));
							System.out.println("Highest value gotten to: " + highestValue + " (" + paths.get(highestAchiever - 1) + ")");
							System.exit(0);
						}
					}
					Pair [] leaderboard = new Pair[birdScores.length];
					for(int i = 0; i < leaderboard.length; i++) {
						leaderboard[i] = new Pair(birdScores[i], i);
					}
					Arrays.sort(leaderboard, new CompPair());
					for(int i = 0; i < leaderboard.length; i++) {
						System.out.printf("%-25s%-7d%.2f%%\n", paths.get(leaderboard[i].second), leaderboard[i].first, 
								(double)leaderboard[i].first / (totalWins) * 100);
					}
					
//					if((lastElim - 1) < 6) {
//						smallWins++;
//					} else if(lastElim - 1 < 12){
//						largeWins++;
//					} else {
//						v3Wins++;
//					}
//					System.out.println(smallWins + " : " + largeWins + " : " + v3Wins);
//					if(smallWins >= firstToGames || largeWins >= firstToGames || v3Wins >= firstToGames) {
//						if(smallWins >= firstToGames) {
//							System.out.println("Small is superior");
//						} else if(largeWins >= firstToGames){
//							System.out.println("Large is superior");
//						} else if(v3Wins >= firstToGames){
//							System.out.println("V3 is superior");
//						}
//						
//						Pair [] leaderboard = new Pair[birdScores.length];
//						for(int i = 0; i < leaderboard.length; i++) {
//							leaderboard[i] = new Pair(birdScores[i], i);
//						}
//						Arrays.sort(leaderboard, new CompPair());
//						for(int i = 0; i < leaderboard.length; i++) {
//							System.out.printf("%-25s%-7d%.2f%%\n", paths.get(leaderboard[i].second), leaderboard[i].first, 
//									(double)leaderboard[i].first / (totalWins) * 100);
//						}
//						System.out.println("Highest value gotten to: " + highestValue + " (" + paths.get(highestAchiever - 1) + ")");
//						System.exit(0);
//					}
				}
				if(pipesCrossed > highestValue) {
					highestValue = pipesCrossed;
					highestAchiever = lastElim;
				}
				resetBirds();
			}
		}
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.setColor(this.getBackground());
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(new Color(255, 255, 255));
		g.setFont(renderFont);
		g.drawString(Integer.toString(pipesCrossed), 30, 40);
		
		if(renders[0]) {
			player.render(g);
		}
		
		if(!renderOnlyPlayer) {
			for(int i = 0; i < AIBirds.size(); i++) {
				if(renders[i + 1]) {
					AIBirds.get(i).render(g);
				}
			}
		}
		
		for (int i = 0; i < pipes.size(); i++) {
			pipes.get(i).render(g);
		}

		g.dispose();
		bs.show();
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new BirdAIGame();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_X) {
			cycles = 1;
		} else if (e.getKeyCode() == KeyEvent.VK_1) {
			cycles = 10;
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			cycles = 25;
		} else if (e.getKeyCode() == KeyEvent.VK_3) {
			cycles = 100;
		} else if (e.getKeyCode() == KeyEvent.VK_4) {
			cycles = 500;
		} else if (e.getKeyCode() == KeyEvent.VK_5) {
			cycles = 1000;
		} else if (e.getKeyCode() == KeyEvent.VK_6) {
			cycles = 10000;
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.up();
		} else if (e.getKeyCode() == KeyEvent.VK_0) {
			AIimmunity = !AIimmunity;
		} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			playerImmunity = !playerImmunity;
		} else if (e.getKeyCode() == KeyEvent.VK_R) {
			resetBirds();
		} else if (e.getKeyCode() == KeyEvent.VK_G) {
			for(int i = 0; i < renders.length; i++) {
				if(renders[i]) System.out.println(i);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_P) {
			renderOnlyPlayer = !renderOnlyPlayer;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
	/*
	 boolean reset = false;
			for (int i = pipes.size() - 1; i >= 0 && !reset; i--) {
				pipes.get(i).tick();

				if (pipes.get(i).hits(bird) && !AIimmunity) {
					System.out.println("Player Wins, Bird 1");
					reset = true;
				} else if (bird.outOfBounds() && !AIimmunity) {
					System.out.println("Player Wins, Bird 1");
					reset = true;
				}
				
				if (pipes.get(i).hits(bird2) && !AIimmunity) {
					System.out.println("Player Wins, Bird 2");
					reset = true;
				} else if (bird2.outOfBounds() && !AIimmunity) {
					System.out.println("Player Wins, Bird 2");
					reset = true;
				}
				
				if (pipes.get(i).hits(player) && !playerImmunity && !reset) {
					System.out.println("AI Wins");
					reset = true;
				} else if (player.outOfBounds() && !playerImmunity && !reset) {
					System.out.println("AI Wins");
					reset = true;
				}

				if (pipes.get(i).offscreen()) {
					pipes.remove(i);
				}
				if(pipes.get(i).x + pipes.get(i).w < player.x && !pipes.get(i).passed) {
					pipesCrossed++;
					pipes.get(i).passed = true;
				}
			}
			if(reset) {
				bird.reset();
				bird2.reset();
				player.reset();
				pipes.clear();
				frameCount = 0;
				pipesCrossed = 0;
				gravityImmunity = gravityImmuneDef;
			}
	 * 
	 */

}
