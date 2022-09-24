package Bomberman;

import static Bomberman.Constants.Constant.GAME_WORLD_HEIGHT;
import static Bomberman.Constants.Constant.GAME_WORLD_WIDTH;
import static Bomberman.Constants.Constant.SCENE_WIDTH;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.BombComponent;
import Bomberman.Components.FlameComponent;
import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BombermanFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder(data)
            .view(new Rectangle(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, Color.rgb(0, 125, 0)))
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
        Point2D randomPos = new Point2D(random(0, SCENE_WIDTH - TILED_SIZE), random(0, SCENE_WIDTH - TILED_SIZE));

        return entityBuilder(data)
            .type(BombermanType.ENEMY1)
            .at(randomPos)
            .viewWithBBox(new Circle(15, 15, 15, Color.RED))
            .collidable()
            .build();
    }

    @Spawns("bomb")
    public Entity newBomb(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.BOMB)
            .at(data.getX(), data.getY())
            .with(new BombComponent())
            .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
            .collidable()
            .build();
    }

    @Spawns("central_flame")
    public Entity newCentralFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .at(data.getX(), data.getY())
            .with(new FlameComponent("central_flame.png"))
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("top_down_flame")
    public Entity newTDFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_down_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("top_up_flame")
    public Entity newTUFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_up_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("top_right_flame")
    public Entity newTRFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_right_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("top_left_flame")
    public Entity newTLFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("top_left_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("up_flame")
    public Entity newUFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("up_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("down_flame")
    public Entity newDFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("down_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("left_flame")
    public Entity newLFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("left_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(new Point2D(0, 0), BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }

    @Spawns("right_flame")
    public Entity newRFlame(SpawnData data) {
        return entityBuilder(data)
            .type(BombermanType.FLAME)
            .with(new FlameComponent("right_flame.png"))
            .at(data.getX(), data.getY())
            .bbox(new HitBox(new Point2D(0, 0), BoundingShape.box(TILED_SIZE, TILED_SIZE)))
            .collidable()
            .build();
    }
}
