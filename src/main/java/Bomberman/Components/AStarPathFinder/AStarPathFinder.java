package Bomberman.Components.AStarPathFinder;

import java.util.ArrayList;

public class AStarPathFinder {
	private class Node implements Comparable {
		private int x;
		private int y;
		private float cost;
		private Node parent;
		private float heuristic;
		private int depth;

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public int compareTo(Object other) {
			Node o = (Node) other;

			float f = heuristic + cost;
			float of = o.heuristic + o.cost;

			return Float.compare(f, of);
		}
	}
	private ArrayList<Node> closed = new ArrayList<>();
	private SortedList<Node> open = new SortedList<>();
	private Map map;
	private Node[][] nodes;

	public AStarPathFinder(Map map) {
		this.map = map;

		nodes = new Node[map.getWidth()][map.getHeight()];
		for (int x=0;x<map.getWidth();x++) {
			for (int y=0;y<map.getHeight();y++) {
				nodes[x][y] = new Node(x,y);
			}
		}
	}
	
	/**
	 * Shortest path from s to t.
	 */
	public Path findPath(int sx, int sy, int tx, int ty) {
		// The target is blocked (== 0)
		if (!isValidLocation(sx, sy, tx, ty) || map.getVal(tx, ty) != 1) {
			return null;
		}
		
		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);
		
		nodes[tx][ty].parent = null;
		
		while (open.size() != 0) {
			Node current = open.first();
//			System.out.println("Current at " + current.x + "-" + current.y);
			if (current == nodes[tx][ty]) {
//				System.out.println("Found the path");
				break;
			}

			open.remove(current);
			closed.add(current);

			int[] arrX = {0, 0, 1, -1};
			int[] arrY = {1, -1, 0, 0};
			for (int i = 0; i < 4; i++) {
				int xp = arrX[i] + current.x;
				int yp = arrY[i] + current.y;

				if (isValidLocation(sx,sy,xp,yp)) {
					float nextStepCost = current.cost + 1;
					Node neighbour = nodes[xp][yp];

					if (nextStepCost < neighbour.cost) {
						if (open.contains(neighbour)) {
							open.remove(neighbour);
						}
					}

					if (!open.contains(neighbour) && !(closed.contains(neighbour))) {
						neighbour.cost = nextStepCost;
						neighbour.heuristic = getHeuristicCost(xp, yp, tx, ty);
						neighbour.setParent(current);
						open.add(neighbour);
					}
				}
			}
		}

		if (nodes[tx][ty].parent == null) {
//			System.out.println("Path not exist!");
			return null;
		}
		
		Path path = new Path();
		Node target = nodes[tx][ty];
		while (target != nodes[sx][sy]) {
			path.prependStep(target.x, target.y);
			target = target.parent;
		}
//		path.prependStep(sx,sy);
		
		return path;
	}

	protected boolean isValidLocation(int sx, int sy, int x, int y) {
		boolean invalid = (x < 0) || (y < 0) || (x >= map.getWidth()) || (y >= map.getHeight());
		
		if ((!invalid) && ((sx != x) || (sy != y))) {
			invalid = (map.getVal(x, y) != 1);
		}
		
		return !invalid;
	}
	
	/**
	 * Manhattan heuristic cost.
	 */
	public float getHeuristicCost(int x, int y, int tx, int ty) {
		return (Math.abs(x-tx) + Math.abs(y-ty));
	}
}
