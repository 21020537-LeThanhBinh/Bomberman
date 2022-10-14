package Bomberman.Components.Bomb;

import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import Bomberman.DynamicEntityState.State;
import com.almasb.fxgl.entity.SpawnData;

public class LazerBomb extends BombComponent{
  @Override
  public void explode(int flames) {
    spawn("central_flame", new SpawnData(entity.getX(), entity.getY(), 1));
    entity.removeFromWorld();
  }

  public void explode(int flames, State state) {
    spawn("central_flame", new SpawnData(entity.getX(), entity.getY(), 1));

    switch (state) {
      case UP:
        for (int i = 1; i < flames; i++) {
          spawn("up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 1));
        }
        spawn("top_up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 1));
        break;
      case RIGHT:
        for (int i = 1; i < flames; i++) {
          spawn("right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 1));
        }
        spawn("top_right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 1));
        break;
      case DOWN:
        for (int i = 1; i < flames; i++) {
          spawn("down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 1));
        }
        spawn("top_down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 1));
        break;
      case LEFT:
        for (int i = 1; i < flames; i++) {
          spawn("left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 1));
        }
        spawn("top_left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 1));
        break;
      case STOP:
      case DIE:
        break;
    }
    entity.removeFromWorld();
  }
}
