package Bomberman.Utils;

import static Bomberman.Constants.Constant.TILED_SIZE;

import javafx.geometry.Point2D;

public class Utils {
  public static Point2D rearrange(Point2D pos) {
    int locationX = (int) (pos.getX() % TILED_SIZE > TILED_SIZE / 2
        ? pos.getX() + TILED_SIZE - pos.getX() % TILED_SIZE
        : pos.getX() - pos.getX() % TILED_SIZE);
    int locationY = (int) (pos.getY() % TILED_SIZE > TILED_SIZE / 2
        ? pos.getY() + TILED_SIZE - pos.getY() % TILED_SIZE
        : pos.getY() - pos.getY() % TILED_SIZE);
    return new Point2D(locationX, locationY);
  }
}
