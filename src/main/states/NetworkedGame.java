package main.states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import main.Art;
import main.levels.ClientLevel;
import main.levels.Level;
import main.server.ReceiveThread;
import main.tanks.PlayerTank;
import main.tanks.Tank;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class NetworkedGame extends BasicGameState{
	private int id;
	
	ReceiveThread receiveThread;
	DatagramSocket socket;
	boolean init = false;
	String serverIP;
	public ArrayList<Tank> tanks;
	public ArrayList<Tank> toAdd;
	public Level level;
	public PlayerTank myTank;
	
	public NetworkedGame(int id) {
		this.id = id;
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		if (init) return;
		init = true;
		System.out.println("Initializing Network");
	}
	
	public void start(String name, String col, String serverIP, GameContainer gc, StateBasedGame sbg) {
		this.serverIP = "192.168.1.8";
		try {
			socket = new DatagramSocket();
		} catch (IOException e) {
			System.out.println("Couldnt make a socket");
			e.printStackTrace();
		}
		receiveThread = new ReceiveThread(tanks, socket, null);
		receiveThread.start();
		myTank = new PlayerTank(col, name, 50, 50);
		long curr = System.currentTimeMillis();
		long last = curr;
		long passed = curr - last;
		while (!receiveThread.hasLevel()) {
			if (passed > 100) {
				sendPacket(getMessage());
				passed = 0;
			}
			curr = System.currentTimeMillis();
			passed += curr - last;
			last = curr;
		}
		level = receiveThread.getLevel();
		try {
			level.init(gc, sbg);
			myTank.init(gc, sbg, level);
		} catch (SlickException e1) {
			e1.printStackTrace();
		}
		tanks = new ArrayList<Tank>();
		tanks.add(myTank);
		receiveThread.setTanks(tanks);
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		level.update(gc, sbg, delta);
		for (int i = 0; i < ((ClientLevel)level).toAddTank.size(); i++) {
			Tank t = ((ClientLevel)level).toAddTank.remove(i--);
			t.init(gc, sbg, level);
			tanks.add(t);
		}
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (t.gone()) {
				tanks.remove(i--);
				continue;
			}
			t.update(gc, sbg, delta);
		}
		if (receiveThread.gettingInfo())
			sendPacket(getMessage());
		else {
			sendPacket("received " + getMessage());
		}
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		level.render(gc, sbg, g);
		for (Tank t : tanks) {
			t.render(gc, sbg, g);
		}
	}
	
	public int getID() {
		return id;
	}
	boolean firstTime = true;
	public String getMessage()
	{
		String message = myTank.name+" ";
		if (firstTime) {
			firstTime = false;
			message += Art.getColor(myTank.color) + " ";
			message += myTank.getX() + " ";
			message += myTank.getY() + " ";
			return message;
		}
		//send key presses
		if (myTank.moveLeft) 
			message += "1 ";
		else
			message += "0 ";
		if (myTank.moveRight) 
			message += "1 ";
		else
			message += "0 ";
		if (myTank.angleUp) 
			message += "1 ";
		else
			message += "0 ";
		if (myTank.angleDown) 
			message += "1 ";
		else
			message += "0 ";
		if (myTank.powerDown) 
			message += "1 ";
		else
			message += "0 ";
		if (myTank.powerUp) 
			message += "1 ";
		else
			message += "0 ";
		if (myTank.fire) 
			message += "1 ";
		else
			message += "0 ";
		message += myTank.selectedBullet + " ";
		myTank.moveLeft = myTank.moveRight = myTank.angleDown = myTank.angleUp = myTank.powerDown = myTank.powerUp = myTank.fire = false;
		return message;
	}
	
	public void sendPacket(String message)
	{
		try
		{
			
			byte[] buf = message.getBytes();
			
	        InetAddress address = InetAddress.getByName(serverIP);
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6062);
	        socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Failed to Send");
		}
	}
	
	public void addTank(Tank t) {
		toAdd.add(t);
	}
}
