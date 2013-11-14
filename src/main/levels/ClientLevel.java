package main.levels;

import java.util.ArrayList;
import java.util.Random;

import main.Art;
import main.MainGame;
import main.bullets.Bullet;
import main.particles.ExplosionEmitter;
import main.tanks.Tank;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;

public class ClientLevel extends Level {
	private Image groundImage;
	private Image backImage;
	public int cloud1, cloud2, cloud3;
	public ArrayList<Bullet> bullets;
	public ArrayList<Bullet> toAdd;
	public ArrayList<Integer> toRemove;
	public static float gravity = .1f;
	public ParticleSystem particleSystem;
	public boolean isDirty;
	public ExplosionEmitter eem;
	public ArrayList<Tank> toAddTank; 
	
	public static int[] lightMap;
	
	public ClientLevel(){
		for (int y = 0; y < ground[0].length; y++) {
			for (int x = 0; x < ground.length; x++) {
				if (y > 300)
					ground[x][y] = true;
			}
		}
	}
	public ClientLevel(boolean[][] gr) {
		ground = gr.clone();
	}
	
	public ClientLevel(int seed) {
		Random random = new Random(seed);
		double a = random.nextInt(2)+2;
		double length = width;
		for (int x = 0; x < width; x++) {
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
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		cloud1 = 0;
		cloud2 = -600;
		cloud3 = -1050;
		bullets = new ArrayList<Bullet>();
		toAdd = new ArrayList<Bullet>();
		toRemove = new ArrayList<Integer>();
		toAddTank = new ArrayList<Tank>();
		lights = new ArrayList<Light>();
		particleSystem = new ParticleSystem(Art.explosion);
		particleSystem.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		groundImage = new Image(width, height);
		backImage = Art.background.copy();
		lightMap = new int[width * height];
		for (int i = 0; i < lightMap.length; i++) {
			lightMap[i] = 0;
		}
		lights.add(new Light(500, 0, 800, .9f, 0));
		//lights.add(new Light(0, 0, 400, .7f, 0));
		redrawGround();
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		MainGame.useLighting = false;
		int amount = wind / delta / 2;
		cloud1 += amount;
		cloud2 += amount;
		cloud3 += amount;
		if (cloud1 > 1200) {
			cloud1 = -350;
		}
		if (cloud2 > 1700) {
			cloud2 = -550;
		}
		if (cloud3 > 2000) {
			cloud3 = -300;
		}
		for (int i = 0; i < toAdd.size(); i++) {
			Bullet b = toAdd.remove(i);
			lights.add(b.light);
			bullets.add(b);
		}
		for (int i = 0; i < toRemove.size(); i++) {
			for (int j = 0; j < bullets.size(); j++) {
				if (bullets.get(j).id == toRemove.get(i)) {
					Bullet b = bullets.remove(j--);
					lights.remove(b.light);
				}
			}
		}
		if (eem != null) {
			particleSystem.addEmitter(eem);
			eem = null;
		}
		for (Bullet b : bullets) {
			if (b.dead()) {
				destroyGround((int)(b.x + b.width/2), (int)(b.y + b.height/2), b.dmgRadiusX, b.dmgRadiusY, this);
				toRemove.add(b.id);
			}
		}
		if (isDirty) {
			if (MainGame.useLighting) {
				for (int i = 0; i < lightMap.length; i++) {
					lightMap[i] = 0;
				}
				for (Light l : lights) {
					l.updateMap();
				}
			}
			redrawGround();
			isDirty = false;
		}
		particleSystem.update(delta);
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		Art.sky.draw(0,0,width/720.0f);
		Art.cloud1.draw(cloud1, 20);
		Art.cloud2.draw(cloud2, 100);
		backImage.draw(0,0,width/720.0f);
		Art.cloud3.draw(cloud3, 40);
		groundImage.draw(0,0);
		particleSystem.render();
		for (Bullet b : bullets) {
			b.render(gc, sbg, g);
		}
	}
	
	public void redrawGround() throws SlickException {
		ImageBuffer imgb = new ImageBuffer(groundImage.getWidth(), groundImage.getHeight());
		ImageBuffer backBuffer = new ImageBuffer(backImage.getWidth(), backImage.getHeight());
		for (int y = 0; y < ground[0].length; y++) {
			for (int x = 0; x < ground.length; x++) {
				if (ground[x][y]) {
					// Draws the ground with light
					// val = 0 means no light
					// val = 255 means full light
					float val = (lightMap[x + y * width] & 0xff) / 255.0f;
					int color = lightMap[x + y * width] >> 8;
					if (color != 0) {
						Color c = groundImage.getColor(x, y);
						imgb.setRGBA(x, y, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
						continue;
					}
					int rr = color >> 16 & 0xff;
					int gg = color >> 8 & 0xff;
					int bb = color & 0xff;
					Color c = new Color(rr, gg, bb);
					Color toUse = new Color(c.r * val, .5f * val + c.g * val, c.b * val);
					imgb.setRGBA(x, y, toUse.getRed(), toUse.getGreen(), toUse.getBlue(), toUse.getAlpha());
				} else {
					float val = (lightMap[x + y * width] & 0xff) / 255.0f;
					int color = lightMap[x + y * width] >> 8;
					int rr = color >> 16 & 0xff;
					int gg = color >> 8 & 0xff;
					int bb = color & 0xff;
					Color c = new Color(rr, gg, bb);
					Color toUse = new Color(c.r * val, c.g * val, c.b * val, val);
					Color old = Art.background.getColor(x, y);
					Color now = null;
					if (old.getAlpha() > 0) { 
						now = new Color(old.r * val + toUse.r * val, old.g * val + toUse.r * val, old.b * val + toUse.b * val);
						backBuffer.setRGBA(x, y, now.getRed(), now.getGreen(), now.getBlue(), now.getAlpha());
					}
					else {
						old = Art.sky.getColor(x, y);
						now = new Color(old.r * val + toUse.r * val, old.g * val + toUse.r * val, old.b * val + toUse.b * val);
						backBuffer.setRGBA(x, y, now.getRed(), now.getGreen(), now.getBlue(), now.getAlpha());
					}
				}
			}
		}
		groundImage = new Image(imgb);
		backImage = new Image(backBuffer);
	}
	
	public boolean addBullet(Bullet b) {
		return bullets.add(b);
	}
	
	public void addParticles(float x, float y) {
		eem = new ExplosionEmitter(x, y);
	}
}
