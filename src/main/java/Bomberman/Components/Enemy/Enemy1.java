package Bomberman.Components.Enemy;

import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.BRICK;
import static Bomberman.BombermanType.ENEMY1;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.WALL;
import static Bomberman.Constants.Constant.ENEMY_SPEED;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onCollisionEnd;

import Bomberman.Components.Bomb.LightBomb;
import javafx.util.Duration;

public class Enemy1 extends EnemyComponent {
    public Enemy1() {
        super(-ENEMY_SPEED, 0, 1, 3, "enemy1.png");
        onCollisionBegin(ENEMY1, BRICK, (enemy1, brick) -> {
            enemy1.getComponent(Enemy1.class).turn();
        });
        onCollisionBegin(ENEMY1, WALL, (enemy1, wall) -> {
            enemy1.getComponent(Enemy1.class).turn();
        });
        onCollisionBegin(ENEMY1, BOMB, (enemy1, bomb) -> {
            if (!bomb.hasComponent(LightBomb.class))
                enemy1.getComponent(Enemy1.class).turn();
        });
        onCollisionEnd(ENEMY1, FLAME, (enemy1, flame) -> {
            enemy1.getComponent(Enemy1.class).setStateDie();
            getGameTimer().runOnceAfter(enemy1::removeFromWorld, Duration.seconds(2.4));
        });
    }
}
