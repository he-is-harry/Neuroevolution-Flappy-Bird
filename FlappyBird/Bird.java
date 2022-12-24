package FlappyBird;

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
		int [] hidden_layers = {8, 8, 8};
		this.brain = new LargeNeuralNetwork(5, hidden_layers, 2);
		this.red = Math.random() * 256;
		this.green = Math.random() * 256;
		this.blue = Math.random() * 256;
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
	
	public void think(ArrayList<Pipe> pipes) {
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
}
