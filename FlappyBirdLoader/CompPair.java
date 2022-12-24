package FlappyBirdLoader;

import java.util.Comparator;

public class CompPair implements Comparator<Pair>{
	public int compare(Pair x, Pair y) {
		return Integer.compare(x.first, y.first);
	}
}
