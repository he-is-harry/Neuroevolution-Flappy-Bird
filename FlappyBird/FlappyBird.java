package FlappyBird;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author harryhe
 */
public class FlappyBird extends Canvas implements Runnable, KeyListener {
	public static final int WIDTH = 800, HEIGHT = 600;
	private Thread thread;
	private boolean running = false;

	public static final int population = 500;
	public static int cycles = 1;

	ArrayList<Bird> birds;
	ArrayList<Bird> savedBirds;
	ArrayList<Pipe> pipes;

	long frameCount;
	int pipeSpacing = 70;

	GeneticAlgo algo;

	int pipesCrossed;

	public FlappyBird() {
		this.setBackground(new Color(0, 0, 0));
		this.birds = new ArrayList<>();
		this.savedBirds = new ArrayList<>();
		
		try {
			// Load in birds from all time best to start off where
			// the program was left off
			Scanner inputFile = new Scanner(new File("AllTimeBest.txt"));
			
			LargeNeuralNetwork bird_brain = new LargeNeuralNetwork(0, new int[1], 0);
			String[] colors = { "255", "255", "255" };
			bird_brain.deserialize("AllTimeBest.txt");
			
			String previous = "";
			while (inputFile.hasNextLine()) {
				previous = inputFile.nextLine();
			}
			colors = previous.split(" ");
			if (colors.length != 3) {
				colors = new String[3];
				colors[0] = "255";
				colors[1] = "255";
				colors[2] = "255";
			}
			
			Bird parent = new Bird(WIDTH, HEIGHT, bird_brain, Double.parseDouble(colors[0]),
					Double.parseDouble(colors[1]), Double.parseDouble(colors[2]));
			if(population > 0) {
				birds.add(parent);
			}
			for(int i = 0; i < population; i++) {
				Bird child = new Bird((int) parent.WIDTH, (int) parent.HEIGHT, parent.brain, 
						parent.red, parent.green, parent.blue);
				child.brain.mutate(new Function("Gaussian Mutate", false), Math.random() * 0.6);
				child.red = Math.max(Math.min(255, child.red + 32 * Math.random() - 16), 0);
				child.green = Math.max(Math.min(255, child.green + 32 * Math.random() - 16), 0);
				child.blue = Math.max(Math.min(255, child.blue + 32 * Math.random() - 16), 0);
				birds.add(child);
			}
			
		} catch (IOException e) {
			// File is not found, just add in the default birds
			for (int i = 0; i < population; i++) {
				birds.add(new Bird(WIDTH, HEIGHT));
			}
		}

		this.pipes = new ArrayList<>();
//		pipes.add(new Pipe(WIDTH, HEIGHT));

		algo = new GeneticAlgo(population, WIDTH, HEIGHT, birds, savedBirds);

		pipesCrossed = 0;

		addKeyListener(this);
		new Window(WIDTH, HEIGHT, "Flappy Bird", this);
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

			for (Bird b : birds) {
				b.think(pipes);
				b.tick();
			}
			for (int i = pipes.size() - 1; i >= 0; i--) {
				pipes.get(i).tick();

				for (int j = birds.size() - 1; j >= 0; j--) {
					if (pipes.get(i).hits(birds.get(j))) {
						// Remove Bad Faster O(N) approach
						if (birds.size() < population / 2) {
							savedBirds.add(birds.get(j));
						}

						birds.remove(j);
					} else if (birds.get(j).outOfBounds()) {
						if (birds.size() < population / 2) {
							savedBirds.add(birds.get(j));
						}

						birds.remove(j);
					}
				}

				if (pipes.get(i).offscreen()) {
					pipes.remove(i);
				}
				if (pipes.get(i).x + pipes.get(i).w < 50 && !pipes.get(i).passed) {
					pipesCrossed++;
					pipes.get(i).passed = true;
				}
			}
			if (birds.size() == 0) {
				algo.setPipesCrossed(pipesCrossed);
				algo.nextGeneration();
				pipes.clear();
				frameCount = 0;
				pipesCrossed = 0;
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

		for (Bird b : birds) {
			b.render(g);
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
		new FlappyBird();
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
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			Bird bird = birds.get(0);
			try {
				bird.brain.serialize("FirstBird.txt");
				PrintWriter pw = new PrintWriter(new FileWriter("FirstBird.txt", true));
				pw.println(bird.red + " " + bird.green + " " + bird.blue);
				pw.close();
				System.out.println("Saved First Bird");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_C) {
			System.out.println(algo.allTimeBest + " " + birds.get(0).score);
		} else if (e.getKeyCode() == KeyEvent.VK_I) {
			System.out.println(birds.size());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
