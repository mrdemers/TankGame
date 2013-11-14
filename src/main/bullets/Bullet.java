package main.bullets;

import main.MainGame;
import main.levels.Level;
import main.levels.Light;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.StateBasedGame;

public class Bullet {
	public float x, y, dX, dY;
	public int type, id;
	public int width, height;
	public int destroyX, destroyY;
	public int dmgRadiusX, dmgRadiusY;
	public boolean dead;
	public static int numBullets;
	public int deadTime = 0;
	public Level level;
	public Light light;
	
	public Bullet(float x, float y, int type, int id) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.id = id;
		dmgRadiusX = getDmgRadius(type)[0];
		dmgRadiusY = getDmgRadius(type)[1];
		width = (int)Math.ceil(dmgRadiusX/2);
		height = (int)Math.ceil(dmgRadiusY/2);
		if (MainGame.useLighting)
			this.light = new Light(x,y,50, .9f, 1);
	}
	
	public Bullet(float x, float y, float dX, float dY, int type) {
		this.x = x;
		this.y = y;
		this.dX = dX;
		this.dY = dY;
		this.type = type;
		this.id = numBullets++;
		dmgRadiusX = getDmgRadius(type)[0];
		dmgRadiusY = getDmgRadius(type)[1];
		width = (int)Math.ceil(dmgRadiusX/2);
		height = (int)Math.ceil(dmgRadiusY/2);
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) {
		
	}
	
	public void update(Level level, int delta) {
		if (dead) return;
		x += dX;
		y += dY;
		if (x > level.ground.length || x + width < 0 || y > level.ground[0].length) {
			dead = true;
			destroyX = (int)x;
			destroyY = (int)y;
		}
		dY += Level.gravity;
		dX += level.wind * (delta/50000f);
		for (int yy = (int)y; yy < y + height; yy++) {
			for (int xx = (int)x; xx < x + width; xx++) {
				if (xx >= 0 && xx < level.ground.length && yy >= 0 && yy < level.ground[0].length && !dead) {
					if (level.ground[xx][yy]) {
						dead = true;
						Level.destroyGround(xx, yy, dmgRadiusX, dmgRadiusY, level);
						destroyX = xx;
						destroyY = yy;
					}
				}
			}
		}
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		deadTime++;
		g.setColor(Color.white);
		g.fillOval((int)x, (int)y, (int)Math.ceil(dmgRadiusX/2), (int)Math.ceil(dmgRadiusY/2));
	}
	
	public void setX(float x) { 
		this.x = x;
	}
	public void setY(float y) { 
		this.y = y; 
	}
	public void setPos(float x, float y) {
		setX(x);
		setY(y);
		deadTime = 0;
		if (MainGame.useLighting)
			this.light.move(x,y);
	}
	public boolean dead() {
		return deadTime > 200;
	}
	
	public String getInfo() {
		String ret = id + " ";
		if (dead) {
			ret += destroyX + " ";
			ret += destroyY + " ";
		} else { 
			ret += x + " ";
			ret += y + " ";
		}
		ret += type + " ";
		ret += dead? "1 " : "0 ";
		return ret;
	}
	
	public static int[] getDmgRadius(int type) {
		switch (type) {
		case 1: 
			return new int[]{5, 5};
		case 2: 
			return new int[]{10,10};
		case 3: 
			return new int[]{20,20};
		case 4:
			return new int[]{10, 10};
		}
		return new int[]{0,0};
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		
		Bullet b = (Bullet)o;
		return b.id == this.id; 
	}
}
