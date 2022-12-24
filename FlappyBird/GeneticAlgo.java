package FlappyBird;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class GeneticAlgo {
	public int WIDTH, HEIGHT;
	public int population;
	public int gen;
	public double allTimeBest;
	public int pipesCrossed;
	ArrayList<Bird> birds;
	ArrayList<Bird> savedBirds;
	private Random rand;
	private Bird champ;

	public class BirdComparator implements Comparator<Bird> {
		public int compare(Bird o1, Bird o2) {
			return -Double.compare(o1.score, o2.score);
		}
	}

	public GeneticAlgo(int population, int WIDTH, int HEIGHT, ArrayList<Bird> birds, ArrayList<Bird> savedBirds) {
		this.population = population;
		this.gen = 0;
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.birds = birds;
		this.savedBirds = savedBirds;
		this.rand = new Random();
		
		try {
			Scanner inputFile = new Scanner(new File("BestScore.txt"));
			this.allTimeBest = inputFile.nextDouble();
		} catch (FileNotFoundException e) {
			this.allTimeBest = 0;
		}
	}

	public void nextGeneration() {
//		removeBad();
		gen++;
		calculateFitnessSquare();
		champ = getChamp();
//		if(gen % 20 == 0) saveChamp("FlappyChamp.txt");
		checkBest();
		birds.add(champ);
		// Insert rest of birds with mutation
		for(int i = 1; i < population; i++) {
//			Bird parent1 = pickOne();
//			Bird parent2 = pickOne();
//			double childRed = Math.random() < 0.5 ? parent1.red : parent2.red;
//			double childGreen = Math.random() < 0.5 ? parent1.green : parent2.green;
//			double childBlue = Math.random() < 0.5 ? parent1.blue : parent2.blue;
//			Bird child;
//			try {
//				child = new Bird(WIDTH, HEIGHT, NeuralNetwork.crossover(parent1.brain, parent2.brain),
//						childRed, childGreen, childBlue);
//				mutateBird(child, 0.4);
//				birds.add(child);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			Bird child = pickOne();
			mutateBird(child, 0.1);
			birds.add(child);
		}
		savedBirds.clear();
	}

	public void removeBad() {
		// Removes the lower half of the birds
		// so to reduce the amount of birds that
		// just last the first pipe to reproduce
		savedBirds.sort(new BirdComparator());
		for (int i = savedBirds.size() - 1; i >= population / 2; i--) {
			savedBirds.remove(i);
		}
	}
	
	public void checkBest() {
		Bird newBest = null;
		String path = "HallOfFame/L_V3_" + pipesCrossed + ".txt";
		for (int i = 0; i < savedBirds.size(); i++) {
			if (savedBirds.get(i).score > allTimeBest) {
				allTimeBest = savedBirds.get(i).score;
				newBest = savedBirds.get(i);
			}
		}
		if(newBest != null) {
			try {
				newBest.brain.serialize(path);
				PrintWriter pw = new PrintWriter(new FileWriter(path, true));
				pw.println(newBest.red + " " + newBest.green + " " + newBest.blue);
				pw.close();
				newBest.brain.serialize("AllTimeBest.txt");
				PrintWriter bestBirdWriter = new PrintWriter(new FileWriter("AllTimeBest.txt", true));
				bestBirdWriter.println(newBest.red + " " + newBest.green + " " + newBest.blue);
				bestBirdWriter.close();
				PrintWriter bestScoreWriter = new PrintWriter(new FileWriter("BestScore.txt"));
				bestScoreWriter.println(allTimeBest);
				bestScoreWriter.println(pipesCrossed);
				bestScoreWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Bird getChamp() {
		double max = 0;
		int maxIndex = -1;
		for (int i = 0; i < savedBirds.size(); i++) {
			if (savedBirds.get(i).fitness > max) {
				max = savedBirds.get(i).fitness;
				maxIndex = i;
			}
		}
		if (maxIndex == -1)
			return new Bird(WIDTH, HEIGHT);
		// Reset Champ Scores
		savedBirds.get(maxIndex).score = 0;
		return savedBirds.get(maxIndex);
	}

	public void saveChamp(String path) {
		try {
			champ.brain.serialize(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bird pickOne() {
		int index = 0;
		double r = Math.random();
		while (r > 0) {
			r = r - savedBirds.get(index).fitness;
		}
		index--;
		Bird parent = savedBirds.get(rand.nextInt(savedBirds.size()));
		Bird child = new Bird((int) parent.WIDTH, (int) parent.HEIGHT, parent.brain, 
				parent.red, parent.green, parent.blue);
//		child.brain.mutate(new Function("Gaussian Mutate", false), 0.1);
//		child.red = Math.max(Math.min(255, child.red + 4 * Math.random() - 2), 0);
//		child.green = Math.max(Math.min(255, child.green + 4 * Math.random() - 2), 0);
//		child.blue = Math.max(Math.min(255, child.blue + 4 * Math.random() - 2), 0);
		return child;
	}
	
	public void mutateBird(Bird b, double mutationRate) {
		b.brain.mutate(new Function("Gaussian Mutate", false), mutationRate);
		b.red = Math.max(Math.min(255, b.red + 4 * Math.random() - 2), 0);
		b.green = Math.max(Math.min(255, b.green + 4 * Math.random() - 2), 0);
		b.blue = Math.max(Math.min(255, b.blue + 4 * Math.random() - 2), 0);
	}

	public void calculateFitness() {
		double sum = 0;
		for (Bird b : savedBirds) {
			sum += b.score;
		}

		for (Bird b : savedBirds) {
			b.fitness = b.score / sum;
		}
	}

	public void calculateFitnessSquare() {
		double sum = 0;
		for (Bird b : savedBirds) {
			sum += b.score * b.score;
		}
		for (Bird b : savedBirds) {
			b.fitness = (b.score * b.score) / sum;
		}
	}

	public void calculateFitnessNatural() {
		double sum = 0;
		for (Bird b : savedBirds) {
			sum += Math.exp(b.score / 100);
		}
		for (Bird b : savedBirds) {
			b.fitness = Math.exp(b.score / 100) / sum;
		}
	}
	
	public void setPipesCrossed(int pipesCrossed) {
		this.pipesCrossed = pipesCrossed;
	}
}
