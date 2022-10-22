package Bomberman.Components.Enemy;

import static Bomberman.BombermanGame.*;
import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.BRICK;
import static Bomberman.BombermanType.ENEMY1;
import static Bomberman.BombermanType.ENEMY2;
import static Bomberman.BombermanType.ENEMY3;
import static Bomberman.BombermanType.ENEMY4;
import static Bomberman.BombermanType.ENEMY5;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.PLAYER;
import static Bomberman.BombermanType.WALL;
import static Bomberman.Constants.Constant.ENEMY_SPEED;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static Bomberman.DynamicEntityState.State.DIE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.inc;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGL.set;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

import Bomberman.BombermanGame;
import Bomberman.Components.Bomb.LightBomb;
import Bomberman.Components.AStarPathFinder.AStarPathFinder;
import Bomberman.Components.AStarPathFinder.Map;
import Bomberman.Components.AStarPathFinder.Path;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class Enemy3 extends EnemyComponent {
    // PathFinder stuff
    protected AStarPathFinder aStar;
    protected Map map;
    protected Path path;
    // real locaiton
    protected Point2D nextStep;
    // location on the map
    protected int myX;
    protected int myY;
    protected int playerX;
    protected int playerY;
    // Random movement
    protected boolean movingRandom;
    // Marker
    protected List<Entity> nextStepList = new ArrayList<com.almasb.fxgl.entity.Entity>();

    public Enemy3() {
        super(-ENEMY_SPEED, 0, 1.5, 3, "enemy3.png");

        FXGL.onCollisionEnd(ENEMY3, FLAME, (enemy3, flame) -> {
            enemy3.getComponent(Enemy3.class).setStateDie();
            getGameTimer().runOnceAfter(enemy3::removeFromWorld, Duration.seconds(2.4));
        });

        map = new Map(getMapWidth(), getMapHeight());
        aStar = new AStarPathFinder(map);

        movingRandom = true;
    }

    public Enemy3(double dx, double dy, double speedFactor, double reactionForce, String assetName) {
        super(dx, dy, speedFactor, reactionForce, assetName);

        FXGL.onCollision(ENEMY3, FLAME, (enemy3, flame) -> {
            enemy3.getComponent(Enemy3.class).setStateDie();
            getGameTimer().runOnceAfter(enemy3::removeFromWorld, Duration.seconds(2.4));
        });

        map = new Map(getMapWidth(), getMapHeight());
        aStar = new AStarPathFinder(map);

        movingRandom = false;
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

        if (myX == playerX && myY == playerY) {
            movingRandom = false;
        }

        if (!movingRandom) {
            loadPlayer();
            speedFactor = 1.5;
        }
        loadMap();
        path = aStar.findPath(myX,myY,playerX,playerY);

        // No path to player
        if (path == null) {
            playerX = random(myX-2,myX+2);
            playerY = random(myY-2,myY+2);

            movingRandom = true;
            speedFactor = 1;
            return;
        }

        // Ideal nextStep
        nextStep = new Point2D(path.getX(0) * TILED_SIZE, path.getY(0) * TILED_SIZE);

        // Marking (for testing)
//        removeMarker();
//        markPath();

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

    public void loadMap() {
        for (int x = 0; x < getMapWidth(); x++) {
            for (int y = 0; y < getMapHeight(); y++) {
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
    }

    public void loadPlayer() {
        Point2D playerPos = getGameWorld().getSingleton(PLAYER).getPosition();
        playerX = (int)Math.round(playerPos.getX() / TILED_SIZE);
        playerY = (int)Math.round(playerPos.getY() / TILED_SIZE);
    }

    public void markPath() {
        for (int i = 0; i < path.getLength(); i++) {
            nextStepList.add(spawn("yellow_mark", path.getX(i) * TILED_SIZE, path.getY(i) * TILED_SIZE));
        }
    }

    public void removeMarker() {
        nextStepList.forEach(Entity::removeFromWorld);
        nextStepList.clear();
    }

    @Override
    public void onRemoved() {
        removeMarker();
        super.onRemoved();
    }
}
