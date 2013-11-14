package main.states;

import main.Art;
import main.MainGame;
import main.gui.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.state.*;

public class NetworkedGameCreation extends BasicGameState {
	int id;
	private Button backButton;
	private Button joinButton;
	private TextField ipAddress;
	private TextField name;
	private RadioButtonGroup colorPick;
	
	public NetworkedGameCreation(int id) {
		this.id = id;
	}
	
	public void init(final GameContainer gc, final StateBasedGame sbg) throws SlickException {
		joinButton = new Button("Join", new RoundedRectangle(400, 300, 100, 30, 13), Color.white, Color.yellow, Color.orange) {
			public void onClick() {
				((NetworkedGame)sbg.getState(MainGame.networkedGame)).start(name.getText(),colorPick.getSelected().getInfo(), ipAddress.getText(), gc, sbg);
				sbg.enterState(MainGame.networkedGame);
			}
		};
		
		backButton = new Button("Back", new RoundedRectangle(200, 300, 100, 30, 13), Color.white, Color.yellow, Color.orange) {
			public void onClick() {
				sbg.enterState(MainGame.menu);
			}
		};
		ipAddress = new TextField(250, 100, 100, 20, "IP Adrress: ");
		name = new TextField(250, 130, 100, 20, "Player Name: ");
		colorPick = new RadioButtonGroup("colors", 250, 160);
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		backButton.update(gc, sbg, delta);
		joinButton.update(gc, sbg, delta);
		ipAddress.update(gc, sbg, delta);
		name.update(gc, sbg, delta);
		colorPick.update(gc, sbg, delta);
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		Art.menuBackground.draw(0,0,MainGame.WIDTH/720.0f);
		backButton.render(gc, sbg, g);
		joinButton.render(gc, sbg, g);
		ipAddress.render(gc, sbg, g);
		name.render(gc, sbg, g);
		colorPick.render(gc, sbg, g);
		g.drawString("Color: ", colorPick.getX() - 70, colorPick.getY());
	}
	
	public int getID() {
		return id;
	}
}
