package Bomberman;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.*;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.BrickComponent;
import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsWorld;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

public class BombermanGame extends GameApplication {
    private Scanner sc;
    private int HEIGHT;
    private int WIDTH;
    private Entity player;
    private PlayerComponent playerComponent;
    private List<Entity> stillObject = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        loadFile("Level1_sample.txt");

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
        loadLevel();
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
            protected void onActionBegin() {
                playerComponent.placeBomb(geti("flame"));
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();
        physics.setGravity(0,0);

        onCollision(PLAYER, PORTAL, (player, portal) -> {
            if (getGameWorld().getGroup(ENEMY1, POWERUP_BOMBS, POWERUP_FLAMES).getSize() == 0) {
                // Next level music . . .

                player.removeFromWorld();
                playerComponent.setBombValid(false);
                getGameTimer().runOnceAfter(this::nextLevel, Duration.seconds(1));
            }
        });

        // Stop player from moving in bomb's pos
        onCollisionEnd(PLAYER, BOMB, (player, bomb) -> {
            Entity physic_block = spawn("physic_block", bomb.getX(), bomb.getY());
            getGameTimer().runOnceAfter(physic_block::removeFromWorld, Duration.seconds(2.1));
        });

        onCollisionBegin(FLAME, WALL, (flame, wall) -> {
            flame.removeFromWorld();
        });
        onCollision(BRICK, FLAME, (brick, flame) -> {
            flame.removeFromWorld();
            brick.getComponent(BrickComponent.class).brickBreak();
            getGameTimer().runOnceAfter(brick::removeFromWorld, Duration.seconds(0.4));
        });

        onCollision(PLAYER, FLAME, (player, flame) -> {
            playerComponent.die();
        });
        onCollision(ENEMY1, PLAYER, (enemy1, player) -> {
            playerComponent.die();
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", STARTING_LEVEL);
        vars.put("speed", PLAYER_SPEED);
        vars.put("bomb", 1);
        vars.put("flame", 1);
    }

    @Override
    protected void initUI() {

    }

    protected void loadFile(String file) {
        try {
            sc = new Scanner(new File("src/main/resources/assets/levels/" + file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadLevel() {
        sc.nextLine();
        for (int i = 0; i < HEIGHT; i++) {
            String line = sc.nextLine();
            for (int j = 0; j < WIDTH; j++) {
                Character ch = line.charAt(j);
                switch (ch) {
                    case '#':
                        stillObject.add(spawn("wall", j * TILED_SIZE, i * TILED_SIZE));
                        break;
                    case 'p':
                        player = spawn("player", j * TILED_SIZE, i * TILED_SIZE);
                        playerComponent = player.getComponent(PlayerComponent.class);
                        break;
                    case '1':
                        spawn("enemy1", j * TILED_SIZE, i * TILED_SIZE);
                        break;
                    case 'x':
                        stillObject.add(spawn("portal", j * TILED_SIZE, i * TILED_SIZE));
                        break;
                    case 'b':
                        stillObject.add(spawn("powerup_bombs", j * TILED_SIZE, i * TILED_SIZE));
                        break;
                    case 'f':
                        stillObject.add(spawn("powerup_flames", j * TILED_SIZE, i * TILED_SIZE));
                        break;
                    case 's':
                        stillObject.add(spawn("powerup_speed", j * TILED_SIZE, i * TILED_SIZE));
                        break;
                }
                if (Arrays.asList('*', 'x', 'b', 'f', 's').contains(ch)) {
                    stillObject.add(spawn("brick", j * TILED_SIZE, i * TILED_SIZE));
                }
            }
        }
    }

    protected void nextLevel() {
        inc("level", 1);

        // Remove old level . . .
        stillObject.forEach(Entity::removeFromWorld);

        if (geti("level") <= MAX_LEVEL) {
            loadFile("Level" + geti("level") + "_sample.txt");
            loadLevel();
        }
        else {
            System.out.println("You win!");
            // Load win screen . . .
        }
    }
}
