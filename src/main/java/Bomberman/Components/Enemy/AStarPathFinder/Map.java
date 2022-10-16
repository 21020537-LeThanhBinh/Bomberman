package Bomberman.Components.Enemy.AStarPathFinder;

public class Map {
  private int[][] binaryMap;
  private int width;
  private int height;

  public Map(int width, int height) {
    this.binaryMap = new int[height][width];
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
  public int getVal(int x, int y) {
    return binaryMap[y][x];
  }

  public void setVal(int x, int y, int val) {
    binaryMap[y][x] = val;
  }
}
