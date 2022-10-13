package Bomberman.Components.Enemy;

import Bomberman.Components.FlameComponent;
import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

import static Bomberman.Constants.Constant.ENEMY_SPEED;
import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static Bomberman.DynamicEntityState.State.DIE;
import static com.almasb.fxgl.dsl.FXGL.*;

public class Enemy1 extends Enemy {
    public Enemy1() {
        super(-ENEMY_SPEED, 0, 1, 3, "enemy1.png");
        onCollisionBegin(ENEMY1, BRICK, (enemy1, brick) -> {
            enemy1.getComponent(Enemy1.class).turn();
        });
        onCollisionBegin(ENEMY1, WALL, (enemy1, wall) -> {
            enemy1.getComponent(Enemy1.class).turn();
        });
        onCollisionBegin(ENEMY1, BOMB, (enemy1, bomb) -> {
            enemy1.getComponent(Enemy1.class).turn();
        });
        onCollision(ENEMY1, FLAME, (enemy1, flame) -> {
            enemy1.getComponent(Enemy1.class).setStateDie();
            getGameTimer().runOnceAfter(enemy1::removeFromWorld, Duration.seconds(2.4));
        });
    }
}
