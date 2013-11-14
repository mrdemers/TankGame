package oldserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

public class TankGameServer extends JFrame {
	private static final long serialVersionUID = 1L;
	public static JTextArea area;
	private int width = 200, height = 100;

	public static void main(String[] args) {
		new TankGameServer();
	}

	public TankGameServer() {
		super("Tank Server");
		setSize(width, height);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.white);
		area = new JTextArea(20, 50);
		area.setEnabled(false);
		area.setText("Tank Game Server");
		add(area, BorderLayout.CENTER);
		pack();
		new GameThread("Tank Game").start();
	}

	public static void writeText(String text) {
		area.setText(area.getText() + "\n" + text);
	}
}

class GameThread extends Thread {
	ServerThread server;
	// frames per second counter variables
	long prevTime = System.currentTimeMillis();
	float elapsedTime = 0;
	int prevSec = 0;
	int fps = 0;
	int frameCount = 0;

	// game flag
	boolean gameRunning = true;

	// All players
	PlayerList playerList = new PlayerList();

	public GameThread(String name) {
		super(name);
		server = new ServerThread(playerList);
		server.start();
	}

	public void run() {
		while (gameRunning) {
			playerList.advance(elapsedTime);

			calcElapsedTime();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				gameRunning = false;
			}
		}
	}

	public void calcElapsedTime() {
		elapsedTime = (float) (System.currentTimeMillis() - prevTime);
		prevTime = System.currentTimeMillis();
		if (prevSec != (int) (System.currentTimeMillis() / 1000)) {
			fps = frameCount;
			frameCount = 0;
		}
		frameCount++;
		prevSec = (int) (System.currentTimeMillis() / 1000);
	}
}

class ServerThread extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;

	protected boolean runServer = true;

	protected PlayerList playerList;

	public ServerThread(PlayerList pL) {
		super("Server Thread");
		playerList = pL;
		try {
			// creates the socket
			socket = new DatagramSocket(6062);
			TankGameServer.writeText(InetAddress.getLocalHost().getHostName()
					+ "\n" + InetAddress.getLocalHost().getHostAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (runServer) {
			try {
				receivePacket();
				sendPacket();
			} catch (IOException e) {
				e.printStackTrace();
				runServer = false;
			}
		}
		socket.close();
	}

	public void receivePacket() throws IOException {
		// receive message from client
		byte[] buf = new byte[256];
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);

		// translate message in a thread
		String message = new String(packet.getData(), 0, packet.getLength());
		Translator translator = new Translator(playerList, message);
		translator.start();
	}

	public void sendPacket() throws IOException {
		// create message of all players key presses
		byte[] buf = playerList.toString().getBytes();

		// send the message to the client to the given address and port
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		packet = new DatagramPacket(buf, buf.length, address, port);
		socket.send(packet);
	}
}

class Translator extends Thread {
	protected PlayerList playerList;
	protected String message;

	public Translator(PlayerList pL, String m) {
		playerList = pL;
		message = m;
	}

	public synchronized void run() {
		StringTokenizer part = new StringTokenizer(message);
		String start = part.nextToken();
		if (start.equals("add")) {
			String name = part.nextToken();
			String color = part.nextToken();
			String xPosition = part.nextToken();
			String yPosition = part.nextToken();
			playerList.add(new Player(name, color, Integer.parseInt(xPosition),
					Integer.parseInt(yPosition)));
		} else {
			Player player = playerList.getByName(start);

			player.resetKeys();
			int x = Integer.parseInt(part.nextToken());
			player.x = x;
			int y = Integer.parseInt(part.nextToken());
			player.y = y;

			int temp = Integer.parseInt(part.nextToken());
			if (temp == 1)
				player.left = true;
			else
				player.left = false;
			temp = Integer.parseInt(part.nextToken());
			if (temp == 1)
				player.right = true;
			else
				player.right = false;
			temp = Integer.parseInt(part.nextToken());
			if (temp == 1)
				player.angleUp = true;
			else
				player.angleUp = false;
			temp = Integer.parseInt(part.nextToken());
			if (temp == 1)
				player.angleDown = true;
			else
				player.angleDown = false;
			temp = Integer.parseInt(part.nextToken());
			if (temp == 1)
				player.powerUp = true;
			else
				player.powerUp = false;
			temp = Integer.parseInt(part.nextToken());
			if (temp == 1)
				player.powerDown = true;
			else
				player.powerDown = false;
			temp = Integer.parseInt(part.nextToken());
			if (temp == 1)
				player.fire = true;
			else
				player.fire = false;
		}
	}
}

class PlayerList {
	// player list
	protected LinkedList<Player> list = new LinkedList<Player>();

	public PlayerList() {
	}

	public void add(Player p) {
		list.add(p);
	}

	public void advance(float elapsedTime) {
		for (int x = list.size() - 1; x >= 0; x--) {
			list.get(x).advance(elapsedTime);
			if (list.get(x).getKick()) {
				TankGameServer.writeText(list.get(x).name + " Timed Out");
				list.remove(x);
			}
		}
	}

	public String toString() {
		String info = "";
		Player player;
		for (int x = 0; x < list.size(); x++) {
			player = list.get(x);
			if (player.getKick()) {
				info += "kick";
			}
			info += player.name + " ";
			info += player.color + " ";
			info += player.x + " ";
			info += player.y + " ";

			if (player.left)
				info += "1 ";
			else
				info += "0 ";
			if (player.right)
				info += "1 ";
			else
				info += "0 ";
			if (player.angleUp)
				info += "1 ";
			else
				info += "0 ";
			if (player.angleDown)
				info += "1 ";
			else
				info += "0 ";
			if (player.powerUp)
				info += "1 ";
			else
				info += "0 ";
			if (player.powerDown)
				info += "1 ";
			else
				info += "0 ";
			if (player.fire)
				info += "1 ";
			else
				info += "0 ";
		}
		return info;
	}

	public Player getByName(String n) {
		for (int x = 0; x < list.size(); x++) {
			if (list.get(x).name.equals(n)) {
				return list.get(x);
			}
		}
		return null;
	}

	public Player getPlayer(int index) {
		return list.get(index);
	}

	public int size() {
		return list.size();
	}
}

class Player {
	public boolean angleUp, angleDown;
	public boolean powerUp, powerDown;
	public boolean left, right, fire;
	public int x, y;
	public String color;

	public String name = "";
	// time out counter
	int timeOut = 0, kickTime = 1000;
	boolean kick = false;

	public Player(String name, String col, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.color = col;
		TankGameServer.writeText(name + " connected");
	}

	public void advance(float delta) {
		timeOut += delta;
		if (timeOut > kickTime)
			kick = true;
	}

	public void resetKeys() {
		timeOut = 0;
		angleUp = false;
		angleDown = false;
		powerUp = false;
		powerDown = false;
		left = false;
		right = false;
		fire = false;
	}

	public boolean getKick() {
		return false;
	}
}
