package main.server;

import java.util.ArrayList;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class PlayerList {
	public ArrayList<Player> players = new ArrayList<Player>();
	public PlayerList() {
		
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		for (Player p : players) {
			p.update(gc, sbg, delta);
		}
	}
	public void add(Player player) {
		players.add(player);
	}
	public String toString() {
		String ret = "";
		
		return ret;
	}
	public Color getColor(String col) {
		if (col.equals("red")) {
			return Color.red;
		}
		if (col.equals("blue")) {
			return Color.blue;
		}
		if (col.equals("black")) {
			return Color.gray;
		}
		if (col.equals("green")) {
			return Color.green;
		}
		if (col.equals("yellow")) {
			return Color.yellow;
		}
		return Color.white;
	}
	public Player getPlayer(String name) {
		for (Player p : players) {
			if (p.name.equals(name)) {
				return p;
			}
		}
		return null;
	}
}
