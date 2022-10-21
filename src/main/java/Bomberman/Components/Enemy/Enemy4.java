package Bomberman.Components.Enemy;

import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.BRICK;
import static Bomberman.BombermanType.ENEMY4;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.PLAYER;
import static Bomberman.BombermanType.WALL;
import static Bomberman.Constants.Constant.ENEMY_SPEED;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

public class Enemy4 extends Enemy3 {
    public Enemy4() {
        super(-ENEMY_SPEED, 0, 2, 3, "enemy4.png");

        FXGL.onCollision(ENEMY4, FLAME, (enemy4, flame) -> {
            enemy4.getComponent(Enemy4.class).setStateDie();
            getGameTimer().runOnceAfter(enemy4::removeFromWorld, Duration.seconds(2.4));
        });
    }

    @Override
    public void loadMap() {
        for (int x = 0; x < geti("map_width"); x++) {
            for (int y = 0; y < geti("map_height"); y++) {
                map.setVal(x,y,1);
            }
        }

        getGameWorld().getEntitiesByType(WALL, BRICK, FLAME).forEach(entity1 -> {
            int thisX = (int)entity1.getX()/TILED_SIZE;
            int thisY = (int)entity1.getY()/TILED_SIZE;
            map.setVal(thisX,thisY,0);
        });

        getGameWorld().getEntitiesByType(BOMB).forEach(entity1 -> {
            int thisX = (int)entity1.getX()/TILED_SIZE;
            int thisY = (int)entity1.getY()/TILED_SIZE;
            map.setVal(thisX,thisY,0);

            int flame = getGameWorld().getSingleton(PLAYER).getComponent(PlayerComponent.class).getFlamePower();
            for (int i = 1; i <= flame; i++) {
                map.setVal(thisX+i,thisY,0);
                map.setVal(thisX-i,thisY,0);
                map.setVal(thisX,thisY+i,0);
                map.setVal(thisX,thisY-i,0);
            }
        });
    }

    @Override
    public void loadPlayer() {

    }
}
