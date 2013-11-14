package main.tanks;

import main.Art;
import main.levels.Level;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Tank {
	public Color color;
	public String name;
	public float power;
	public int angle;
	public Level currentLevel;
	private int posX, posY, width, height;
	private Image tankBarrel;
	public int kickTime;
	public boolean moveLeft, moveRight;
	public boolean angleUp, angleDown;
	public boolean powerUp, powerDown;
	public boolean fire;
	public int selectedBullet = 1;
	public int coolDownTime = 0;
	public int coolDown = 0;
	public static Tank dead = new Tank("white", "dead", -10, -10);

	/* how high the tank can climb walls */

	public Tank(String col, String name, int x, int y) {
		this.color = Art.getColor(col);
		this.name = name;
		power = 50;
		angle = 90;
		posX = x;
		posY = y;
		width = 30;
		height = 15;
	}

	public void init(GameContainer gc, StateBasedGame sbg, Level level)
			throws SlickException {
		tankBarrel = Art.tankBarrel.copy();
		tankBarrel.setCenterOfRotation(0, 2);
		tankBarrel.rotate(-angle);
		this.currentLevel = level;
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		tankBarrel.setRotation(-angle);
		kickTime++;
		if (fire) {
			switch (selectedBullet) {
			case 1: coolDownTime = 10; break;
			case 2: coolDownTime = 40; break;
			case 3: coolDownTime = 100; break;
			case 4: coolDownTime = 150; break;
			default: coolDownTime = 0;
			}
		}
		if (coolDownTime > 0) {
			coolDown++;
			if (coolDown > coolDownTime) {
				coolDownTime = 0;
				coolDown = 0;
			}
		}
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		tankBarrel.draw(posX + width/2,
				posY + height/2 - 3);
		Art.tank.draw(posX, posY, color);
		g.setColor(Color.white);
		g.drawString(name, posX-(name.length()*9)/2 + width/2, posY-30);
	}
	
	public boolean gone() {
		return kickTime > 60 * 5;
	}
	public int getX() { return posX; }
	public int getY() { return posY; }
	public void setX(int x) { 
		this.posX = x;
		kickTime = 0;
	}
	public void setY(int y) { this.posY = y; }
	public void setPos(int x, int y) {
		setX(x);
		setY(y);
	}
}
