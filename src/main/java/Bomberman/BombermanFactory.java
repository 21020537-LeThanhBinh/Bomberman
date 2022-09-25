package Bomberman;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.*;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.*;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BombermanFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder(data)
            .view(new Rectangle(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, Color.rgb(0, 125, 0)))
            .zIndex(-1)
            .build();
    }

    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        return entityBuilder(data)
            .type(WALL)
            .viewWithBBox("wall.png")
            .with(new PhysicsComponent())
            .collidable()
            .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        return entityBuilder(data)
            .type(BRICK)
            .viewWithBBox("brick.png")
            .with(new PhysicsComponent())
            .collidable()
            .build();
    }

    @Spawns("brick_break")
    public Entity newBrickBreak(SpawnData data) {
        return FXGL.entityBuilder(data)
            .type(BRICK_BREAK)
            .with(new BrickBreakComponent())
            .build();
    }

    @Spawns("portal")
    public Entity newPortal(SpawnData data) {
        return entityBuilder(data)
            .type(PORTAL)
            .viewWithBBox("portal.png")
            .collidable()
            .zIndex(-1)
            .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.PLAYER)
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(20)))
            .with(new PlayerComponent())
            .collidable()
            .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.ENEMY1)
            .viewWithBBox(new Circle(24, 24, 24, Color.RED))
            .collidable()
            .build();
    }

    @Spawns("bomb")
    public Entity newBomb(SpawnData data) {
        return entityBuilder(data)
            .type(BOMB)
            .with(new BombComponent())
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
            .collidable()
            .build();
    }

    @Spawns("central_flame")
    public Entity newCentralFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("central_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("top_down_flame")
    public Entity newTDFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_down_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("top_up_flame")
    public Entity newTUFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_up_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("top_right_flame")
    public Entity newTRFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_right_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("top_left_flame")
    public Entity newTLFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_left_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("up_flame")
    public Entity newUFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("up_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("down_flame")
    public Entity newDFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("down_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("left_flame")
    public Entity newLFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("left_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("right_flame")
    public Entity newRFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("right_flame.png"))
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .build();
    }

    @Spawns("powerup_flames")
    public Entity newItem(SpawnData data) {
        return entityBuilder(data)
            .type(POWERUP_FLAMES)
            .viewWithBBox("powerup_flames.png")
            .at(data.getX()+4,data.getY()+4)
            .collidable()
            .zIndex(-1)
            .build();
    }

    @Spawns("powerup_bombs")
    public Entity newItem2(SpawnData data) {
        return entityBuilder(data)
            .type(POWERUP_BOMBS)
            .viewWithBBox("powerup_bombs.png")
            .at(data.getX()+4,data.getY()+4)
            .collidable()
            .zIndex(-1)
            .build();
    }

    @Spawns("powerup_speed")
    public Entity newItem3(SpawnData data) {
        return entityBuilder(data)
            .type(POWERUP_SPEED)
            .viewWithBBox("powerup_speed.png")
            .at(data.getX()+4,data.getY()+4)
            .collidable()
            .zIndex(-1)
            .build();
    }
}
