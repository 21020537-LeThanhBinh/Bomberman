package Bomberman.Components;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.BONUS_SPEED;
import static Bomberman.DynamicEntityState.State.*;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.inc;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.addUINode;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geti;

import Bomberman.BombermanGame;
import Bomberman.BombermanType;
import Bomberman.DynamicEntityState.State;
import Bomberman.Utils.Utils;
import Bomberman.net.packets.Packet02Move;
import Bomberman.net.packets.Packet03PlaceBomb;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlayerComponent extends Component {

    private final int FRAME_SIZE = 45;

    // Username
    private String username;
    private Text usernameTexture;

    // Physics
    private final PhysicsComponent physics;
    private State state;

    // Bomb
    private boolean bombValid;
    private State prevState;
    private BombermanType bombType;

    // Texture
    private AnimatedTexture texture;
    private AnimationChannel animIdleDown, animIdleRight, animIdleUp, animIdleLeft;
    private AnimationChannel animWalkDown, animWalkRight, animWalkUp, animWalkLeft;
    private AnimationChannel animDie;

    public PlayerComponent(String username) {
        this.username = username;
        usernameTexture = new Text(username);
        usernameTexture.setFont(Font.font("Showcard Gothic", 24));

        physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        state = STOP;

        prevState = DOWN;
        bombType = BombermanType.CLASSICBOMB;
        bombValid = true;

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

        Platform.runLater(() -> {
            addUINode(usernameTexture, 0, 0);
        });
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
    }

    public void moveRight() {
        state = RIGHT;
        physics.setVelocityX(geti("speed"));

        Packet02Move packet = new Packet02Move(this.getUsername(), (physics.getVelocityX()), (physics.getVelocityY()*0.9), state.getValue(), entity.getX(), entity.getY());
        packet.writeData(BombermanGame.game.getSocketClient());
    }

    public void moveLeft() {
        state = LEFT;
        physics.setVelocityX(-geti("speed"));

        Packet02Move packet = new Packet02Move(this.getUsername(), (physics.getVelocityX()), (physics.getVelocityY()*0.9), state.getValue(), entity.getX(), entity.getY());
        packet.writeData(BombermanGame.game.getSocketClient());
    }

    public void moveUp() {
        state = UP;
        physics.setVelocityY(-geti("speed"));

        Packet02Move packet = new Packet02Move(this.getUsername(), (physics.getVelocityX()*0.9), (physics.getVelocityY()), state.getValue(), entity.getX(), entity.getY());
        packet.writeData(BombermanGame.game.getSocketClient());
    }

    public void moveDown() {
        state = DOWN;
        physics.setVelocityY(geti("speed"));

        Packet02Move packet = new Packet02Move(this.getUsername(), (physics.getVelocityX()*0.9), (physics.getVelocityY()), state.getValue(), entity.getX(), entity.getY());
        packet.writeData(BombermanGame.game.getSocketClient());
    }

    public void stop() {
        prevState = state;
        state = STOP;

        Packet02Move packet = new Packet02Move(this.getUsername(), 0, 0, state.getValue(), entity.getX(), entity.getY());
        packet.writeData(BombermanGame.game.getSocketClient());
    }

    public void die() {
        state = DIE;

        Packet02Move packet = new Packet02Move(this.getUsername(), 0, 0, STOP.getValue(), entity.getX(), entity.getY());
        packet.writeData(BombermanGame.game.getSocketClient());
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
                if (prevState == STOP) prevState = state;
                spawn("lazer_bomb", new SpawnData(bombLocation.getX(), bombLocation.getY(), prevState.getValue()));
                break;
            case LIGHTBOMB:
                spawn("light_bomb", new SpawnData(bombLocation));
                break;
        }

        Packet03PlaceBomb packet = new Packet03PlaceBomb(this.getUsername(), prevState.getValue(), bombType == CLASSICBOMB ? 0 : bombType == LAZERBOMB ? 1 : 2);
        packet.writeData(BombermanGame.game.getSocketClient());
    }

    public void setBombType(BombermanType bombType) {
        this.bombType = bombType;
    }

    public State getState() {
        return state;
    }

    public String getUsername() {
        return username;
    }

    public void setPos(double velocityX, double velocityY, double x, double y) {
        try {
            physics.setVelocityX(velocityX);
            physics.setVelocityY(velocityY);

            // Lag caused by lost packages on the net
            if (entity.getPosition().distance(new Point2D(x + 2,y + 2)) > 24) {
                physics.overwritePosition(new Point2D(x + 2, y + 2));
            }

        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }

    public void setPrevState(State prevState) {
        this.prevState = prevState;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setUsernameTextLocation(double x, double y) {
        usernameTexture.setX(x);
        usernameTexture.setY(y);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        usernameTexture.setText("");
    }
}
