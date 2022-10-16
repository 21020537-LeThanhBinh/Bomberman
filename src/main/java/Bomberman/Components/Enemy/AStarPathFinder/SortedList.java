package Bomberman.Components.Enemy.AStarPathFinder;

import java.util.ArrayList;
import java.util.Collections;

class SortedList<T extends Comparable> {
  private ArrayList<T> list = new ArrayList<>();

  public T first() {
    return list.get(0);
  }
  public void clear() {
    list.clear();
  }
  public void add(T o) {
    list.add(o);
    Collections.sort(list);
  }

  public void remove(T o) {
    list.remove(o);
  }

  public int size() {
    return list.size();
  }

  public boolean contains(T o) {
    return list.contains(o);
  }
}