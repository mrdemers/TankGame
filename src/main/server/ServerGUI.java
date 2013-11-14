package main.server;

import org.newdawn.slick.*;
import org.newdawn.slick.gui.*;

public class ServerGUI extends BasicGame {
	private Server server;
	private TextField text;

	public ServerGUI() {
		super("Server");
		server = new Server();
	}

	public void write(String message) {
		text.setText(text.getText() + "\n" + message);
	}

	public void addPlayer(Player player) {
		server.addPlayer(player);
		write(player.name + " connected");
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setColor(Color.white);
		g.fillRect(0, 0, 400, 300);
		g.setColor(Color.black);
		text.render(gc, g);
	}

	public void init(GameContainer gc) throws SlickException {
		text = new TextField(gc, gc.getDefaultFont(), 0, 0, 400, 300);
		server.init(gc, null);
		text.setAcceptingInput(false);
		String name = server.getHostName(), address = server.getHostAddress();
		text.setText("Tank Server" + "\n" + name + "\n" + address);
	}

	public void update(GameContainer gc, int delta) throws SlickException {
		if (server.toAdd.size() > 0) {
			for (Player p : server.toAdd) {
				write(p.name + " connected from " + p.ip);
				write("now " + (server.getPlayers().size() + 1) + " players in the game");
			}
		}
		for (Player p : server.getPlayers()) {
			if (p.gone()) {
				write(p.name + " disconnected");
				write("now " + (server.getPlayers().size() - 1) + " players in the game");
				server.removePlayer(p);
			}
		}
		server.update(gc, null, delta);
	}
	
	public static void main(String[] args) {
		AppGameContainer gc;
		try {
			gc = new AppGameContainer(new ServerGUI());
			gc.setDisplayMode(400, 300, false);
			gc.setMaximumLogicUpdateInterval(20);
			gc.setMinimumLogicUpdateInterval(20);
			gc.setShowFPS(false);
			gc.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
