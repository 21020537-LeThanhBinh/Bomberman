package Bomberman;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.*;
import static Bomberman.DynamicEntityState.State.DIE;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.Enemy.*;
import Bomberman.Components.PlayerComponent;
import Bomberman.Components.PlayerMP;
import Bomberman.DynamicEntityState.State;
import Bomberman.net.GameClient;
import Bomberman.net.GameServer;
import Bomberman.net.packets.Packet00Login;
import Bomberman.net.packets.Packet01Disconnect;
import Bomberman.net.packets.Packet02Move;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
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
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javax.swing.JOptionPane;

public class BombermanGame extends GameApplication  {
    public Viewport viewport;

    public static BombermanGame game;
    // Multiplayer
    private GameServer socketServer;
    private GameClient socketClient;
    private PlayerMP playerMP;

    // File and map
    private Scanner sc;
    private int MAP_HEIGHT;
    private int MAP_WIDTH;

    // Entities related
    private Entity player;
    private PlayerComponent playerComponent;
    private List<Entity> stillObject = new ArrayList<>();
    private List<Entity> enemies = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        game = this;

        loadFile("Level1_sample.txt");

        MAP_HEIGHT = sc.nextInt();
        MAP_WIDTH = sc.nextInt();

        settings.setWidth(Math.min(MAX_SCENE_WIDTH, MAP_WIDTH * TILED_SIZE)/2);
        settings.setHeight(Math.min(MAX_SCENE_HEIGHT, MAP_HEIGHT * TILED_SIZE));

        settings.setTitle(GAME_TITLE);
        settings.setVersion(GAME_VERSION);

        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(false);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BombermanFactory());
        set("map_width", MAP_WIDTH);
        set("map_height", MAP_HEIGHT);

        spawn("background");

        viewport = getGameScene().getViewport();
        viewport.setBounds(0, 0, MAP_WIDTH * TILED_SIZE, MAP_HEIGHT * TILED_SIZE);
//        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);

        loadLevel();

        if (JOptionPane.showConfirmDialog(null, "Do you want to run the server") == 0) {
            socketServer = new GameServer(this);
            socketServer.start();
        }

        socketClient = new GameClient(this, "localhost");
        socketClient.start();

        playerMP = new PlayerMP(48, 48, JOptionPane.showInputDialog(this, "Please enter a username"),
            null,-1);
        player = playerMP.getEntity();
        playerComponent = player.getComponent(PlayerComponent.class);
        addPlayer(playerMP.getEntity());

        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);

        Packet00Login loginPacket = new Packet00Login(playerMP.getUsername(), player.getX(), player.getY());
        if (socketServer != null) {
            socketServer.addConnection(playerMP, loginPacket);
        }
        loginPacket.writeData(socketClient);
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
                playerComponent.placeBomb();
            }
        }, KeyCode.SPACE);

        getInput().addAction(new UserAction("Switch to classic bomb") {
            @Override
            protected void onActionBegin() {
                playerComponent.setBombType(CLASSICBOMB);
                System.out.println("Switched to classic bomb");
                // Music ...
            }
        }, KeyCode.DIGIT1);

        getInput().addAction(new UserAction("Switch to lazer bomb") {
            @Override
            protected void onActionBegin() {
                playerComponent.setBombType(LAZERBOMB);
                System.out.println("Switched to lazer bomb");
                // Music ...
            }
        }, KeyCode.DIGIT2);

        getInput().addAction(new UserAction("Switch to light bomb") {
            @Override
            protected void onActionBegin() {
                playerComponent.setBombType(LIGHTBOMB);
                System.out.println("Switched to light bomb");
                // Music ...
            }
        }, KeyCode.DIGIT3);

        getInput().addAction(new UserAction("Exit game") {
            @Override
            protected void onActionBegin() {
                Packet01Disconnect packet = new Packet01Disconnect(playerComponent.getUsername());
                packet.writeData(socketClient);
                Platform.exit();
                System.out.println("Exit game");
            }
        }, KeyCode.ESCAPE);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();
        physics.setGravity(0,0);

        onCollision(PLAYER, PORTAL, (player, portal) -> {
            if (getGameWorld().getGroup(ENEMY1, ENEMY2, ENEMY3, ENEMY4, ENEMY5, POWERUP_BOMBS, POWERUP_FLAMES).getSize() == 0) {
                // Next level music . . .

                player.removeFromWorld();
                playerComponent.setBombValid(false);
                getGameTimer().runOnceAfter(this::nextLevel, Duration.seconds(1));
            }
        });

        onCollisionBegin(PLAYER, ENEMY1, (player, enemy) -> {
            if (enemy.getComponent(Enemy1.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied();
            }
        });
        onCollisionBegin(PLAYER, ENEMY2, (player, enemy) -> {
            if (enemy.getComponent(Enemy2.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied();
            }
        });
        onCollisionBegin(PLAYER, ENEMY3, (player, enemy) -> {
            if (enemy.getComponent(Enemy3.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied();
            }
        });
        onCollisionBegin(PLAYER, ENEMY4, (player, enemy) -> {
            if (enemy.getComponent(Enemy4.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied();
            }
        });
        onCollisionBegin(PLAYER, FLAME, (player, flame) -> {
            if (playerComponent.getState() != DIE) {
                onPlayerDied();
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("map_width", 0);
        vars.put("map_height", 0);
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
        for (int i = 0; i < MAP_HEIGHT; i++) {
            String line = sc.nextLine();
            for (int j = 0; j < MAP_WIDTH; j++) {
                Character ch = line.charAt(j);
                switch (ch) {
                    case '#':
                        stillObject.add(spawn("wall", j * TILED_SIZE, i * TILED_SIZE));
                        break;
//                    case 'p':
//                        player = spawn("player", j * TILED_SIZE, i * TILED_SIZE);
//                        playerComponent = player.getComponent(PlayerComponent.class);
//                        break;
//                    case '1':
//                        enemies.add(spawn("enemy1", j * TILED_SIZE, i * TILED_SIZE));
//                        break;
//                    case '2':
//                        enemies.add(spawn("enemy2", j * TILED_SIZE, i * TILED_SIZE));
//                        break;
                    case '3':
                        enemies.add(spawn("enemy3", j * TILED_SIZE, i * TILED_SIZE));
                        break;
                    case '4':
                        enemies.add(spawn("enemy4", j * TILED_SIZE, i * TILED_SIZE));
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

//        Viewport viewport = getGameScene().getViewport();
//        viewport.setBounds(0, 0, MAP_WIDTH * TILED_SIZE, MAP_HEIGHT * TILED_SIZE);
//        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);
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

    protected void resetLevel() {
        player.removeFromWorld();
        stillObject.forEach(Entity::removeFromWorld);
        enemies.forEach(Entity::removeFromWorld);

        loadFile("Level" + geti("level") + "_sample.txt");
        loadLevel();

        set("flame", 1);
    }

    public void onPlayerDied() {
//        playerComponent.die();
//        resetLevel();
    }


    /**
     * Server stuff.
     */

    public void addPlayer(Entity newPlayer) {
        Platform.runLater(() -> {
            getGameWorld().addEntity(newPlayer);
        });
    }

    public PlayerMP getPlayerMP() {
        return playerMP;
    }

    public GameServer getSocketServer() {
        return socketServer;
    }

    public void setSocketServer(GameServer socketServer) {
        this.socketServer = socketServer;
    }

    public GameClient getSocketClient() {
        return socketClient;
    }

    public void setSocketClient(GameClient socketClient) {
        this.socketClient = socketClient;
    }

    public void removePlayerMP(String username) {
        Platform.runLater(() -> {
            getGameWorld().getEntitiesByType(PLAYER).forEach(p -> {
                if (p.getComponent(PlayerComponent.class).getUsername().equalsIgnoreCase(username)) {
                    p.removeFromWorld();
                }
            });
        });
    }

    public void movePlayer(String username, double velocityX, double velocityY, int state, double x, double y) {
        Platform.runLater(() -> {
            getGameWorld().getEntitiesByType(PLAYER).forEach(p -> {
                if (p.getComponent(PlayerComponent.class).getUsername().equalsIgnoreCase(username)) {
                    p.getComponent(PlayerComponent.class).setPos(velocityX, velocityY, State.valueOf(state), x, y);
                }
            });
        });
    }

    public void placeBomb(String username, int state, int bombType) {
        Platform.runLater(() -> {
            getGameWorld().getEntitiesByType(PLAYER).forEach(p -> {
                if (p.getComponent(PlayerComponent.class).getUsername().equalsIgnoreCase(username)) {
                    p.getComponent(PlayerComponent.class).setPrevState(State.valueOf(state));
                    p.getComponent(PlayerComponent.class).setBombType(bombType == 0 ? CLASSICBOMB : bombType == 1 ? LAZERBOMB : LIGHTBOMB);
                    p.getComponent(PlayerComponent.class).placeBomb();
                }
            });
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        super.onUpdate(tpf);
        // Player's name rendering
        getGameWorld().getEntitiesByType(PLAYER).forEach(p -> {
            p.getComponent(PlayerComponent.class).text.setX(p.getX() - viewport.getX());
            p.getComponent(PlayerComponent.class).text.setY(p.getY() - viewport.getY());
        });
    }
}
