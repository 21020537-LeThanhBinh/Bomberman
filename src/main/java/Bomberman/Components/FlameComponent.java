package Bomberman.Components;

import static Bomberman.BombermanType.BRICK;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.WALL;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class FlameComponent extends Component {
    private String name;
    private boolean isLazer;
    private AnimatedTexture texture;
    private AnimationChannel animation;

    public FlameComponent(String assetName, boolean isLazer) {
        this.name = assetName.split("\\.")[0];
        this.isLazer = isLazer;

        getGameTimer().runOnceAfter(() -> {
            if (entity != null) entity.removeFromWorld();
        }, Duration.seconds(0.3));

        onCollisionBegin(FLAME, WALL, (flame, wall) -> {
            flame.removeFromWorld();
        });
        onCollisionBegin(FLAME, BRICK, (flame, brick) -> {
            if (!flame.getComponent(FlameComponent.class).isLazer) flame.removeFromWorld();
            brick.getComponent(BrickComponent.class).brickBreak();
        });
        onCollisionBegin(FLAME, FLAME, (flame, flame1) -> {
            String thisName = flame.getComponent(FlameComponent.class).name;
            int thisZ = flame.getComponent(FlameComponent.class).isLazer ? 1 : 0;

            getGameTimer().runOnceAfter(() -> {
                switch (thisName) {
                    case "up_flame":
                    case "top_up_flame":
                        spawn(thisName, new SpawnData(flame.getX(), flame.getY()-TILED_SIZE, thisZ));
                        break;
                    case "down_flame":
                    case "top_down_flame":
                        spawn(thisName, new SpawnData(flame.getX(), flame.getY()+TILED_SIZE, thisZ));
                        break;
                    case "left_flame":
                    case "top_left_flame":
                        spawn(thisName, new SpawnData(flame.getX()-TILED_SIZE, flame.getY(), thisZ));
                        break;
                    case "right_flame":
                    case "top_right_flame":
                        spawn(thisName, new SpawnData(flame.getX()+TILED_SIZE, flame.getY(), thisZ));
                        break;
                }
            }, Duration.seconds(0.05));

            if (thisName.equals("central_flame")) {
                flame1.removeFromWorld();
            }
            else {
                flame.removeFromWorld();
            }
        });

        animation = new AnimationChannel(FXGL.image(assetName), 3, TILED_SIZE, TILED_SIZE, Duration.seconds(0.3), 0, 2);
        texture = new AnimatedTexture(animation);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }
}
