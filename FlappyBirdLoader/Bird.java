package FlappyBirdLoader;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Bird {
	double HEIGHT;
	double WIDTH;
	double y;
	double lift = -15;
	double x = 50;
	
	double gravity = 1.2;
	double velocity = 0;
	
	final double hitHeight = 32;
	final double hitWidth = 32;
	
	double score;
	double fitness;
	LargeNeuralNetwork brain;
	
	double red, green, blue;
	
	public Bird(int WIDTH, int HEIGHT) {
		this.y = HEIGHT / 2.0;
		this.HEIGHT = HEIGHT;
		this.WIDTH = WIDTH;
		this.score = 0;
		this.fitness = 0;
		this.brain = new LargeNeuralNetwork(new NeuralNetwork(5, 8, 2));
		this.red = Math.random() * 256;
		this.green = Math.random() * 256;
		this.blue = Math.random() * 256;
	}
	
	public Bird(int WIDTH, int HEIGHT, NeuralNetwork brain, double red, double green, double blue) {
		this.y = HEIGHT / 2.0;
		this.HEIGHT = HEIGHT;
		this.WIDTH = WIDTH;
		this.score = 0;
		this.fitness = 0;
		this.brain = new LargeNeuralNetwork(brain.copy());
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Bird(int WIDTH, int HEIGHT, LargeNeuralNetwork brain, double red, double green, double blue) {
		this.y = HEIGHT / 2.0;
		this.HEIGHT = HEIGHT;
		this.WIDTH = WIDTH;
		this.score = 0;
		this.fitness = 0;
		this.brain = brain.copy();
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Bird(int WIDTH, int HEIGHT, boolean noAI) {
		if(noAI) {
			this.y = HEIGHT / 2.0;
			this.HEIGHT = HEIGHT;
			this.WIDTH = WIDTH;
			this.score = 0;
			this.fitness = 0;
			this.brain = null;
			this.red = 255;
			this.green = 117;
			this.blue = 244;
		} else {
			this.y = HEIGHT / 2.0;
			this.HEIGHT = HEIGHT;
			this.WIDTH = WIDTH;
			this.score = 0;
			this.fitness = 0;
			this.brain = new LargeNeuralNetwork(new NeuralNetwork(5, 8, 2));
			this.red = Math.random() * 256;
			this.green = Math.random() * 256;
			this.blue = Math.random() * 256;
		}
	}
	
//	public Bird(int WIDTH, int HEIGHT, NeuralNetwork brain) {
//		this.y = HEIGHT / 2.0;
//		this.HEIGHT = HEIGHT;
//		this.WIDTH = WIDTH;
//		this.score = 0;
//		this.fitness = 0;
//		this.brain = new LargeNeuralNetwork(brain.copy());
//		this.red = Math.random() * 256;
//		this.green = Math.random() * 256;
//		this.blue = Math.random() * 256;
//	}
	
	public void think(ArrayList<Pipe> pipes) {
		if(brain == null) {
			Pipe closest = null;
			double closestD = Double.MAX_VALUE;
			for(int i = 0; i < pipes.size(); i++) {
				double d = (pipes.get(i).x + pipes.get(i).w + hitWidth / 2 + 4) - x;
				if(d < closestD && d > 0) {
					closest = pipes.get(i);
					closestD = d;
				}
			}
			
			double safety = velocity > 0 ? 2 * velocity / (-lift) : 0;
			if((closest.bottom - 15 <= y + safety + hitHeight / 2 || 
					y + safety + 30 >= HEIGHT - hitWidth / 2) && velocity >= -5) {
				up();
			}
			return ;
		}
		
		Pipe closest = null;
		double closestD = Double.MAX_VALUE;
		for(int i = 0; i < pipes.size(); i++) {
			double d = (pipes.get(i).x + pipes.get(i).w) - x;
			if(d < closestD && d > 0) {
				closest = pipes.get(i);
				closestD = d;
			}
		}

		
		double [] inputs = new double[5];
		inputs[0] = y / HEIGHT;
		inputs[1] = closest.top / HEIGHT;
		inputs[2] = closest.bottom / HEIGHT;
		inputs[3] = closest.x / WIDTH;
		inputs[4] = velocity / 10;
		try {
			double [] output = brain.predict(inputs);
			if(output[0] > output[1]) {
				up();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void up() {
		this.velocity = lift;
	}
	
	public boolean outOfBounds() {
		return y + hitHeight / 2 > HEIGHT || y - hitWidth < 0;
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2F));
        g.setColor(new Color((int)red, (int)green, (int)blue));
        g.fillOval((int)(this.x - hitWidth / 2), (int)(this.y - hitHeight / 2),
				(int)hitWidth, (int)hitHeight);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		g.drawOval((int)(this.x - hitWidth / 2), (int)(this.y - hitHeight / 2),
				(int)hitWidth, (int)hitHeight);
		
	}
	
	public void tick() {
		score++;
		velocity += gravity;
		y += velocity;
	}
	
	public void reset() {
		this.y = HEIGHT / 2.0;
		this.velocity = 0;
		this.score = 0;
	}
}
