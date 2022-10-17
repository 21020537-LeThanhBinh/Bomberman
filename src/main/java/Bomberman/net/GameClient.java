package Bomberman.net;

import Bomberman.BombermanGame;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class GameClient extends Thread {
  private InetAddress ipAddress;
  private DatagramSocket socket;
  private BombermanGame bombermanGame;

  public GameClient(BombermanGame bombermanGame, String ipAddress) {
    this.bombermanGame = bombermanGame;
    try {
      socket = new DatagramSocket();
      this.ipAddress = InetAddress.getByName(ipAddress);
    } catch (SocketException | UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  public void run() {
    while (true) {
      byte[] data = new byte[1024];
      DatagramPacket packet = new DatagramPacket(data, data.length);
      try {
        socket.receive(packet);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      System.out.println("SERVER > " + new String(packet.getData()));
    }
  }

  public void sendData(byte[] data) {
    DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
    try {
      socket.send(packet);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
