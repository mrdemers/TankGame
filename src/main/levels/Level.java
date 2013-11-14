package main.levels;

import java.util.ArrayList;
import java.util.Random;

import main.MainGame;
import main.bullets.Bullet;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Level {
	public final int width = MainGame.WIDTH, height = MainGame.HEIGHT - 100;
	public boolean[][] ground = new boolean[width][height];
	public int wind = 40;
	public ArrayList<Bullet> bullets;
	public ArrayList<Bullet> bulletsToAdd;
	public static float gravity = .1f;
	public int seed;
	public boolean hasLevel;
	public ArrayList<Light> lights;
	
	public Level() {
		seed = (int)(Math.random() * 100000);
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		bullets = new ArrayList<Bullet>();
		bulletsToAdd = new ArrayList<Bullet>();
		Random rand = new Random(seed);
		mountains(rand.nextInt(2) + 2);
	}
	
	
	public void hills() {
		Random rand = new Random(seed);
		int waves = (int)(rand.nextDouble() * 15 + 10);
		int offset = (int)(rand.nextDouble() * 15);
		for (int x = 0; x < ground.length; x++) {
			int start = height - (int)((Math.cos(x/Math.PI/waves + offset) + 2) * 50);
			for (int y = start; y < ground[0].length; y++) {
				ground[x][y] = true;
			}
		}
	}
	
	public void mountains() {
		Random random = new Random(seed);
		int oscilations = random.nextInt(10) + 1;
		System.out.println("# of oscilations " + oscilations);
		double lastLength = 0;
		double length = width/oscilations;
		for (int osc = 0; osc < oscilations; osc++) {
			int maxHeight = 200 - random.nextInt(100);
			double randLength = (random.nextDouble() + 1) * Math.PI;
			System.out.println("Max Height: " + maxHeight + ", rand length " + randLength);
			int start = 0;
			for (int x = 0; x < length; x++) {
				// 0 -> x = 0 -> 2PI
				double sin = Math.sin(x * randLength/length - lastLength);
				int height = (int)((sin + 1) * maxHeight);
				start = height - height;
				for (int y = start; y < ground[0].length; y++) {
					ground[x + (int)(length * osc)][y] = true;
				}
			}
			lastLength = randLength;
		}
	}
	
	public void mountains(double a) {
		double length = width;
		for (int x = 0; x < length; x++) {
			double sum = 0;
			for (int i = 1; i < 400; i++) {
				sum += Math.sin(Math.PI * Math.pow(i, a) * (x/length))/(Math.PI * Math.pow(i, a));
			}
			int start = height - (int)(Math.abs(sum) * 600);
			for (int y = start; y < ground[0].length; y++) {
				ground[x][y] = true;
			}
		}
	}
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update(this, delta);
		}
		for (int i = 0; i < bulletsToAdd.size();) {
			bullets.add(bulletsToAdd.remove(i));
		}
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {

	}
	
	public void redrawGround() throws SlickException {
		
	}
	
	public boolean addBullet(Bullet b) {
		return bulletsToAdd.add(b);
	}
	
	public static void destroyGround(int xPos, int yPos, int radiusX, int radiusY, Level level) {
		for (int y = yPos - radiusY; y < yPos + radiusY; y++) {
			for (int x = xPos - radiusX; x < xPos + radiusX; x++) {
				if (x >= 0 && x < level.ground.length && y >= 0 && y < level.ground[0].length) { 
					float xx = x - xPos;
					float yy = y - yPos;
					float dist = (float)(xx * xx / (radiusX * radiusX) + yy * yy / (radiusY * radiusY));
					if (dist < 1) {
						level.ground[x][y] = false;
					}
				}
			}
		}
	}
}
