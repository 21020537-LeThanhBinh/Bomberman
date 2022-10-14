package Bomberman.Components;

import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import java.util.ArrayList;
import javafx.util.Duration;

public class BombComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animation;
    private ArrayList<Entity> listFlame = new ArrayList<>();

    public BombComponent() {
        animation = new AnimationChannel(image("bomb.png"), 3, TILED_SIZE, TILED_SIZE, Duration.seconds(0.4), 0, 2);
        texture = new AnimatedTexture(animation);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    public void explode(int flames) {
        listFlame.add(spawn("central_flame", new SpawnData(entity.getX(), entity.getY())));
        listFlame.add(spawn("top_down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 0)));
        listFlame.add(spawn("top_right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 0)));
        listFlame.add(spawn("top_left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 0)));
        for (int i = 1; i < flames; i++) {
            listFlame.add(spawn("right_flame", new SpawnData(entity.getX() + TILED_SIZE, entity.getY(), 0)));
            listFlame.add(spawn("left_flame", new SpawnData(entity.getX() - TILED_SIZE, entity.getY(), 0)));
            listFlame.add(spawn("down_flame", new SpawnData(entity.getX(), entity.getY() + TILED_SIZE, 0)));
            listFlame.add(spawn("up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 0)));
        }
        listFlame.add(spawn("top_up_flame", new SpawnData(entity.getX(), entity.getY() - TILED_SIZE, 0)));

        entity.removeFromWorld();
    }
}