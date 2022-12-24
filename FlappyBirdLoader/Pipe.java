package FlappyBirdLoader;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Pipe {
	final double spacing = 175;
	double top;
	double bottom;
	double x;
	final double w = 80;
	final double speed = 5;
	boolean passed;
	
	int WIDTH;
	int HEIGHT;
	
	public Pipe(int WIDTH, int HEIGHT) {
		Random rand = new Random();
		this.top = rand.nextInt((int)(HEIGHT * 7.0 / 12)) + HEIGHT / 6;
		this.bottom = top + spacing;
		
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		x = WIDTH;
		
		passed = false;
	}
	
	public void tick() {
		x -= speed;
	}
	
	public void render(Graphics g) {
		g.setColor(new Color(255, 255, 255));
		g.fillRect((int)x, 0, (int)w, (int)top);
		g.fillRect((int)x, (int)(bottom), (int)w, (int)(HEIGHT - bottom));
	}
	
	public boolean offscreen() {
		return x < -w;
	}
	
	public void pass(Bird b) {
		if(b.x > this.x && !passed) {
			passed = true;
		}
	}
	
	public boolean hits(Bird b) {
//		if((b.x >= x && b.x <= x - w)) {
//			System.out.println((b.x >= x && b.x <= x + w));
//		}
		
		return ((b.y <= top + b.hitHeight / 2 || b.y >= bottom - b.hitHeight / 2) 
				&& (b.x + b.hitWidth / 2 >= x && b.x - b.hitWidth / 2 <= x + w));
	}
}
