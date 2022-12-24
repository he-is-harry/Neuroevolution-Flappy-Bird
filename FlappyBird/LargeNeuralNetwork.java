package FlappyBird;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class LargeNeuralNetwork {
	int input_nodes;
	int [] hidden_nodes_list;
	int output_nodes;
	
	Matrix [] weights;
	Matrix [] bias;
	
	double learningRate;
	
	Function activation_function;
	
	public LargeNeuralNetwork(int input_nodes, int [] hidden_nodes_list, int output_nodes) {
		this.input_nodes = input_nodes;
		this.hidden_nodes_list = hidden_nodes_list;
		this.output_nodes = output_nodes;
		
		weights = new Matrix[this.hidden_nodes_list.length + 1];
		weights[0] = new Matrix(this.hidden_nodes_list[0], this.input_nodes);
		for(int i = 0; i + 1 < this.hidden_nodes_list.length; i++) {
			weights[i + 1] = new Matrix(this.hidden_nodes_list[i + 1], this.hidden_nodes_list[i]);
		}
		weights[this.weights.length - 1] = 
				new Matrix(this.output_nodes, this.hidden_nodes_list[this.hidden_nodes_list.length - 1]);
		for(int i = 0; i < weights.length; i++) {
			weights[i].randomize();
		}
		
		bias = new Matrix[this.hidden_nodes_list.length + 1];
		bias[0] = new Matrix(this.hidden_nodes_list[0], 1);
		for(int i = 0; i + 1 < this.hidden_nodes_list.length; i++) {
			bias[i + 1] = new Matrix(this.hidden_nodes_list[i + 1], 1);
		}
		bias[this.bias.length - 1] = 
				new Matrix(this.output_nodes, 1);
		for(int i = 0; i < bias.length; i++) {
			bias[i].randomize();
		}
		
		this.learningRate = 0.1;
		this.setActivationFunction("Sigmoid");
	}
	
	public LargeNeuralNetwork(LargeNeuralNetwork nn) {
		this.input_nodes = nn.input_nodes;
		this.hidden_nodes_list = nn.hidden_nodes_list;
		this.output_nodes = nn.output_nodes;
		this.weights = new Matrix[nn.weights.length];
		for(int i = 0; i < nn.weights.length; i++) {
			this.weights[i] = nn.weights[i].copy();
		}
		this.bias = new Matrix[nn.bias.length];
		for(int i = 0; i < nn.bias.length; i++) {
			this.bias[i] = nn.bias[i].copy();
		}
		this.learningRate = nn.learningRate;
		this.activation_function = nn.activation_function;
	}
	
	public LargeNeuralNetwork(NeuralNetwork nn) {
		this.input_nodes = nn.input_nodes;
		this.hidden_nodes_list = new int[1];
		this.hidden_nodes_list[0] = nn.hidden_nodes;
		this.output_nodes = nn.output_nodes;
		this.weights = new Matrix[this.hidden_nodes_list.length + 1];
		this.weights[0] = nn.weights_ih;
		this.weights[1] = nn.weights_ho;
		this.bias = new Matrix[this.hidden_nodes_list.length + 1];
		this.bias[0] = nn.bias_h;
		this.bias[1] = nn.bias_o;
		this.learningRate = nn.learningRate;
		this.activation_function = nn.activation_function;
	}
	
	public LargeNeuralNetwork(int input_nodes, int [] hidden_nodes_list, int output_nodes, 
			Matrix [] weights, Matrix [] bias) {
		this.input_nodes = input_nodes;
		this.hidden_nodes_list = hidden_nodes_list;
		this.output_nodes = output_nodes;
		
		this.weights = weights;
		this.bias = bias;
		
		learningRate = 0.1;
		setActivationFunction("Sigmoid");
	}
	
	double [] predict(Matrix input) throws Exception {
		Matrix cur = input;
		for(int i = 0; i < weights.length; i++) {
			Matrix next = Matrix.multiply(weights[i], cur);
			next.add(bias[i]);
			next.map(activation_function);
			cur = next;
		}
		
		return cur.toArray();
	}
	
	double [] predict(double [] input_array) throws Exception {
		Matrix cur = Matrix.fromArray(input_array);
		for(int i = 0; i < weights.length; i++) {
			Matrix next = Matrix.multiply(weights[i], cur);
			next.add(bias[i]);
			next.map(activation_function);
			cur = next;
		}
		
		return cur.toArray();
	}
	
	void mutate(Function e, double rate) {
		for(int i = 0; i < weights.length; i++) {
			weights[i].map(e, rate);
			bias[i].map(e, rate);
		}
	}
	
	LargeNeuralNetwork copy() {
		return new LargeNeuralNetwork(this);
	}
	
	static LargeNeuralNetwork crossover(LargeNeuralNetwork nn1, LargeNeuralNetwork nn2) throws Exception {
		boolean good = true;
		if(nn1.input_nodes != nn2.input_nodes) {
			good = false;
		} else if(nn1.output_nodes != nn2.output_nodes) {
			good = false;
		} else if(nn1.hidden_nodes_list.length != nn2.hidden_nodes_list.length) {
			good = false;
		} else {
			for(int i = 0; i < nn1.hidden_nodes_list.length; i++) {
				if(nn1.hidden_nodes_list[i] != nn2.hidden_nodes_list[i]) {
					good = false;
					break;
				}
			}
		}
		if(!good) {
			System.out.println("Error: Numbers of nodes in nn1 and nn2 must match");
			throw new Exception("Network Crossover Error");
		}
		Matrix [] weights_cross = new Matrix[nn1.weights.length];
		Matrix [] bias_cross = new Matrix[nn1.bias.length];
		for(int i = 0; i < weights_cross.length; i++) {
			weights_cross[i] = Matrix.crossover(nn1.weights[i], nn2.weights[i]);
			bias_cross[i] = Matrix.crossover(nn1.bias[i], nn2.bias[i]);
		}
		return new LargeNeuralNetwork(nn1.input_nodes, nn1.hidden_nodes_list, nn1.output_nodes, 
				weights_cross, bias_cross);
	}
	
	void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	
	void setActivationFunction(String type) {
		activation_function = new Function(type, false);
	}
	
	void serialize(String name) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(name));
		pw.println(input_nodes);
		pw.println(hidden_nodes_list.length);
		for(int i = 0; i < hidden_nodes_list.length; i++) {
			if(i == hidden_nodes_list.length - 1) {
				pw.println(hidden_nodes_list[i]);
			} else {
				pw.print(hidden_nodes_list[i] + " ");
			}
		}
		pw.println(output_nodes);
		pw.println(learningRate);
		pw.println(activation_function.strType);
		for(int i = 0; i < weights.length; i++) {
			weights[i].serialize(pw);
		}
		for(int i = 0; i < bias.length; i++) {
			bias[i].serialize(pw);
		}
		pw.close();
	}
	
	void deserialize(String name) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
		StringTokenizer st;
		st = new StringTokenizer(br.readLine().trim());
		input_nodes = Integer.parseInt(st.nextToken());
		
		st = new StringTokenizer(br.readLine().trim());
		int numHiddenNodes = Integer.parseInt(st.nextToken());
		hidden_nodes_list = new int[numHiddenNodes];
		st = new StringTokenizer(br.readLine().trim());
		for(int i = 0; i < numHiddenNodes; i++) {
			hidden_nodes_list[i] = Integer.parseInt(st.nextToken());
		}
				
		st = new StringTokenizer(br.readLine().trim());
		output_nodes = Integer.parseInt(st.nextToken());
		st = new StringTokenizer(br.readLine().trim());
		learningRate = Double.parseDouble(st.nextToken());
		String strType = br.readLine().trim();
		activation_function = new Function(strType, false);
		
		weights = new Matrix[numHiddenNodes + 1];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = new Matrix(0, 0);
			weights[i].deserialize(br, st);
		}
		bias = new Matrix[numHiddenNodes + 1];
		for(int i = 0; i < weights.length; i++) {
			bias[i] = new Matrix(0, 0);
			bias[i].deserialize(br, st);
		}
	}
}
