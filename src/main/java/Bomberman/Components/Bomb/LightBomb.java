package Bomberman.Components.Bomb;

import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.ENEMY1;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.PHYSIC_BLOCK;
import static Bomberman.BombermanType.PLAYER;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.onCollision;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onCollisionEnd;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import Bomberman.Utils.Utils;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.PhysicsComponent;

public class LightBomb extends BombComponent {
  public LightBomb() {
    onCollision(BOMB, FLAME, (bomb, flame) -> {
      if (bomb != null) bomb.getComponent(LightBomb.class).explode();
    });

    // Stop player from moving in bomb's pos
    onCollisionEnd(PLAYER, BOMB, (player, bomb) -> {
      if (physic_block == null) physic_block = spawn("physic_block", bomb.getX(), bomb.getY(), 1);
    });

  //    onCollisionBegin(PHYSIC_BLOCK, ENEMY1, (physicBlock, enemy) -> {
  //      if (physicBlock != null) physicBlock.removeFromWorld();
  //    });
  }

  @Override
  public void onUpdate(double tpf) {
    super.onUpdate(tpf);
    if (physic_block != null) entity.setPosition(physic_block.getPosition());
  }

  @Override
  public void explode() {
    super.explode();

    // Explode after rearrange
    entity.setPosition(Utils.rearrange(entity.getPosition()));

    spawn("central_flame", new SpawnData(entity.getX(), entity.getY(), 0));

    spawn("top_up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 0));
    spawn("top_down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 0));
    spawn("top_right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 0));
    spawn("top_left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 0));
    for (int i = 1; i < flames-1; i++) {
      spawn("right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 0));
      spawn("left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 0));
      spawn("down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 0));
      spawn("up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 0));
    }

  }
}
