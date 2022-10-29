package Bomberman.net;

import Bomberman.BombermanGame;
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
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {
  private final DatagramSocket socket;
  private final BombermanGame game;
  private final List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();

  public GameServer(BombermanGame game) {
    this.game = game;
    try {
      socket = new DatagramSocket(1331);
    } catch (SocketException e) {
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

    Packet packet;
    switch (type) {
      default:
      case INVALID:

        break;
      case LOGIN:
        packet = new Packet00Login(data);
        System.out.println("["+address.getHostAddress()+":"+port+"]"+((Packet00Login)packet).getUsername()+" has connected...");

        PlayerMP player = new PlayerMP(((Packet00Login)packet).getX(), ((Packet00Login)packet).getY(), ((Packet00Login)packet).getUsername(), address, port);
        this.addConnection(player, (Packet00Login)packet);

        break;
      case DISCONNET:
        packet = new Packet01Disconnect(data);
        System.out.println("["+address.getHostAddress()+":"+port+"]"+((Packet01Disconnect)packet).getUsername()+" has left...");

        this.removeConnection((Packet01Disconnect)packet);

        break;
      case MOVE:
        packet = new Packet02Move(data);
        if (getPlayerMP(((Packet02Move) packet).getUsername()) != null) {
          packet.writeData(this);
        }

        break;
      case PLACEBOMB:
        packet = new Packet03PlaceBomb(data);
        if (getPlayerMP(((Packet03PlaceBomb) packet).getUsername()) != null) {
          packet.writeData(this);
        }

        break;
    }
  }

  public void addConnection(PlayerMP player, Packet00Login packet) {
    boolean alreadyConnected = false;
    for (PlayerMP p : this.connectedPlayers) {
      if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
        if (p.getIpAddress() == null) {
          p.setIpAddress(player.getIpAddress());
        }
        if (p.getPort() == -1) {
          p.setPort(player.getPort());
        }
        alreadyConnected = true;
      } else {
        sendData(packet.getData(), p.getIpAddress(), p.getPort());
        sendData(new Packet00Login(p.getUsername(), p.getEntity().getX(), p.getEntity().getY()).getData(),
            player.getIpAddress(), player.getPort());
      }
    }
    if (!alreadyConnected) {
      this.connectedPlayers.add(player);
    }
  }

  public void removeConnection(Packet01Disconnect packet) {
    this.connectedPlayers.removeIf(p -> p.getUsername().equalsIgnoreCase(packet.getUsername()));
    packet.writeData(this);
  }

  public void sendData(byte[] data, InetAddress ipAddress, int port) {
    DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
    try {
      socket.send(packet);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendDataToAllClients(byte[] data) {
    for (PlayerMP p : connectedPlayers) {
      sendData(data, p.getIpAddress(), p.getPort());
    }
  }

  private PlayerMP getPlayerMP(String username) {
    for (PlayerMP p : connectedPlayers) {
      if (p.getUsername().equalsIgnoreCase(username)) {
        return p;
      }
    }
    return null;
  }

  public DatagramSocket getSocket() {
    return socket;
  }
}
