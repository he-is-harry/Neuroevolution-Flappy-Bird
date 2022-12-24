package FlappyBirdLoader;

import java.util.Random;

public class Function {
	String strType;
	int type;
	// 0 - Sigmoid
	// 1 - Hyperbolic Tangent
	// 2 - Default Mutate
	// 3 - Mutate with Gaussian Distribution, standard deviation of 0.1
	// Add MORE HERE
	boolean derivative;
	Random rand;
	public Function(String type, boolean derivative) {
		this.strType = type;
		this.type = -1;
		if(type.equalsIgnoreCase("Sigmoid") || type.equals("Sigma") || 
				type.equalsIgnoreCase("s")) {
			this.type = 0;
		} else if(type.equalsIgnoreCase("Hyperbolic Tangent") || type.equalsIgnoreCase("tanh")
				|| type.equalsIgnoreCase("th") || type.equalsIgnoreCase("Hyper Tan")) {
			this.type = 1;
		} else if(type.equalsIgnoreCase("Default Mutate") || type.equalsIgnoreCase("dMut")
				|| type.equalsIgnoreCase("dm") || type.equalsIgnoreCase("Random Mutate")) {
			this.type = 2;
		} else if(type.equalsIgnoreCase("Gaussian Mutate") || type.equalsIgnoreCase("gMut")
				|| type.equalsIgnoreCase("gm")) {
			this.type = 3;
		} else {
			System.out.println("!!! No function of type \"" + type + "\" found");
		}
		this.derivative = derivative;
		
		rand = new Random();
	}
	
	double f(double x) {
		if(type == 0) {
			if(derivative) {
				return dsigmoid(x);
			} else {
				return sigmoid(x);
			}
		} else if(type == 1) {
			if(derivative) {
				return tanh(x);
			} else {
				return dtanh(x);
			}
		}
		return 0x3f3f3f;
	}
	
	double f(double x, double i, double j) {
		if(type == 0) {
			if(derivative) {
				return dsigmoid(x);
			} else {
				return sigmoid(x);
			}
		} else if(type == 1) {
			if(derivative) {
				return tanh(x);
			} else {
				return dtanh(x);
			}
		}
		return 0x3f3f3f;
	}
	
	double f(double x, double rate) {
		if(type == 2) {
			if(derivative) {
				
			} else {
				return defaultMutate(x, rate);
			}
		} else if(type == 3) {
			if(derivative) {
				
			} else {
				return gaussianMutate(x, rate);
			}
		}
		System.out.println("Function Does Not Match Parameters");
		return 0x3f3f3f;
	}
	
	double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x));
	}
	
	double dsigmoid(double y) {
		return y * (1 - y);
	}
	
	double tanh(double x) {
		return Math.tanh(x);
	}
	
	double dtanh(double y) {
		return 1 - (y * y);
	}
	
	double gaussianMutate(double x, double rate) {
		if(Math.random() < rate) {
			return x + rand.nextGaussian() * 0.1;
		}
		return x;
	}
	
	double defaultMutate(double x, double rate) {
		if(Math.random() < rate) {
			return Math.random() * 2 - 1;
		}
		return x;
	}
}
