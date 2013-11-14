package main.states;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import main.levels.ClientLevel;
import main.levels.Level;
import main.server.ReceiveThread;
import main.server.Server;
import main.tanks.Tank;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LocalGame extends BasicGameState{
	private int id;
	private Level level;
	public Server server;
	public ArrayList<Tank> tanks;
	ReceiveThread receiveThread;
	DatagramSocket socket;
	private String serverIP;
	
	public LocalGame(int id) {
		this.id = id;
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		System.out.println("Inizializing local game");
		try {
			serverIP = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		level.update(gc, sbg, delta);
		for (Tank t : tanks) {
			t.update(gc, sbg, delta);
		}
		sendMessage(getMessage());
		server.update(gc, sbg, delta);
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
	
	public void start(GameContainer gc, StateBasedGame sbg, Tank ... tankArray) throws SlickException {
		tanks = new ArrayList<Tank>();
		server = new Server();
		server.init(gc, sbg);
		this.level = new ClientLevel(server.getGround());
		level.init(gc, sbg);
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < tankArray.length; i++) {
			Tank t = tankArray[i];
			if (t == null) continue;
			t.init(gc, sbg, level);
			
			tanks.add(t);
			server.addPlayer(t);
		}
		receiveThread = new ReceiveThread(tanks, socket, level);
		receiveThread.start();
	}
	
	public void sendMessage(String message) {
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
	
	public String getMessage() {
		String message = "";
		for (Tank t : tanks) {
			message += t.name + " ";
			if (t.moveLeft) 
				message += "1 ";
			else
				message += "0 ";
			if (t.moveRight) 
				message += "1 ";
			else
				message += "0 ";
			if (t.angleUp) 
				message += "1 ";
			else
				message += "0 ";
			if (t.angleDown) 
				message += "1 ";
			else
				message += "0 ";
			if (t.powerDown) 
				message += "1 ";
			else
				message += "0 ";
			if (t.powerUp) 
				message += "1 ";
			else
				message += "0 ";
			if (t.fire) 
				message += "1 ";
			else
				message += "0 ";
			message += t.selectedBullet + " ";
			t.moveLeft = t.moveRight = t.angleDown = t.angleUp = t.powerDown = t.powerUp = t.fire = false;
		}
		return message;
	}
}
