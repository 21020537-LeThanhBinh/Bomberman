package Bomberman.net.packets;

import Bomberman.net.GameClient;
import Bomberman.net.GameServer;

public class Packet00Login extends Packet {
  private final String username;
  private final double x;
  private final double y;

  public Packet00Login(byte[] data) {
    super(00);
    String[] dataArray = readData(data).split(",");
    this.username = dataArray[0];
    this.x = Double.parseDouble(dataArray[1]);
    this.y = Double.parseDouble(dataArray[2]);
  }

  public Packet00Login(String username, double x, double y) {
    super(00);
    this.username = username;
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
    return ("00" + this.username + "," + this.x + "," + this.y).getBytes();
  }

  public String getUsername() {
    return username;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
}
