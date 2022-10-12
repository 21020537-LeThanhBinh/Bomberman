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
    private boolean isCatching;
    public Enemy1() {
        super(-ENEMY_SPEED, 0, 1, 3, "enemy1.png");
        isCatching = true;
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

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(1.0/60);

        Entity player = getGameWorld().getSingleton(PLAYER);

        if (state == DIE || player
            .getComponent(PlayerComponent.class)
            .getState() == DIE) {
            return;
        }

        int playerCellX = (int) (player.getX() / TILED_SIZE);
        int playerCellY = (int) (player.getY() / TILED_SIZE);
        int enemyCellY = (int) (entity.getY() / TILED_SIZE);
        int enemyCellX = (int) (entity.getX() / TILED_SIZE);
        if (entity.distance(player) < TILED_SIZE * 3) {
            if (isCatching) {
                if (dx == 0) {
                    if ((entity.getY() - player.getY()) * dy < 0) {
                        speedFactor = 1.3;
                    } else {
                        speedFactor = 1;
                    }

                    if (enemyCellY == playerCellY) {
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

                    if (enemyCellX == playerCellX) {
                        if (player.getY() > entity.getY()) {
                            turnDown();
                        } else {
                            turnUp();
                        }
                    }
                }
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
