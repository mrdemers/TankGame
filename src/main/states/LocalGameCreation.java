package main.states;

import main.Art;
import main.MainGame;
import main.gui.Button;
import main.gui.RadioButtonGroup;
import main.gui.TextField;
import main.tanks.ComputerTank;
import main.tanks.PlayerTank;
import main.tanks.Tank;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LocalGameCreation extends BasicGameState{
	private int id;
	private int numPlayers;
	private Button increasePlayers;
	private Button decreasePlayers;
	private Button cont;
	private Button start;
	private Button back;
	private TextField player1Name;
	private TextField player2Name;
	private TextField player3Name;
	private TextField player4Name;
	private RadioButtonGroup player1Buttons;
	private RadioButtonGroup player2Buttons;
	private RadioButtonGroup player3Buttons;
	private RadioButtonGroup player4Buttons;
	private boolean tankInfoScreen = false;
	
	public LocalGameCreation(int id) {
		this.id = id;
	}
	
	public void init(final GameContainer gc, final StateBasedGame sbg) throws SlickException {
		numPlayers = 2;
		increasePlayers = new Button(new Rectangle(MainGame.CENTER + 50, 200, 60, 80), Art.arrows[0]) {
			public void onClick() {
				if (numPlayers < 4) {
					numPlayers++;
				}
			}
		};
		
		decreasePlayers = new Button(new Rectangle(MainGame.CENTER - 50 - 60, 200, 60, 80), Art.arrows[0][0].getFlippedCopy(true, false), Art.arrows[0][1].getFlippedCopy(true, false), Art.arrows[0][2].getFlippedCopy(true, false)) {
			public void onClick() {
				if (numPlayers > 2) {
					numPlayers--;
				}
			}
		};
		
		cont = new Button("Continue", new RoundedRectangle(MainGame.CENTER - 50, 260, 100, 40, 19), Color.white, Color.yellow, Color.orange) {
			public void onClick() {
				tankInfoScreen = true;
				if (numPlayers == 2) {
					player1Name = new TextField(MainGame.CENTER - 125, 150, 80, 20, "Name: ");
					player2Name = new TextField(MainGame.CENTER + 75, 150, 80, 20, "Name: ");
					player1Buttons = new RadioButtonGroup("colors", player1Name.getX() + 3, player1Name.getY() + 30);
					player2Buttons = new RadioButtonGroup("colors", player2Name.getX() + 3, player2Name.getY() + 30);
				} else if (numPlayers == 3) {
					player1Name = new TextField(MainGame.CENTER - 250, 150, 80, 20, "Name: ");
					player2Name = new TextField(MainGame.CENTER - 50, 150, 80, 20, "Name: ");
					player3Name = new TextField(MainGame.CENTER + 150, 150, 80, 20, "Name: ");
					player1Buttons = new RadioButtonGroup("colors", player1Name.getX() + 3, player1Name.getY() + 30);
					player2Buttons = new RadioButtonGroup("colors", player2Name.getX() + 3, player2Name.getY() + 30);
					player3Buttons = new RadioButtonGroup("colors", player3Name.getX() + 3, player3Name.getY() + 30);
				} else if (numPlayers == 4) {
						player1Name = new TextField(MainGame.CENTER - 125, 50, 80, 20, "Name: ");
						player2Name = new TextField(MainGame.CENTER + 75, 50, 80, 20, "Name: ");
						player3Name = new TextField(MainGame.CENTER - 125, 250, 80, 20, "Name: ");
						player4Name = new TextField(MainGame.CENTER + 75, 250, 80, 20, "Name: ");
						player1Buttons = new RadioButtonGroup("colors", player1Name.getX() + 3, player1Name.getY() + 30);
						player2Buttons = new RadioButtonGroup("colors", player2Name.getX() + 3, player2Name.getY() + 30);
						player3Buttons = new RadioButtonGroup("colors", player3Name.getX() + 3, player3Name.getY() + 30);
						player4Buttons = new RadioButtonGroup("colors", player4Name.getX() + 3, player4Name.getY() + 30);
				}
			}
		};
		
		back = new Button("Back", new RoundedRectangle(20, 300, 100, 40, 19), Color.white, Color.yellow, Color.orange) {
			public void onClick() {
				if (tankInfoScreen) {
					tankInfoScreen = false;
				} else {
					sbg.enterState(MainGame.menu);
				}
			}
		};
		
		start = new Button("Start", new RoundedRectangle(MainGame.WIDTH - 120, 300, 100, 40, 19), Color.white, Color.yellow, Color.orange) {
			public void onClick() {
				Tank tank1 = new PlayerTank(player1Buttons.getSelected().getInfo(), player1Name.getText(), 50, 200);
				Tank tank2 = new ComputerTank(player2Buttons.getSelected().getInfo(), player2Name.getText(), 200, 200);
				Tank tank3 = null;
				Tank tank4 = null;
				if (numPlayers > 2) {
					tank3 = new PlayerTank(player3Buttons.getSelected().getInfo(), player3Name.getText(), 350, 200);
					if (numPlayers > 3) {
						tank4 = new PlayerTank(player4Buttons.getSelected().getInfo(), player4Name.getText(), 500, 200);
					}
				}
				try {
					((LocalGame)sbg.getState(MainGame.localGame)).start(gc, sbg, tank1, tank2, tank3, tank4);
				} catch (SlickException e) {
					e.printStackTrace();
				}
				sbg.enterState(MainGame.localGame);
			}
		};
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		back.update(gc, sbg, delta);
		if (!tankInfoScreen) {
			increasePlayers.update(gc, sbg, delta);
			decreasePlayers.update(gc, sbg, delta);
			cont.update(gc, sbg, delta);
		} else {
			player1Name.update(gc, sbg, delta);
			player2Name.update(gc, sbg, delta);
			player1Buttons.update(gc, sbg, delta);
			player2Buttons.update(gc, sbg, delta);
			if (numPlayers > 2) {
				player3Name.update(gc, sbg, delta);
				player3Buttons.update(gc, sbg, delta);
				if (numPlayers > 3) {
					player4Name.update(gc, sbg, delta);
					player4Buttons.update(gc, sbg, delta);
				}
			}
			start.update(gc, sbg, delta);
		}
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		Art.menuBackground.draw(0,0,MainGame.WIDTH/720.0f);
		back.render(gc, sbg, g);
		if (!tankInfoScreen) {
			increasePlayers.render(gc, sbg, g);
			decreasePlayers.render(gc, sbg, g);
			Art.numbers[0][numPlayers-2].draw(MainGame.CENTER - 19, 195);
			Art.numberOfPlayersInfo.draw(180, 100);
			g.setColor(Color.black);
			cont.render(gc, sbg, g);
		} else {
			g.drawString("Player 1", player1Name.getX(), player1Name.getY() - 20);
			g.drawString("Player 2", player2Name.getX(), player2Name.getY() - 20);
			player1Name.render(gc, sbg, g);
			player2Name.render(gc, sbg, g);
			g.drawString("Color: ", player1Name.getX() - 70, player1Name.getY() + 30);
			g.drawString("Color: ", player2Name.getX() - 70, player2Name.getY() + 30);
			player1Buttons.render(gc, sbg, g);
			player2Buttons.render(gc, sbg, g);
			if (numPlayers > 2) {
				g.drawString("Player 3", player3Name.getX(), player3Name.getY() - 20);
				g.drawString("Color: ", player3Name.getX() - 70, player3Name.getY() + 30);
				player3Name.render(gc, sbg, g);
				player3Buttons.render(gc, sbg, g);
				if (numPlayers > 3) {
					g.drawString("Player 4", player4Name.getX(), player4Name.getY() - 20);
					g.drawString("Color: ", player4Name.getX() - 70, player4Name.getY() + 30);
					player4Name.render(gc, sbg, g);
					player4Buttons.render(gc, sbg, g);
				}
			}
			start.render(gc, sbg, g);
		}
	}
	
	public int getID() {
		return id;
	}
}
