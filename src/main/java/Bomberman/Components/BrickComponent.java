package Bomberman.Components;

import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class BrickComponent extends Component {
    AnimatedTexture texture;
    AnimationChannel animBrick, animBrickBreak;

    public BrickComponent() {
        animBrick = new AnimationChannel(image("brick.png"), 1, TILED_SIZE, TILED_SIZE,
                Duration.seconds(0.4), 0, 0);
        animBrickBreak = new AnimationChannel(image("brick_break.png"), 3, TILED_SIZE, TILED_SIZE,
            Duration.seconds(0.4), 0, 2);

        texture = new AnimatedTexture(animBrick);
        texture.loop();
    }

    public void brickBreak() {
        texture.loopNoOverride(animBrickBreak);
        getGameTimer().runOnceAfter(() -> {
            if (entity != null) entity.removeFromWorld();
        }, Duration.seconds(0.4));
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }
}
