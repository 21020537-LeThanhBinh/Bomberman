package Bomberman;

import static Bomberman.BombermanType.*;
import static Bomberman.Constants.Constant.*;
import static Bomberman.DynamicEntityState.State.DIE;
import static Bomberman.DynamicEntityState.State.DOWN;
import static Bomberman.DynamicEntityState.State.STOP;
import static Bomberman.Sounds.SoundEffect.turnOffMusic;
import static Bomberman.Sounds.SoundEffect.turnOnMusic;
import static Bomberman.Sounds.SoundEffect.unmute;
import static com.almasb.fxgl.dsl.FXGL.*;

import Bomberman.Components.Enemy.Enemy1;
import Bomberman.Components.Enemy.Enemy2;
import Bomberman.Components.Enemy.Enemy3;
import Bomberman.Components.Enemy.Enemy4;
import Bomberman.Components.PlayerComponent;
import Bomberman.Components.PlayerMP;
import Bomberman.DynamicEntityState.State;
import Bomberman.Menu.GameMenu;
import Bomberman.Menu.MainMenu;
import Bomberman.UI.EndingScene;
import Bomberman.UI.UIComponents;
import Bomberman.net.GameClient;
import Bomberman.net.GameServer;
import Bomberman.net.packets.Packet00Login;
import Bomberman.net.packets.Packet01Disconnect;
import Bomberman.net.packets.Packet02Move;
import Bomberman.net.packets.Packet03PlaceBomb;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javax.swing.JOptionPane;

public class BombermanGame extends GameApplication  {

    private static BombermanGame instance;

    // Multiplayer
    private boolean isMultiplayer;
    private GameServer socketServer;
    private GameClient socketClient;
    private PlayerMP playerMP;

    // File and map
    private Scanner sc;
    private int MAP_HEIGHT;
    private int MAP_WIDTH;
    private Viewport viewport;

    // Entities related
    private Entity player;
    private PlayerComponent playerComponent;
    private List<Entity> stillObject = new ArrayList<>();
    private List<Entity> enemies = new ArrayList<>();

    @Override
    protected void initSettings(GameSettings settings) {
        instance = this;

        isMultiplayer = false;

//        settings.setWidth(Math.min(MAX_SCENE_WIDTH, MAP_WIDTH * TILED_SIZE));
//        settings.setHeight(Math.min(MAX_SCENE_HEIGHT, MAP_HEIGHT * TILED_SIZE));

        settings.setWidth(MAX_SCENE_WIDTH);
        settings.setHeight(MAX_SCENE_HEIGHT);

        settings.setTitle(GAME_TITLE);
        settings.setVersion(GAME_VERSION);

        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(false);

        settings.setIntroEnabled(false);
        settings.setGameMenuEnabled(true);
        settings.setMainMenuEnabled(true);
        settings.setFontUI("game_font.ttf");
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new GameMenu();
            }

        });
    }

    @Override
    protected void onPreInit() {
        unmute();
        loopBGM("stage_theme.mp3");

        socketClient = new GameClient(this, "192.168.1.4");
        socketClient.start();
    }

    @Override
    protected void initGame() {
        if (isMultiplayer) {
            loadFile("Multiplayer_map.txt");
        } else {
            loadFile("Level1_sample.txt");
        }

        // Todo: make map_size static
        MAP_HEIGHT = sc.nextInt(); set("map_height", MAP_HEIGHT);
        MAP_WIDTH = sc.nextInt(); set("map_width", MAP_WIDTH);

        getGameWorld().addEntityFactory(new BombermanFactory());
        loadLevel();

        viewport = getGameScene().getViewport();
        viewport.setBounds(0, -96, MAP_WIDTH * TILED_SIZE, MAP_HEIGHT * TILED_SIZE);

        if (isMultiplayer) {
            playerMP = new PlayerMP(48, 48, JOptionPane.showInputDialog(this, "Please enter a username"), null,-1);
            player = playerMP.getEntity();
            getGameWorld().addEntity(player);

            viewport.setX(-(MAX_SCENE_WIDTH-MAP_WIDTH*TILED_SIZE)/2);
            viewport.setY(-(MAX_SCENE_HEIGHT-MAP_HEIGHT*TILED_SIZE));

            Packet00Login loginPacket = new Packet00Login(playerMP.getUsername(), player.getX(), player.getY());
            if (socketServer != null) {
                socketServer.addConnection(playerMP, loginPacket);
            }
            loginPacket.writeData(socketClient);
        } else {
            viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);
        }
        playerComponent = player.getComponent(PlayerComponent.class);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerComponent.moveLeft();
                Packet02Move packet = new Packet02Move(playerComponent.getUsername(), playerComponent.getPhysics().getVelocityX(), playerComponent.getPhysics().getVelocityY(), playerComponent.getState().getValue(), player.getX(), player.getY());
                packet.writeData(socketClient);
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerComponent.moveRight();
                Packet02Move packet = new Packet02Move(playerComponent.getUsername(), playerComponent.getPhysics().getVelocityX(), playerComponent.getPhysics().getVelocityY(), playerComponent.getState().getValue(), player.getX(), player.getY());
                packet.writeData(socketClient);
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerComponent.moveUp();
                Packet02Move packet = new Packet02Move(playerComponent.getUsername(), playerComponent.getPhysics().getVelocityX(), playerComponent.getPhysics().getVelocityY(), playerComponent.getState().getValue(), player.getX(), player.getY());
                packet.writeData(socketClient);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerComponent.moveDown();
                Packet02Move packet = new Packet02Move(playerComponent.getUsername(), playerComponent.getPhysics().getVelocityX(), playerComponent.getPhysics().getVelocityY(), playerComponent.getState().getValue(), player.getX(), player.getY());
                packet.writeData(socketClient);
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Place Bomb") {
            @Override
            protected void onActionBegin() {
                playerComponent.placeBomb();

                Packet03PlaceBomb packet = new Packet03PlaceBomb(playerComponent.getUsername(), playerComponent.getPrevState().getValue(), playerComponent.getBombType() == CLASSICBOMB ? 0 : playerComponent.getBombType() == LAZERBOMB ? 1 : 2);
                packet.writeData(socketClient);
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
            }
        }, KeyCode.DELETE);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0,0);

        onCollision(PLAYER, PORTAL, (player, portal) -> {
            if (getGameWorld().getGroup(ENEMY1, ENEMY2, ENEMY3, ENEMY4, ENEMY5, POWERUP_BOMBS, POWERUP_FLAMES).getSize() == 0) {
                // Next level music . . .

                player.removeFromWorld();
                playerComponent.setBombValid(false);
                getGameTimer().runOnceAfter(this::nextLevel, Duration.seconds(1));
            }
        });
        onCollisionBegin(PLAYER, ENEMY1, (p, enemy) -> {
            if (enemy.getComponent(Enemy1.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied(p);
            }
        });
        onCollisionBegin(PLAYER, ENEMY2, (p, enemy) -> {
            if (enemy.getComponent(Enemy2.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied(p);
            }
        });
        onCollisionBegin(PLAYER, ENEMY3, (p, enemy) -> {
            if (enemy.getComponent(Enemy3.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied(p);
            }
        });
        onCollisionBegin(PLAYER, ENEMY4, (p, enemy) -> {
            if (enemy.getComponent(Enemy4.class).getState() != DIE
                && playerComponent.getState() != DIE) {
                onPlayerDied(p);
            }
        });
        // Multiplayer
        onCollisionBegin(PLAYER, FLAME, (p, flame) -> {
            if (p.getComponent(PlayerComponent.class).getState() != DIE) {
                onPlayerDied(p);
                if (p != this.player) {
                    inc("score", 1);
                    // Sent score to server ...

                    System.out.println(playerMP.getUsername() + "'s score: " + geti("score"));
                }
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("map_width", 0);
        vars.put("map_height", 0);
        vars.put("level", STARTING_LEVEL);
        vars.put("score", 0);

        vars.put("life", 3);
        vars.put("enemies", 0);
        vars.put("flame", 2);
        vars.put("speed", PLAYER_SPEED);
        vars.put("bomb", 5);
        vars.put("levelTime", TIME_LEVEL);
    }

    @Override
    protected void initUI() {
        UIComponents.addILabelUI("level", "🚩 %d", 35, 25);
        UIComponents.addILabelUI("life", "💜 %d", 160, 25);
        UIComponents.addILabelUI("score", "💵  %d", 300, 25);
        UIComponents.addILabelUI("flame", "🔥 %d", 560, 25);
        UIComponents.addILabelUI("speed", "👟  %d", 670, 25);
        UIComponents.addILabelUI("bomb", "💣 %d", 840, 25);
        UIComponents.addILabelUI("enemies", "👻 %d", 1010, 25);
        UIComponents.addDLabelUI("levelTime", "⏰ %.0f", 1140, 25);
    }

    protected void loadFile(String file) {
        try {
            sc = new Scanner(new File("src/main/resources/assets/levels/" + file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadLevel() {
        getGameScene().setBackgroundColor(Color.web("#B9B9B9"));

        spawn("background");

        sc.nextLine();
        for (int i = 0; i < MAP_HEIGHT; i++) {
            String line = sc.nextLine();
            for (int j = 0; j < MAP_WIDTH; j++) {
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
                        enemies.add(spawn("enemy1", j * TILED_SIZE, i * TILED_SIZE));
                        break;
                    case '2':
                        enemies.add(spawn("enemy2", j * TILED_SIZE, i * TILED_SIZE));
                        break;
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
    }

    protected void nextLevel() {
        inc("level", 1);

        // Remove old level
        stillObject.forEach(Entity::removeFromWorld);
        enemies.forEach(Entity::removeFromWorld);

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

        // Reset powers ...
        set("flame", 1);
    }

    public void onPlayerDied(Entity p) {
        PlayerComponent pComponent = p.getComponent(PlayerComponent.class);
        pComponent.die();

        // If single player
        if (!isMultiplayer) {
            turnOffMusic();
            play("player_die.wav");
            FXGL.runOnce(() -> {
                getGameScene().getViewport().fade(() -> {
                    turnOnMusic();
                    inc("life", -1);
                    if (geti("life") > 0) {
                        resetLevel();
                    } else {
                        turnOffMusic();
                        getSceneService().pushSubScene(new EndingScene("   GAME OVER !!!\n\n\n\n   DO YOUR BEST"));
                    }
                });
            }, Duration.seconds(2));
        } else {
            FXGL.runOnce(() -> {
                int randomX = random(1, MAP_WIDTH-1) * TILED_SIZE;
                int randomY = random(1, MAP_HEIGHT-1) * TILED_SIZE;
                // Todo: improved respawn method ...
                pComponent.setPrevState(DOWN);
                pComponent.setState(STOP);
                pComponent.setPos(0,0, randomX, randomY);

                Packet02Move packet = new Packet02Move(pComponent.getUsername(), 0, 0, STOP.getValue(), randomX, randomY);
                packet.writeData(socketClient);
            }, Duration.seconds(2));
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        getGameWorld().getEntitiesByType(PLAYER).forEach(p ->  {
            p.getComponent(PlayerComponent.class).setUsernameTextLocation(p.getX() - viewport.getX(), p.getY() - viewport.getY());
        });
    }


    /**
     * Server stuff.
     * Where enemies are other players
     */

    public void addPlayerMP(Entity p) {
        Platform.runLater(() -> {
            getGameWorld().addEntity(p);
            enemies.add(p);
            System.out.println("Spawn enemy!");
            if (p.getComponent(PlayerComponent.class).getUsername().compareTo(playerComponent.getUsername()) > 0) {
                p.getComponent(PlayerComponent.class).setPos(0, 0, 11*48, 11*48);
            } else {
                playerComponent.setPos(0,0,11*48, 11*48);
            }
        });
    }

    public void removePlayerMP(String username) {
        Platform.runLater(() -> {
            for (int i = 0; i < enemies.size(); i++) {
                if (enemies.get(i).getComponent(PlayerComponent.class).getUsername().equalsIgnoreCase(username)) {
                    enemies.get(i).removeFromWorld();
                    enemies.remove(enemies.get(i--));
                }
            }
        });
    }

    public void movePlayerMP(String username, double velocityX, double velocityY, int state, double x, double y) {
        Platform.runLater(() -> {
            enemies.forEach(p -> {
                if (p.getComponent(PlayerComponent.class).getUsername().equalsIgnoreCase(username)) {
                    p.getComponent(PlayerComponent.class).setState(State.valueOf(state));
                    p.getComponent(PlayerComponent.class).setPos(velocityX, velocityY, x, y);
                }
            });
        });
    }

    public void placeBombMP(String username, int prevState, int bombType) {
        Platform.runLater(() -> {
            enemies.forEach(p -> {
                if (p.getComponent(PlayerComponent.class).getUsername().equalsIgnoreCase(username)) {
                    p.getComponent(PlayerComponent.class).setPrevState(State.valueOf(prevState));
                    p.getComponent(PlayerComponent.class).setBombType(bombType == 0 ? CLASSICBOMB : bombType == 1 ? LAZERBOMB : LIGHTBOMB);
                    p.getComponent(PlayerComponent.class).placeBomb();
                }
            });
        });
    }

    public void setNewSocketServer() {
        this.socketServer = new GameServer(this);
        socketServer.start();
    }

    public PlayerMP getPlayerMP() {
        return playerMP;
    }

    public void setMultiplayer(boolean multiplayer) {
        isMultiplayer = multiplayer;
    }

    public void setPlayerMP(PlayerMP playerMP) {
        this.playerMP = playerMP;
    }

    public static BombermanGame getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
