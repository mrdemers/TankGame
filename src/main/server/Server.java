package main.server;

import java.util.ArrayList;

import main.Art;
import main.levels.Level;
import main.tanks.Tank;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Server {
	private ServerThread serverThread;
	private PlayerList playerList;
	public ArrayList<Player> toAdd;
	public ArrayList<Player> toRemove;
	private Level level;
	
	public Server() {
		level = new Level();
		playerList = new PlayerList();
	}
	public Server(PlayerList playerList) {
		this.playerList = playerList;
		level = new Level();
	}
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		level.init(gc, sbg);
		toAdd = new ArrayList<Player>();
		toRemove = new ArrayList<Player>();
		serverThread = new ServerThread(playerList, level);
		serverThread.addParent(this);
		serverThread.start();
	}
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		level.update(gc, sbg, delta);
		playerList.update(gc, sbg, delta);
		for (int i = 0; i < toAdd.size(); i++) {
			playerList.add(toAdd.remove(i--));
			System.out.println("Removed from toAdd");
		}
		for (int i = 0; i < toRemove.size(); i++) {
			playerList.players.remove(toRemove.remove(i--));
		}
	}
	public void addPlayer(Tank player) {
		Player p = new Player(player.name, "127.0.0.1", player.getX(), player.getY(), Art.getColor(player.color), level);
		p.hasLevel = true;
		playerList.add(p);
	}
	public void addPlayer(Player player) {
		toAdd.add(player);
	}
	public boolean[][] getGround() {
		return level.ground;
	}
	public String getHostName() { return serverThread.hostName; }
	public String getHostAddress() { return serverThread.hostAddress; }
	public ArrayList<Player> getPlayers() {
		return playerList.players;
	}
	public void removePlayer(Player p) {
		toRemove.add(p);
	}
	
	public Player getPlayer(String name) {
		for (Player p : playerList.players) {
			if (p.name.equals(name)) 
				return p;
		}
		for (Player p : toAdd) {
			if (p.name.equals(name)) {
				return p;
			}
		}
		return null;
	}
}
