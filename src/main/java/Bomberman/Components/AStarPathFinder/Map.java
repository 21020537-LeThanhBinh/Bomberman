package Bomberman.Components.AStarPathFinder;

public class Map {
  private final int[][] binaryMap;
  private final int width;
  private final int height;

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
    if (!isValidLocation(x, y)) return 0;
    return binaryMap[y][x];
  }

  public void setVal(int x, int y, int val) {
    if (!isValidLocation(x, y)) return;
    binaryMap[y][x] = val;
  }

  protected boolean isValidLocation(int x, int y) {
    return !((x < 0) || (y < 0) || (x >= width) || (y >= height));
  }
}
