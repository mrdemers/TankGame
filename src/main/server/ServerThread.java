package main.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

import main.bullets.Bullet;
import main.levels.Level;

public class ServerThread extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected boolean runServer = true;
	protected PlayerList playerList;
	public String hostName, hostAddress;
	public Level level;
	public Server parent;

	public ServerThread(PlayerList playerList, Level level) {
		super("Server Thread");
		this.playerList = playerList;
		this.level = level;
		try {
			// creates the socket
			socket = new DatagramSocket(6062);
			hostName = InetAddress.getLocalHost().getHostName();
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addParent(Server s) {
		parent = s;
	}

	public void run() {
		System.out.println("running server thread");
		while (runServer) {
			try {
				receivePacket();
				sendPacket();
			} catch (IOException ex) {
				System.out.println("Packet failed");
				ex.printStackTrace();
				runServer = false;
			}
		}
	}

	public void receivePacket() throws IOException {
		// receive message from client
		byte[] buf = new byte[256];
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		// translate message in a thread
		String message = new String(packet.getData(), 0, packet.getLength());
		Translator translator = new Translator(parent, message, packet
				.getAddress().getHostAddress(), level);
		translator.start();
	}

	public void sendPacket() throws IOException {
		byte[] buf = getMessage().getBytes();

		// send the message to the client to the given address and port
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		packet = new DatagramPacket(buf, buf.length, address, port);
		socket.send(packet);
	}

	int lastSpot = 0;

	public synchronized String getMessage() {
		String ret = "";
		for (Player p : playerList.players) {
			if (!p.hasLevel) {
				ret += "level ";
				ret += level.seed + " ";
				return ret;
			}
		}
		for (Player p : playerList.players) {
			ret += "info " + p.getInfo();
		}
		ArrayList<Bullet> toRemove = new ArrayList<Bullet>();
		int temp = lastSpot;
		int length = level.bullets.size() > 10? 10 : level.bullets.size();
		for (int i = lastSpot; i < lastSpot + length; i++) {
			if (level.bullets.size() > 0) {
				temp++;
				Bullet b = level.bullets.get(i % level.bullets.size());
				ret += "bullet " + b.getInfo();
				if (b.dead) toRemove.add(b);
			}
		}
		lastSpot = temp;
		for (int i = 0; i < toRemove.size(); i++) {
			level.bullets.remove(toRemove.remove(i--));
		}
		return ret;
	}

	public void addPlayer(Player p) {
		playerList.add(p);
	}
}

class Translator extends Thread {
	private String message, ip;
	private Level level;
	private Server parent;

	public Translator(Server parent, String message, String ip, Level level) {
		this.message = message;
		this.ip = ip;
		this.level = level;
		this.parent = parent;
	}

	public synchronized void run() {
		StringTokenizer part = new StringTokenizer(message);
		ArrayList<String> lines = new ArrayList<String>();
		while (part.hasMoreTokens()) {
			lines.add(part.nextToken());
		}
		int i = 0;
		while (i < lines.size()) {
			String first = lines.get(i++);
			if (first.equals("received")) {
				first = lines.get(i++);
				Player p = parent.getPlayer(first);
				p.setInfo(lines.subList(i, i + 8));
				p.hasLevel = true;
				i += 8;
				continue;
			}
			Player p = parent.getPlayer(first);
			if (p == null) {
				String name = first;
				String col = lines.get(i++);
				int x = Integer.parseInt(lines.get(i++));
				int y = Integer.parseInt(lines.get(i++));
				Player player = new Player(name, ip, x, y, col, level);
				parent.addPlayer(player);
			} else {
				p.setInfo(lines.subList(i, i + 8));
				i += 8;
			}
		}
	}
}
