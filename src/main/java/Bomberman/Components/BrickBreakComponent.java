package Bomberman.Components;

import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class BrickBreakComponent extends Component {
    AnimatedTexture texture;
    AnimationChannel animation;

    public BrickBreakComponent() {
        getGameTimer().runOnceAfter(() -> {
            entity.removeFromWorld();
        }, Duration.seconds(0.4));

        animation = new AnimationChannel(image("brick_break.png"), 3, TILED_SIZE, TILED_SIZE,
                Duration.seconds(0.4), 0, 2);
        texture = new AnimatedTexture(animation);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }
}
