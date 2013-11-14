package main.tanks;

import main.Art;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import org.omg.IOP.IOR;

public class PlayerTank extends Tank{
	public int MOVE_LEFT = Input.KEY_A;
	public int MOVE_RIGHT = Input.KEY_D;
	public int ANGLE_UP = Input.KEY_W;
	public int ANGLE_DOWN = Input.KEY_S;
	public int POWER_UP = Input.KEY_E;
	public int POWER_DOWN = Input.KEY_Q;
	public int FIRE = Input.KEY_SPACE;
	public static int players = 0;
	public Image powerImage = Art.powerMeter;
	
	
	public PlayerTank(String col, String name, int x, int y) {
		super(col, name, x, y);
		players++;
		if (players == 2) {
			MOVE_LEFT = Input.KEY_LEFT;
			MOVE_RIGHT = Input.KEY_RIGHT;
			ANGLE_UP = Input.KEY_UP;
			ANGLE_DOWN = Input.KEY_DOWN;
			POWER_UP = Input.KEY_DELETE;
			POWER_DOWN = Input.KEY_END;
			FIRE = Input.KEY_RSHIFT;
		}
		if (players == 3) {
			MOVE_LEFT = Input.KEY_L;
			MOVE_RIGHT = Input.KEY_J;
			ANGLE_UP = Input.KEY_I;
			ANGLE_DOWN = Input.KEY_K;
			POWER_UP = Input.KEY_O;
			POWER_DOWN = Input.KEY_I;
			FIRE = Input.KEY_RETURN;
		}
		if (players == 4) {
			MOVE_LEFT = Input.KEY_NUMPAD6;
			MOVE_RIGHT = Input.KEY_NUMPAD4;
			ANGLE_UP = Input.KEY_NUMPAD8;
			ANGLE_DOWN = Input.KEY_NUMPAD5;
			POWER_UP = Input.KEY_NUMPAD9;
			POWER_DOWN = Input.KEY_NUMPAD7;
			FIRE = Input.KEY_ADD;
		}
		ImageBuffer buff = new ImageBuffer(Art.powerMeter.getWidth(), Art.powerMeter.getHeight());
		int numPixels = (int)(Art.powerMeter.getWidth() * (this.power/100));
		
		for (int xx = 0; xx < numPixels; xx++) {
			for (int yy = 0; yy < Art.powerMeter.getHeight(); yy++) {
				Color c = Art.powerMeter.getColor(xx, yy);
				buff.setRGBA(xx, yy, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			}
		}
		this.powerImage = new Image(buff);
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input =  gc.getInput();
		if (input.isKeyDown(MOVE_LEFT)) {
			moveLeft = true;
		}
		if (input.isKeyDown(MOVE_RIGHT)) {
			moveRight = true;
		}
		if (input.isKeyDown(ANGLE_UP)) {
			angleUp = true;
		}
		if (input.isKeyDown(ANGLE_DOWN)) {
			angleDown = true;
		}
		if (input.isKeyDown(POWER_UP)) {
			powerUp = true;
		}
		if (input.isKeyDown(POWER_DOWN)) {
			powerDown = true;
		}
		if (input.isKeyPressed(FIRE)) {
			fire = true;
		}
		if (input.isKeyPressed(Input.KEY_1)) {
			selectedBullet = 1;
		}
		if (input.isKeyPressed(Input.KEY_2)) {
			selectedBullet = 2;
		}
		if (input.isKeyPressed(Input.KEY_3)) {
			selectedBullet = 3;
		}
		if (input.isKeyPressed(Input.KEY_4)) {
			selectedBullet = 4;
		}
		super.update(gc, sbg, delta);
		if (powerUp || powerDown) {
			ImageBuffer buff = new ImageBuffer(Art.powerMeter.getWidth(), Art.powerMeter.getHeight());
			int numPixels = (int)(Art.powerMeter.getWidth() * (this.power/100));
			
			for (int x = 0; x < numPixels; x++) {
				for (int y = 0; y < Art.powerMeter.getHeight(); y++) {
					Color c = Art.powerMeter.getColor(x, y);
					buff.setRGBA(x, y, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
				}
			}
			this.powerImage = new Image(buff);
		}
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		super.render(gc, sbg, g);
		int startX = 600;
		int startY = 410;
		powerImage.draw(startX + 4, startY+2);
		Art.powerBar.draw(startX, startY);
		g.setColor(Color.white);
		g.drawString("Power: " + (int)power, startX-100, startY+20);
		g.drawString("Angle: " + angle, startX-100, startY+40);
		startX = 10;
		startY = 405;
		Art.smallBullet.draw(startX, startY);
		Art.mediumBullet.draw(startX+100, startY);
		Art.largeBullet.draw(startX+200, startY);
		Art.splitBullet.draw(startX+300, startY);
		if (coolDownTime <= 0) {
			Color c = new Color(255, 255, 0, 50);
			g.setColor(c);
			g.fillRoundRect(startX + (selectedBullet-1) * 100, startY, 90, 90, 13);
		} else {
			g.setColor(new Color(100, 100, 100, 200));
			for (int i = 0; i < 4; i++) {
				int x = startX + 100 * i;
				int y = startY;
				g.setClip(x, y, 90, 90);
				int end = (int)((double)coolDown / coolDownTime * 360);
				g.fillArc(x-15, y-15, 120, 120, 0, end);
				g.clearClip();
			}
		}
	}
}
