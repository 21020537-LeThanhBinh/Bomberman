package Bomberman.net.packets;

import Bomberman.net.GameClient;
import Bomberman.net.GameServer;

public class Packet02Move extends Packet {
  private final String username;
  private final double velocityX;
  private final double velocityY;
  private final int state;
  private final double x;
  private final double y;

  public Packet02Move(byte[] data) {
    super(02);
    String[] dataArray = readData(data).split(",");
    this.username = dataArray[0];
    this.velocityX = Double.parseDouble(dataArray[1]);
    this.velocityY = Double.parseDouble(dataArray[2]);
    this.state = Integer.parseInt(dataArray[3]);
    this.x = Double.parseDouble(dataArray[4]);
    this.y = Double.parseDouble(dataArray[5]);
  }

  public Packet02Move(String username, double velocityX, double velocityY, int state, double x, double y) {
    super(02);
    this.username = username;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
    this.state = state;
    this.x = x;
    this.y = y;
  }

  @Override
  public void writeData(GameClient client) {
    client.sendData(getData());
  }

  @Override
  public void writeData(GameServer server) {
    server.sendDataToAllClients(getData());
  }

  @Override
  public byte[] getData() {
    return ("02" + this.username + "," + this.velocityX + "," + this.velocityY + "," + this.state + "," + this.x + "," + this.y).getBytes();
  }

  public String getUsername() {
    return username;
  }

  public int getState() {
    return state;
  }

  public double getVelocityX() {
    return velocityX;
  }

  public double getVelocityY() {
    return velocityY;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
}
