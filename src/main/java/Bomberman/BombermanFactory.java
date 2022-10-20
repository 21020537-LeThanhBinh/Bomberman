package Bomberman;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.*;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.*;
import Bomberman.Components.Bomb.ClassicBomb;
import Bomberman.Components.Bomb.LazerBomb;
import Bomberman.Components.Bomb.LightBomb;
import Bomberman.Components.Enemy.Enemy1;
import Bomberman.Components.Enemy.Enemy2;
import Bomberman.Components.Enemy.Enemy3;
import Bomberman.Components.Enemy.Enemy4;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BombermanFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder(data)
            .view(new Rectangle(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, Color.rgb(0, 125, 0)))
            .zIndex(-1)
            .build();
    }

    @Spawns("yellow_mark")
    public Entity newYellowMark(SpawnData data) {
        return entityBuilder(data)
            .view(new Rectangle(48, 48, Color.rgb(125, 255, 0)))
            .zIndex(-1)
            .build();
    }

    @Spawns("physic_block")
    public Entity newPhysicsBlock(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        if (data.getZ() > 0) physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder(data)
            .type(PHYSIC_BLOCK)
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
            .with(physics)
            .collidable()
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
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .with(new BrickComponent())
            .with(new PhysicsComponent())
            .collidable()
            .build();
    }

    @Spawns("portal")
    public Entity newPortal(SpawnData data) {
        return entityBuilder(data)
            .type(PORTAL)
            .view("portal.png")
            .bbox(new HitBox(new Point2D(1,1), BoundingShape.box(TILED_SIZE-2, TILED_SIZE-2)))
            .collidable()
            .zIndex(-1)
            .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.PLAYER)
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
            .with(new PlayerComponent(""))
            .with(new CollidableComponent(true))
            .build();
    }

    @Spawns("enemy1")
    public Entity newEnemy1(SpawnData data) {
        return entityBuilder(data)
            .type(ENEMY1)
            .bbox(new HitBox(new Point2D(5, 5), BoundingShape.box(38, 38)))
            .with(new Enemy1())
            .collidable()
            .build();
    }

    @Spawns("enemy2")
    public Entity newEnemy2(SpawnData data) {
        return entityBuilder(data)
            .type(ENEMY2)
            .bbox(new HitBox(new Point2D(5, 5), BoundingShape.box(38, 38)))
            .with(new Enemy2())
            .collidable()
            .build();
    }

    @Spawns("enemy3")
    public Entity newEnemy3(SpawnData data) {
        return entityBuilder(data)
            .type(ENEMY3)
            .bbox(new HitBox(new Point2D(5, 5), BoundingShape.box(38, 38)))
            .with(new Enemy3())
            .collidable()
            .build();
    }

    @Spawns("enemy4")
    public Entity newEnemy4(SpawnData data) {
        return entityBuilder(data)
            .type(ENEMY4)
            .bbox(new HitBox(new Point2D(5, 5), BoundingShape.box(38, 38)))
            .with(new Enemy4())
            .collidable()
            .build();
    }

    @Spawns("classic_bomb")
    public Entity newClassicBomb(SpawnData data) {
        return entityBuilder(data)
            .type(BOMB)
            .with(new ClassicBomb())
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
            .collidable()
            .build();
    }

    @Spawns("lazer_bomb")
    public Entity newLazerBomb(SpawnData data) {
        // data.getZ() for bomb's direction
        return entityBuilder(data)
            .type(BOMB)
            .with(new LazerBomb((int)data.getZ()))
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
            .collidable()
            .build();
    }

    @Spawns("light_bomb")
    public Entity newLightBomb(SpawnData data) {
        return entityBuilder(data)
            .type(BOMB)
            .with(new LightBomb())
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
            .collidable()
            .build();
    }

    @Spawns("central_flame")
    public Entity newCentralFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("central_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("top_down_flame")
    public Entity newTDFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_down_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("top_up_flame")
    public Entity newTUFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_up_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("top_right_flame")
    public Entity newTRFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_right_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("top_left_flame")
    public Entity newTLFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_left_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("up_flame")
    public Entity newUFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("up_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("down_flame")
    public Entity newDFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("down_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("left_flame")
    public Entity newLFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("left_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
            .collidable()
            .build();
    }

    @Spawns("right_flame")
    public Entity newRFlame(SpawnData data) {
        boolean isLazer = data.getZ() > 0;
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("right_flame.png", isLazer))
            .bbox(new HitBox(new Point2D(2,2), BoundingShape.box(TILED_SIZE-4, TILED_SIZE-4)))
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
