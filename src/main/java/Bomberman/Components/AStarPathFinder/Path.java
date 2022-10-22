package Bomberman.Components.AStarPathFinder;

import java.util.ArrayList;
import javafx.util.Pair;

public class Path {
	private final ArrayList<Pair<Integer, Integer>> steps = new ArrayList<>();

	public Path() {
		
	}

	public int getLength() {
		return steps.size();
	}
	
	public Pair<Integer, Integer> getStep(int index) {
		return steps.get(index);
	}
	
	public int getX(int index) {
		return getStep(index).getKey();
	}

	public int getY(int index) {
		return getStep(index).getValue();
	}
	
	public void appendStep(int x, int y) {
		steps.add(new Pair<>(x,y));
	}

	public void prependStep(int x, int y) {
		steps.add(0, new Pair<>(x,y));
	}
	
	public boolean contains(int x, int y) {
		return steps.contains(new Pair<>(x,y));
	}
	
}
