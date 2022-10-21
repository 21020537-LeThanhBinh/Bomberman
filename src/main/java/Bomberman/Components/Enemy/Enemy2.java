package Bomberman.Components.Enemy;

import static Bomberman.Constants.Constant.ENEMY_SPEED;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static Bomberman.DynamicEntityState.State.DIE;
import static Bomberman.BombermanType.*;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.inc;
import static com.almasb.fxgl.dsl.FXGL.onCollision;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onCollisionEnd;
import static com.almasb.fxgl.dsl.FXGL.set;

import Bomberman.Components.Bomb.LightBomb;
import Bomberman.Components.FlameComponent;
import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

public class Enemy2 extends EnemyComponent {
    private boolean isCatching;

    public Enemy2() {
        super(-ENEMY_SPEED, 0, 1, 3, "enemy2.png");
        isCatching = true;
        onCollisionBegin(ENEMY2, BRICK, (enemy2, brick) -> {
            enemy2.getComponent(Enemy2.class).turn();
        });
        onCollisionBegin(ENEMY2, WALL, (enemy2, wall) -> {
            enemy2.getComponent(Enemy2.class).turn();
        });
        onCollisionBegin(ENEMY2, PORTAL, (enemy2, portal) -> {
            enemy2.getComponent(Enemy2.class).turn();
        });
        onCollisionBegin(ENEMY2, BOMB, (enemy2, bomb) -> {
            if (!bomb.hasComponent(LightBomb.class))
                enemy2.getComponent(Enemy2.class).turn();
        });
        onCollisionEnd(ENEMY2, FLAME, (enemy2, flame) -> {
            enemy2.getComponent(Enemy2.class).setStateDie();
            getGameTimer().runOnceAfter(enemy2::removeFromWorld, Duration.seconds(2.4));
            inc("enemies", -1);
        });

    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        Entity player = getGameWorld().getSingleton(PLAYER);

        if (state == DIE || player
                .getComponent(PlayerComponent.class)
                .getState() == DIE) {
            return;
        }

        int playerX = (int) player.getX();
        int playerY = (int) player.getY();
        int enemyY = (int) entity.getY();
        int enemyX = (int) entity.getX();
        if (getEntity().distance(player) < TILED_SIZE * 3) {
            if (isCatching) {
                if (dx == 0) {
                    if ((entity.getY() - player.getY()) * dy < 0) {
                        speedFactor = 1.3;
                    } else {
                        speedFactor = 1;
                    }

                    if (enemyY == playerY) {
                        if (player.getX() > entity.getX()) {
                            turnRight();
                        } else {
                            turnLeft();
                        }
                    }
                } else if (dy == 0) {
                    if ((entity.getX() - player.getX()) * dx < 0) {
                        speedFactor = 1.3;
                    } else {
                        speedFactor = 1;
                    }

                    if (enemyX == playerX) {
                        if (player.getY() > entity.getY()) {
                            turnDown();
                        } else {
                            turnUp();
                        }
                    }
                }
            }
            else if (dx == 0 && ((int) entity.getY() % TILED_SIZE <= 5 && (int) entity.getY() % TILED_SIZE > 0)) {
                isCatching = true;
            } else if (dy == 0 && ((int) entity.getX() % TILED_SIZE <= 5 && (int) entity.getY() % TILED_SIZE > 0)) {
                isCatching = true;
            }
        } else {
            speedFactor = 1;
            isCatching = true;
        }
    }

    @Override
    public void turn() {
        isCatching = false;
        super.turn();
    }
}
