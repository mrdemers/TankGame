package main;

import main.states.*;	

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class MainGame extends StateBasedGame{
	public static final String gameName = "Synergy";
	public static final int WIDTH = 720;
	public static final int HEIGHT = 500;
	public static final int CENTER = WIDTH/2;
	public static final int menu = 0;
	public static final int localGame = 1;
	public static final int networkedGame = 2;
	public static final int localGameCreation = 3;
	public static final int networkedGameCreation = 4;
	public static boolean useLighting = false;

	public MainGame(String name) {
		super(name);
		this.addState(new Menu(menu));
		this.addState(new LocalGame(localGame));
		this.addState(new NetworkedGame(networkedGame));
		this.addState(new LocalGameCreation(localGameCreation));
		this.addState(new NetworkedGameCreation(networkedGameCreation));
	}

	public void initStatesList(GameContainer gc) throws SlickException {
		System.out.println("Starting");
		this.getState(menu).init(gc, this);
		this.getState(localGame).init(gc, this);
		this.getState(networkedGame).init(gc,this);
		this.getState(localGameCreation).init(gc, this);
		this.getState(networkedGameCreation).init(gc, this);
		this.enterState(localGameCreation);
	}

	public static void main(String[] args) {
		AppGameContainer appgc;
		try {
			appgc = new AppGameContainer(new MainGame(gameName));
			appgc.setDisplayMode(WIDTH, HEIGHT, false);
			appgc.setMaximumLogicUpdateInterval(20);
			appgc.setMinimumLogicUpdateInterval(20);
			appgc.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
