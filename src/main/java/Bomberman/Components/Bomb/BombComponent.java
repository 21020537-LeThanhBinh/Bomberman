package Bomberman.Components.Bomb;

import static Bomberman.BombermanType.BOMB;
import static Bomberman.BombermanType.ENEMY1;
import static Bomberman.BombermanType.FLAME;
import static Bomberman.BombermanType.PHYSIC_BLOCK;
import static Bomberman.BombermanType.PLAYER;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.inc;
import static com.almasb.fxgl.dsl.FXGL.onCollision;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onCollisionEnd;
import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import Bomberman.Components.PlayerComponent;
import Bomberman.DynamicEntityState.State;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import java.util.ArrayList;
import javafx.util.Duration;

public abstract class BombComponent extends Component {
    protected Entity owner;
    protected int flames;
    protected Entity physic_block;
    private AnimatedTexture texture;
    private AnimationChannel animation;

    public BombComponent() {
        getGameTimer().runOnceAfter(() -> {
            if (entity != null) explode();
        }, Duration.seconds(2));

        onCollisionBegin(PHYSIC_BLOCK, FLAME, (physicBlock, flame) -> {
            if (physicBlock != null) physicBlock.removeFromWorld();
        });

        animation = new AnimationChannel(image("bomb.png"), 3, TILED_SIZE, TILED_SIZE, Duration.seconds(0.4), 0, 2);
        texture = new AnimatedTexture(animation);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    public void explode() {
        entity.removeFromWorld();
        play("explosion.wav");
        owner.getComponent(PlayerComponent.class).incBombCounter();
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public void setFlamePower(int flames) {
        this.flames = flames;
    }

    public int getFlames() {
        return flames;
    }
}