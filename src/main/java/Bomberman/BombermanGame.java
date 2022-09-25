package Bomberman;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.*;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsWorld;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class BombermanGame extends GameApplication {
    private Scanner sc;
    private int level;
    private int HEIGHT;
    private int WIDTH;
    private Entity player;
    private PlayerComponent playerComponent;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        try {
            sc = new Scanner(new File("src/main/resources/assets/levels/Level1_sample.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        level = sc.nextInt();
        HEIGHT = sc.nextInt();
        WIDTH = sc.nextInt();

        settings.setWidth(WIDTH * TILED_SIZE);
        settings.setHeight(HEIGHT * TILED_SIZE);

        settings.setTitle(GAME_TITLE);
        settings.setVersion(GAME_VERSION);

        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(false);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BombermanFactory());

        spawn("background");
        createMap();

        playerComponent = player.getComponent(PlayerComponent.class);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerComponent.moveLeft();
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerComponent.moveRight();
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerComponent.moveUp();
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerComponent.moveDown();
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Place Bomb") {
            @Override
            protected void onAction() {
                playerComponent.placeBomb(geti("flame"));
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();
        physics.setGravity(0,0);

        onCollision(BRICK, FLAME, (brick, flame) -> {
            brick.removeFromWorld();
            spawn("brick_break", brick.getX(), brick.getY());
        });
        onCollision(PLAYER, FLAME, (player, flame) -> {
            playerComponent.die();
        });
        onCollision(ENEMY1, FLAME, (enemy1, flame) -> {
            enemy1.removeFromWorld();
        });
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
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("speed", PLAYER_SPEED);
        vars.put("bomb", 1);
        vars.put("flame", 1);
    }

    @Override
    protected void initUI() {

    }

    protected void createMap() {
        sc.nextLine();
        for (int i = 0; i < HEIGHT; i++) {
            String line = sc.nextLine();
            for (int j = 0; j < WIDTH; j++) {
                Character ch = line.charAt(j);
                switch (ch) {
                    case '#':
                        spawn("wall", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                    case 'p':
                        player = spawn("player", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                    case '1':
                    case '2':
                        spawn("enemy", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                    case 'x':
                        spawn("portal", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                    case 'b':
                        spawn("powerup_bombs", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                    case 'f':
                        spawn("powerup_flames", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                    case 's':
                        spawn("powerup_speed", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                }
                if (Arrays.asList('*', 'x', 'b', 'f', 's').contains(ch)) {
                    spawn("brick", j * TILED_SIZE, i * TILED_SIZE);
                }
            }
        }
    }
}
