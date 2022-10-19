package Bomberman.Components;

import Bomberman.BombermanType;
import Bomberman.DynamicEntityState.State;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import java.net.InetAddress;
import javafx.application.Platform;
import javafx.geometry.Point2D;

public class PlayerMP {
  private Entity player;
  private PlayerComponent playerComponent;
  private InetAddress ipAddress;
  private int port;
  private String username;

  public PlayerMP(double x, double y, String username, InetAddress ipAddress, int port) {
    this.username = username;
    this.ipAddress = ipAddress;
    this.port = port;

    this.player = FXGL.entityBuilder()
        .at(x, y)
        .type(BombermanType.PLAYER)
        .bbox(new HitBox(new Point2D(2, 2), BoundingShape.circle(22)))
        .with(new PlayerComponent(username))
        .with(new CollidableComponent(true))
        .build();

    this.playerComponent = player.getComponent(PlayerComponent.class);
  }

  public String getUsername() {
    return this.username;
  }

  public Entity getEntity() {
    return player;
  }

  public InetAddress getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(InetAddress ipAddress) {
    this.ipAddress = ipAddress;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setPos(double velocityX, double velocityY, int state, double x, double y) {
    playerComponent.setPos(velocityX, velocityY, State.valueOf(state), x, y);
  }

  public void placeBomb(int state, int bombType) {
    Platform.runLater(() -> {
      playerComponent.setPrevState(State.valueOf(state));
      if (bombType == 0) {
        playerComponent.setBombType(BombermanType.CLASSICBOMB);
      }
      else if (bombType == 1) {
        playerComponent.setBombType(BombermanType.LAZERBOMB);
      }
      else if (bombType == 2) {
        playerComponent.setBombType(BombermanType.LIGHTBOMB);
      }
      playerComponent.placeBomb();
    });
  }
}
