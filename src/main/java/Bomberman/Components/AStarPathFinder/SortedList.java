package Bomberman.Components.AStarPathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

class SortedList<T extends Comparable> {
  private PriorityQueue<T> queue = new PriorityQueue<>();

  public T first() {
    return queue.peek();
  }
  public void clear() {
    queue.clear();
  }
  public void add(T o) {
    queue.add(o);
  }

  public void remove(T o) {
    queue.poll();
  }

  public int size() {
    return queue.size();
  }

  public boolean contains(T o) {
    return queue.contains(o);
  }
}