package Bomberman.Components.Bomb;

import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.PLAYER;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.inc;
import static com.almasb.fxgl.dsl.FXGL.onCollision;
import static com.almasb.fxgl.dsl.FXGL.onCollisionEnd;
import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import com.almasb.fxgl.entity.SpawnData;
import javafx.util.Duration;

public class ClassicBomb extends BombComponent{
  public ClassicBomb() {
    onCollision(BOMB, FLAME, (bomb, flame) -> {
      if (bomb != null) bomb.getComponent(ClassicBomb.class).explode();
    });
  }

  @Override
  public void explode() {
    super.explode();

    spawn("central_flame", new SpawnData(entity.getX(), entity.getY(), 0));
    spawn("top_down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 0));
    spawn("top_right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 0));
    spawn("top_left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 0));
    for (int i = 1; i < flames; i++) {
      spawn("right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 0));
      spawn("left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 0));
      spawn("down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 0));
      spawn("up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 0));
    }
    spawn("top_up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 0));
  }
}
