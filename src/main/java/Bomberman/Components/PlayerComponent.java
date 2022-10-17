package Bomberman.Components;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.BONUS_SPEED;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static Bomberman.DynamicEntityState.State.*;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.inc;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

import Bomberman.BombermanType;
import Bomberman.DynamicEntityState.State;
import Bomberman.Utils.Utils;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class PlayerComponent extends Component {
    private final int FRAME_SIZE = 45;
    private boolean bombValid;
    private State state;
    private State prevState;
    private BombermanType bombType;
    private PhysicsComponent physics;
    private AnimatedTexture texture;
    private AnimationChannel animIdleDown, animIdleRight, animIdleUp, animIdleLeft;
    private AnimationChannel animWalkDown, animWalkRight, animWalkUp, animWalkLeft;
    private AnimationChannel animDie;

    public PlayerComponent() {
        state = STOP;
        prevState = DOWN;
        bombType = BombermanType.CLASSICBOMB;
        bombValid = true;

        physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        setSkin();

        texture = new AnimatedTexture(animIdleDown);

        onCollisionBegin(PLAYER, POWERUP_FLAMES, (player, powerup) -> {
            powerup.removeFromWorld();
            play("powerup.wav");
            inc("flame", 1);
        });
        onCollisionBegin(PLAYER, POWERUP_BOMBS, (player, powerup) -> {
            powerup.removeFromWorld();
            play("powerup.wav");
            inc("bomb", 1);
        });
        onCollisionBegin(PLAYER, POWERUP_SPEED, (player, powerup) -> {
            powerup.removeFromWorld();
            play("powerup.wav");
            inc("speed", BONUS_SPEED);
            getGameTimer().runOnceAfter(() -> {
                inc("speed", -BONUS_SPEED);
            }, Duration.seconds(6));
        });
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        entity.addComponent(physics);
    }

    private void setSkin() {
        animDie = new AnimationChannel(image("player_die.png"), 5, FRAME_SIZE, FRAME_SIZE, Duration.seconds(3.5), 0, 4);

        animIdleDown = new AnimationChannel(image("player_down.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 0);
        animIdleRight = new AnimationChannel(image("player_right.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 0);
        animIdleUp = new AnimationChannel(image("player_up.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 0);
        animIdleLeft = new AnimationChannel(image("player_left.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 0);

        animWalkDown = new AnimationChannel(image("player_down.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 2);
        animWalkRight = new AnimationChannel(image("player_right.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 2);
        animWalkUp = new AnimationChannel(image("player_up.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 2);
        animWalkLeft = new AnimationChannel(image("player_left.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.4), 0, 2);
    }

    @Override
    public void onUpdate(double tpf) {
        if (physics.getVelocityX() != 0) {
            physics.setVelocityX((int) physics.getVelocityX() * 0.9);

            if (FXGLMath.abs(physics.getVelocityX()) < 1) {
                physics.setVelocityX(0);
            }
        }

        if (physics.getVelocityY() != 0) {
            physics.setVelocityY((int) physics.getVelocityY() * 0.9);

            if (FXGLMath.abs(physics.getVelocityY()) < 1) {
                physics.setVelocityY(0);
            }
        }

        switch (state) {
            case UP:
                texture.loopNoOverride(animWalkUp);
                break;
            case RIGHT:
                texture.loopNoOverride(animWalkRight);
                break;
            case DOWN:
                texture.loopNoOverride(animWalkDown);
                break;
            case LEFT:
                texture.loopNoOverride(animWalkLeft);
                break;
            case STOP:
                if (prevState == DOWN) {
                    texture.loopNoOverride(animIdleDown);
                } else if (prevState == UP) {
                    texture.loopNoOverride(animIdleUp);
                } else if (prevState == LEFT) {
                    texture.loopNoOverride(animIdleLeft);
                } else if (prevState == RIGHT) {
                    texture.loopNoOverride(animIdleRight);
                }
                break;
            case DIE:
                texture.loopNoOverride(animDie);
                break;
        }
    }

    public void moveRight() {
        state = RIGHT;
        physics.setVelocityX(geti("speed"));
    }

    public void moveLeft() {
        state = LEFT;
        physics.setVelocityX(-geti("speed"));
    }

    public void moveUp() {
        state = UP;
        physics.setVelocityY(-geti("speed"));
    }

    public void moveDown() {
        state = DOWN;
        physics.setVelocityY(geti("speed"));
    }

    public void stop() {
        prevState = state;
        state = STOP;
    }

    public void die() {
        state = DIE;
    }

    public void placeBomb() {
        if (geti("bomb") == 0 || !bombValid) {
            return;
        }

        Point2D bombLocation = Utils.rearrange(entity.getPosition());
        play("place_bomb.wav");
        switch (bombType) {
            case CLASSICBOMB:
                spawn("classic_bomb", new SpawnData(bombLocation));
                break;
            case LAZERBOMB:
                spawn("lazer_bomb", new SpawnData(bombLocation));
                break;
            case LIGHTBOMB:
                spawn("light_bomb", new SpawnData(bombLocation));
                break;
        }
    }

    public void setBombValid(boolean bombValid) {
        this.bombValid = bombValid;
    }

    public BombermanType getBombType() {
        return bombType;
    }

    public void setBombType(BombermanType bombType) {
        this.bombType = bombType;
    }

    public State getState() {
        return state;
    }

    public State getPrevState() {
        return prevState;
    }
}
