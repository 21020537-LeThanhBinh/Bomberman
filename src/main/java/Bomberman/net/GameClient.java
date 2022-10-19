package Bomberman.net;

import Bomberman.BombermanGame;
import Bomberman.Components.PlayerMP;
import Bomberman.net.packets.Packet;
import Bomberman.net.packets.Packet00Login;
import Bomberman.net.packets.Packet01Disconnect;
import Bomberman.net.packets.Packet02Move;
import Bomberman.net.packets.Packet03PlaceBomb;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GameClient extends Thread {
  private InetAddress ipAddress;
  private DatagramSocket socket;
  private BombermanGame game;

  public GameClient(BombermanGame game, String ipAddress) {
    this.game = game;
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
      this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
    }
  }

  private void parsePacket(byte[] data, InetAddress address, int port) {
    String message = new String(data).trim();
    Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));

    Packet packet = null;
    switch (type) {
      default:
      case INVALID:

        break;
      case LOGIN:
        packet = new Packet00Login(data);
        System.out.println("["+address.getHostAddress()+":"+port+"]"+((Packet00Login)packet).getUsername()+" has joined the game...");

        PlayerMP player = new PlayerMP(((Packet00Login)packet).getX(), ((Packet00Login)packet).getY(), ((Packet00Login)packet).getUsername(), address, port);
        game.addPlayer(player.getEntity());

        break;
      case DISCONNET:
        packet = new Packet01Disconnect(data);
        System.out.println("["+address.getHostAddress()+":"+port+"]"+((Packet01Disconnect)packet).getUsername()+" has left...");

        game.removePlayerMP(((Packet01Disconnect)packet).getUsername());
         break;
      case MOVE:
        packet = new Packet02Move(data);
        this.handleMove((Packet02Move) packet);

        break;
      case PLACEBOMB:
        packet = new Packet03PlaceBomb(data);
        this.handlePlaceBomb((Packet03PlaceBomb) packet);

        break;
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

  private void handleMove(Packet02Move packet) {
    if (game.getPlayerMP() != null)
      this.game.movePlayer(packet.getUsername(), packet.getVelocityX(), packet.getVelocityY(), packet.getState(), packet.getX(), packet.getY());
  }

  private void handlePlaceBomb(Packet03PlaceBomb packet) {
    if (game.getPlayerMP() != null)
      this.game.placeBomb(packet.getUsername(), packet.getState(), packet.getBombType());
  }
}
