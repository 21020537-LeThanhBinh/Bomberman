package Bomberman.Components.Bomb;

import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.PLAYER;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static Bomberman.DynamicEntityState.State.STOP;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.onCollision;
import static com.almasb.fxgl.dsl.FXGL.onCollisionEnd;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import Bomberman.Components.PlayerComponent;
import Bomberman.DynamicEntityState.State;
import com.almasb.fxgl.entity.SpawnData;

public class LazerBomb extends BombComponent{
  private State direction;
  public LazerBomb(int direction) {
    onCollision(BOMB, FLAME, (bomb, flame) -> {
      if (bomb != null) bomb.getComponent(LazerBomb.class).explode();
    });

    // Stop player from moving in bomb's pos
    onCollisionEnd(PLAYER, BOMB, (player, bomb) -> {
      if (physic_block == null) physic_block = spawn("physic_block", bomb.getX(), bomb.getY());
    });

    this.direction = State.valueOf(direction);
  }

  @Override
  public void explode() {
    super.explode();

    spawn("central_flame", new SpawnData(entity.getX(), entity.getY(), 1));
    switch (direction) {
      case UP:
        spawn("top_up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 1));
        for (int i = 1; i <= flames; i++) {
          spawn("up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 1));
        }
        break;
      case RIGHT:
        spawn("top_right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 1));
        for (int i = 1; i <= flames; i++) {
          spawn("right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 1));
        }
        break;
      case DOWN:
        spawn("top_down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 1));
        for (int i = 1; i <= flames; i++) {
          spawn("down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 1));
        }
        break;
      case LEFT:
        spawn("top_left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 1));
        for (int i = 1; i <= flames; i++) {
          spawn("left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 1));
        }
        break;
    }
  }
}
