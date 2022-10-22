package Bomberman.Components.AStarPathFinder;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class AStarPathFinder {
	private class Node implements Comparable {
		private final int x;
		private final int y;
		private float cost;
		private Node parent;
		private float heuristic;

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
	private final ArrayList<Node> closed = new ArrayList<>();
	private final PriorityQueue<Node> open = new PriorityQueue<>();
	private final Map map;
	private final Node[][] nodes;

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
	 * Shortest path from start (s) to target (t).
	 */
	public Path findPath(int sx, int sy, int tx, int ty) {
		if (!isValidLocation(tx, ty)) {
			return null;
		}
		
		nodes[sx][sy].cost = 0;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);
		
		nodes[tx][ty].parent = null;
		
		while (open.size() > 0) {
			Node current = open.poll();
			if (current == nodes[tx][ty]) {
				break;
			}

			closed.add(current);

			int[] arrX = {0, 0, 1, -1};
			int[] arrY = {1, -1, 0, 0};
			for (int i = 0; i < 4; i++) {
				int xp = arrX[i] + current.x;
				int yp = arrY[i] + current.y;

				if (!isValidLocation(xp,yp)) {
					continue;
				}

				Node neighbour = nodes[xp][yp];
				float nextStepCost = current.cost + 1;

				if (open.contains(neighbour)) {
					if (nextStepCost > neighbour.cost) {
						continue;
					}
				}
				if (closed.contains(neighbour)) {
					if (nextStepCost < neighbour.cost) {
						closed.remove(neighbour);

						neighbour.cost = nextStepCost;
						neighbour.setParent(current);
						open.add(neighbour);
					}
				} else {
					neighbour.cost = nextStepCost;
					neighbour.heuristic = getHeuristicCost(xp, yp, tx, ty);
					neighbour.setParent(current);
					open.add(neighbour);
				}
			}
		}

		if (nodes[tx][ty].parent == null) {
			return null;
		}
		
		Path path = new Path();
		Node target = nodes[tx][ty];
		while (target != nodes[sx][sy]) {
			path.prependStep(target.x, target.y);
			target = target.parent;
		}

		return path;
	}

	protected boolean isValidLocation(int x, int y) {
		boolean invalid = (x < 0) || (y < 0) || (x >= map.getWidth()) || (y >= map.getHeight()) || (map.getVal(x, y) == 0);
		return !invalid;
	}
	
	/**
	 * Manhattan heuristic cost.
	 */
	public float getHeuristicCost(int x, int y, int tx, int ty) {
		return (Math.abs(x-tx) + Math.abs(y-ty));
	}
}
