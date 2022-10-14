package Bomberman.Components;

import static Bomberman.BombermanType.BRICK;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.WALL;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.onCollision;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onCollisionOneTimeOnly;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class FlameComponent extends Component {
    public String name;
    private AnimatedTexture texture;
    private AnimationChannel animation;

    public FlameComponent(String assetName, SpawnData data) {
        this.name = assetName.split("\\.")[0];

        getGameTimer().runOnceAfter(() -> {
            if (entity != null) entity.removeFromWorld();
        }, Duration.seconds(0.4));

        onCollisionBegin(FLAME, WALL, (flame, wall) -> {
            flame.removeFromWorld();
        });
        onCollisionBegin(FLAME, BRICK, (flame, brick) -> {
            flame.removeFromWorld();
            brick.getComponent(BrickComponent.class).brickBreak();
        });
        onCollisionBegin(FLAME, FLAME, (flame, flame1) -> {
            String thisName = flame.getComponent(FlameComponent.class).name;
            switch (thisName) {
                case "up_flame":
                case "top_up_flame":
                    spawn(thisName, new SpawnData(flame.getX(), flame.getY()-TILED_SIZE, 0));
                    break;
                case "down_flame":
                case "top_down_flame":
                    spawn(thisName, new SpawnData(flame.getX(), flame.getY()+TILED_SIZE, 0));
                    break;
                case "left_flame":
                case "top_left_flame":
                    spawn(thisName, new SpawnData(flame.getX()-TILED_SIZE, flame.getY(), 0));
                    break;
                default:
                    spawn(thisName, new SpawnData(flame.getX()+TILED_SIZE, flame.getY(), 0));
                    break;
            }
            flame.removeFromWorld();
        });

        animation = new AnimationChannel(FXGL.image(assetName), 3, TILED_SIZE, TILED_SIZE, Duration.seconds(0.4), 0, 2);
        texture = new AnimatedTexture(animation);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }
}
