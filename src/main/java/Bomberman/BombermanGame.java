package Bomberman;

import static Bomberman.Constants.Constant.GAME_TITLE;
import static Bomberman.Constants.Constant.GAME_VERSION;
import static Bomberman.Constants.Constant.PLAYER_SPEED;
import static Bomberman.Constants.Constant.SCENE_HEIGHT;
import static Bomberman.Constants.Constant.SCENE_WIDTH;
import static Bomberman.Constants.Constant.TILED_SIZE;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class BombermanGame extends GameApplication {
    private Entity player;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(SCENE_WIDTH);
        settings.setHeight(SCENE_HEIGHT);
        settings.setTitle(GAME_TITLE);
        settings.setVersion(GAME_VERSION);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BombermanFactory());

        spawn("background");

        player = spawn("player");
        spawn("enemy");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).moveLeft();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).moveRight();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).moveUp();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).moveDown();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Place Bomb") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).placeBomb(geti("flame"));
            }
        }, KeyCode.SPACE);

    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();
        physics.setGravity(0,0);
        physics.addCollisionHandler(new CollisionHandler(BombermanType.FLAME, BombermanType.ENEMY1) {
            @Override
            protected void onCollisionBegin(Entity flame, Entity enemy1) {
                Point2D randomPos = new Point2D(random(0, SCENE_WIDTH - TILED_SIZE), random(0, SCENE_HEIGHT - TILED_SIZE));
                enemy1.setPosition(randomPos);
                inc("point", 1);
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("point", 0);
        vars.put("speed", PLAYER_SPEED);
        vars.put("bomb", 1);
        vars.put("flame", 1);
    }

    @Override
    protected void initUI() {
        Text textPixels = new Text();
        textPixels.setTranslateX(100);
        textPixels.setTranslateY(100);

        textPixels.textProperty().bind(getWorldProperties().intProperty("point").asString());

        getGameScene().addUINode(textPixels);
    }
}
