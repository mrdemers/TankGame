package main.states;

import main.Art;
import main.MainGame;
import main.gui.Button;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.*;

public class Menu extends BasicGameState{
	private int id;
	Button localGameBtn;
	Button networkedGameBtn;
	
	public Menu(int id) {
		this.id = id;
	}
	
	public void init(GameContainer gc, final StateBasedGame sbg) throws SlickException {
		int buttonWidth = 200;
		int buttonHeight = 50;
		localGameBtn = new Button("Local Game", new Rectangle(MainGame.WIDTH/2 - buttonWidth/2, 100, buttonWidth, buttonHeight), Color.blue, Color.yellow, Color.orange) {
			public void onClick() {
				System.out.println("goint to local creation screen");
				sbg.enterState(MainGame.localGameCreation);
			}
		};
		
		networkedGameBtn = new Button("Network Game", new Rectangle(MainGame.WIDTH/2 - buttonWidth/2, 170, buttonWidth, buttonHeight), Color.blue, Color.yellow, Color.orange) {
			public void onClick() {
				System.out.println("Joining network game");
				sbg.enterState(MainGame.networkedGameCreation);
			}
		};
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		localGameBtn.update(gc, sbg, delta);
		networkedGameBtn.update(gc, sbg, delta);
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		Art.sky.draw(0,0,MainGame.WIDTH/720.0f);
		Art.background.draw(0,0,MainGame.WIDTH/720.0f);
		
		localGameBtn.render(gc, sbg, g);
		networkedGameBtn.render(gc, sbg, g);
	}
	
	public int getID() {
		return id;
	}
}
