package Bomberman.Components;

import static Bomberman.Constants.Constant.TILED_SIZE;
import static Bomberman.DynamicEntityState.State.*;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

import Bomberman.DynamicEntityState.State;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class PlayerComponent extends Component {
    // Player's frame size
    private final int FRAME_SIZE = 45;
    private int bombCounter;
    private boolean bombValid;
    private State state;
    private PhysicsComponent physics;
    private AnimatedTexture texture;
    private AnimationChannel animIdleDown, animIdleRight, animIdleUp, animIdleLeft;
    private AnimationChannel animWalkDown, animWalkRight, animWalkUp, animWalkLeft;
    private AnimationChannel animDie;

    public PlayerComponent() {
        state = STOP;
        bombCounter = 0;
        bombValid = true;

        physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        setSkin();

        texture = new AnimatedTexture(animIdleDown);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        entity.addComponent(physics);
    }

    private void setSkin() {
        animDie = new AnimationChannel(image("player_die.png"), 5, FRAME_SIZE, FRAME_SIZE, Duration.seconds(3.5), 0, 4);

        animIdleDown = new AnimationChannel(image("player_down.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 0);
        animIdleRight = new AnimationChannel(image("player_right.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 0);
        animIdleUp = new AnimationChannel(image("player_up.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 0);
        animIdleLeft = new AnimationChannel(image("player_left.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 0);

        animWalkDown = new AnimationChannel(image("player_down.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 2);
        animWalkRight = new AnimationChannel(image("player_right.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 2);
        animWalkUp = new AnimationChannel(image("player_up.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 2);
        animWalkLeft = new AnimationChannel(image("player_left.png"), 3, FRAME_SIZE, FRAME_SIZE, Duration.seconds(0.5), 0, 2);
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
                if (texture.getAnimationChannel() == animWalkDown) {
                    texture.loopNoOverride(animIdleDown);
                } else if (texture.getAnimationChannel() == animWalkUp) {
                    texture.loopNoOverride(animIdleUp);
                } else if (texture.getAnimationChannel() == animWalkLeft) {
                    texture.loopNoOverride(animIdleLeft);
                } else if (texture.getAnimationChannel() == animWalkRight) {
                    texture.loopNoOverride(animIdleRight);
                }
                break;
            case DIE:
                texture.loopNoOverride(animDie);
                break;
        }
    }

    public void moveRight() {
        state = LEFT;
        physics.setVelocityX(geti("speed"));
    }

    public void moveLeft() {
        state = RIGHT;
        physics.setVelocityX(-geti("speed"));
    }

    public void moveUp() {
        state = DOWN;
        physics.setVelocityY(-geti("speed"));
    }

    public void moveDown() {
        state = UP;
        physics.setVelocityY(geti("speed"));
    }

    public void stop() {
        state = STOP;
    }

    public void die() {
        state = DIE;
    }

    public void placeBomb(int flames) {
        if (bombCounter == geti("bomb") || !bombValid) {
            return;
        }
        bombValid = false;
        getGameTimer().runOnceAfter(() -> {
            bombValid = true;
        }, Duration.seconds(0.1));

        bombCounter++;
        int bombLocationX = (int) (entity.getX() % TILED_SIZE > TILED_SIZE / 2
            ? entity.getX() + TILED_SIZE - entity.getX() % TILED_SIZE
            : entity.getX() - entity.getX() % TILED_SIZE);
        int bombLocationY = (int) (entity.getY() % TILED_SIZE > TILED_SIZE / 2
            ? entity.getY() + TILED_SIZE - entity.getY() % TILED_SIZE
            : entity.getY() - entity.getY() % TILED_SIZE);

        Entity bomb = spawn("bomb", new SpawnData(bombLocationX, bombLocationY));
        play("place_bomb.wav");
        getGameTimer().runOnceAfter(() -> {
            bomb.getComponent(BombComponent.class).explode(flames);
            play("explosion.wav");
            bombCounter--;
        }, Duration.seconds(2.1));
    }
}
