package Bomberman.net;

import Bomberman.BombermanType;
import Bomberman.Components.PlayerComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import java.net.InetAddress;
import javafx.geometry.Point2D;

public class PlayerMP {
  private final Entity player;
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

  public void setUsername(String username) {
    this.username = username;
  }
}
