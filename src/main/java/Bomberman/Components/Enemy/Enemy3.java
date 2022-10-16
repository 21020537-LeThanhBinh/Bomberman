package Bomberman.Components.Enemy;

import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.BRICK;
import static Bomberman.BombermanType.ENEMY3;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.PLAYER;
import static Bomberman.BombermanType.WALL;
import static Bomberman.Constants.Constant.ENEMY_SPEED;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static Bomberman.DynamicEntityState.State.DIE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

import Bomberman.Components.Bomb.LightBomb;
import Bomberman.Components.Enemy.AStarPathFinder.AStarPathFinder;
import Bomberman.Components.Enemy.AStarPathFinder.Map;
import Bomberman.Components.Enemy.AStarPathFinder.Path;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class Enemy3 extends EnemyComponent {
    // PathFinder stuff
    private AStarPathFinder aStar;
    private Map map;
    private Path path;
    // real locaiton
    private Point2D nextStep;
    // location on the map
    private int myX;
    private int myY;
    private int playerX;
    private int playerY;
    public Enemy3() {
        super(-ENEMY_SPEED, 0, 3, 3, "enemy3.png");

        FXGL.onCollisionBegin(ENEMY3, BRICK, (enemy3, brick) -> {
            enemy3.getComponent(Enemy3.class).turn();
        });
        FXGL.onCollisionBegin(ENEMY3, WALL, (enemy3, wall) -> {
            enemy3.getComponent(Enemy3.class).turn();
        });
        FXGL.onCollisionBegin(ENEMY3, BOMB, (enemy3, bomb) -> {
            if (!bomb.hasComponent(LightBomb.class))
                enemy3.getComponent(Enemy3.class).turn();
        });
        FXGL.onCollision(ENEMY3, FLAME, (enemy3, flame) -> {
            enemy3.getComponent(Enemy3.class).setStateDie();
            getGameTimer().runOnceAfter(enemy3::removeFromWorld, Duration.seconds(2.4));
        });

        map = new Map(geti("map_width"), geti("map_height"));
        aStar = new AStarPathFinder(map);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        myX = (int)(entity.getX()/TILED_SIZE);
        myY = (int)(entity.getY()/TILED_SIZE);
    }

    @Override
    public void onUpdate(double tpf) {
        if (state == DIE) {
            super.onUpdate(tpf);
            return;
        }

        loadMap();
        path = aStar.findPath(myX,myY,playerX,playerY);

        // No path to player
        if (path == null) {
            return;
        }

        // Ideal nextStep
        nextStep = new Point2D(path.getX(0) * TILED_SIZE, path.getY(0) * TILED_SIZE);

        // When at nextStep (accuracy < 3)
        if (entity.getPosition().distance(nextStep) < 3) {
            myX = path.getX(0);
            myY = path.getY(0);
            return;
        }

        // Real nextStep and change direction
        if (Math.abs(nextStep.getX() - entity.getX()) > Math.abs(nextStep.getY() - entity.getY())) {
            if (nextStep.getX() > entity.getX()) {
                nextStep = new Point2D(entity.getX() + TILED_SIZE, entity.getY());
                turnRight();
            }
            else {
                nextStep = new Point2D(entity.getX() - TILED_SIZE, entity.getY());
                turnLeft();
            }
        } else {
            if (nextStep.getY() > entity.getY()) {
                nextStep = new Point2D(entity.getX(), entity.getY() + TILED_SIZE);
                turnDown();
            }
            else {
                nextStep = new Point2D(entity.getX(), entity.getY() - TILED_SIZE);
                turnUp();
            }
        }

        super.onUpdate(tpf);
    }

    private void loadMap() {
        for (int x = 0; x < geti("map_width"); x++) {
            for (int y = 0; y < geti("map_height"); y++) {
                map.setVal(x,y,1);
            }
        }

        getGameWorld().getEntitiesByType(WALL, BRICK).forEach(entity1 -> {
            int thisX = (int)entity1.getX()/TILED_SIZE;
            int thisY = (int)entity1.getY()/TILED_SIZE;
            map.setVal(thisX,thisY,0);
        });

        getGameWorld().getEntitiesByType(BOMB).forEach(entity1 -> {
            if (!entity1.hasComponent(LightBomb.class)) {
                int thisX = (int)entity1.getX()/TILED_SIZE;
                int thisY = (int)entity1.getY()/TILED_SIZE;
                map.setVal(thisX,thisY,0);
            }
        });

        Point2D playerPos = getGameWorld().getSingleton(PLAYER).getPosition();
        playerX = (int)Math.round(playerPos.getX() / TILED_SIZE);
        playerY = (int)Math.round(playerPos.getY() / TILED_SIZE);
    }

    @Override
    public void turn() {
//        if (dx < 0) {
//            turnRight();
//        } else if (dx > 0) {
//            turnLeft();
//        } else if (dy < 0) {
//            turnDown();
//        } else {
//            turnUp();
//        }
    }
}
