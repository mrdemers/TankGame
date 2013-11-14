package main.server;

import java.util.List;

import main.Art;
import main.MainGame;
import main.bullets.Bullet;
import main.bullets.SplitBullet;
import main.levels.Level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Player {
	public String ip;
	public String name;
	public boolean hasLevel = false; // TODO: level loading
	public int amountOfLevel = 0;
	public String col;
	public int width, height;
	public float x, y, angle;
	public float power, velocityY, speed;
	public int selectedBullet;
	public boolean left, right, aUp, aDown, pUp, pDown, fire;
	public Level level;
	public int kickTime = 0;
	private int coolDown, coolDownTime;

	public Player(String name, String ip, int x, int y, String col, Level level) {
		this.name = name;
		this.col = col;
		this.ip = ip;
		this.x = x;
		this.y = y;
		this.angle = 90;
		this.power = 50.0f;
		width = 30;
		height = 15;
		speed = 1;
		velocityY = 0;
		this.level = level;
	}

	public boolean gone() {
		return kickTime > 60 * 5;
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		kickTime++;
		if (!onGround()) {
			y = y + velocityY;
			velocityY += Level.gravity;

			if (onGround()) {
				velocityY = 0;
				int highestPoint = closestHighPoint();
				int position = highestPoint - height + 1;
				y = position;
			}
		} else {
			if (left && right) {

			} else if (left) {
				move(-1);
			} else if (right) {
				move(1);
			}
		}

		int rotateAmount = 1;
		if (aUp) {
			angle += rotateAmount;
		}
		if (aDown) {
			angle -= rotateAmount;
		}
		int px = 1;
		if (pUp && power < 100) {
			power += px;
		}
		if (pDown && power > 0) {
			power -= px;
		}
		float multiplier = power * .075f;
		if (fire && coolDown <= 0) {
			float xSpeed = (float) Math.cos(angle * Math.PI / 180) * multiplier;
			float ySpeed = (float) -Math.sin(angle * Math.PI / 180)
					* multiplier;
			float xPos = (float) (x + width / 2/*
												 * + Math.cos(angle Math.PI /
												 * 180) * 19
												 */);
			float yPos = (float) (y + height / 2 - 3/*
													 * - Math .sin(angle *
													 * Math.PI / 180) * 198
													 */);
			if (selectedBullet < 4)
				level.addBullet(new Bullet(xPos, yPos, xSpeed, ySpeed, selectedBullet));
			else {
				if (selectedBullet == 4) {
					level.addBullet(new SplitBullet(xPos, yPos, xSpeed, ySpeed, selectedBullet));
				}
			}
			switch(selectedBullet) {
			case 1: coolDownTime = 10; break;
			case 2: coolDownTime = 40; break;
			case 3: coolDownTime = 100; break;
			case 4: coolDownTime = 150; break;
			default: coolDownTime = 0;
			}
		}
		if (angle < 0)
			angle = 360 - (Math.abs(angle) % 360);
		if (angle > 360)
			angle = angle % 360;
		if (coolDownTime > 0) {
			coolDown++;
			if (coolDown > coolDownTime) {
				coolDownTime = 0;
				coolDown = 0;
			}
		}
	}

	private boolean onGround() {
		int maxY = (int) (y + height);
		for (int i = 0; i < width; i++) {
			if (maxY < MainGame.HEIGHT - 1) {
				int xx = (int) x + i;
				if (xx < 0 || xx > MainGame.WIDTH)
					return true;
				if (level.ground[xx][maxY + 1]) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	private int closestHighPoint() {
		return closestHighPoint((int) x, width);
	}

	private int closestHighPoint(int xStart, int width) {
		// TODO: Fix at some point in life
		int highestPoint = (int) y + height;
		for (int xx = xStart; xx < xStart + width; xx++) {
			for (int yy = (int) (y + height); yy >= 0; yy--) {
				if (withinBounds(xx, yy)) {
					if (!level.ground[xx][yy] && level.ground[xx][yy + 1]
							&& highestPoint >= y) {
						highestPoint = yy;
						break;
					} else if (!level.ground[xx][yy]
							&& !level.ground[xx][yy + 1]) {
						break;
					}
				}
			}
		}

		return highestPoint;
	}

	private int insideGround(int x0, int y0, int x1, int y1) {
		for (int yy = y0; yy < y1; yy++) {
			for (int xx = x0; xx < x1; xx++) {
				if (withinBounds(xx, yy)) {
					if (level.ground[xx][yy] && Art.tank.getColor(xx-x0, yy-y0).a > 0) {
						return xx;
					}
				}
			}
		}

		return -1;
	}

	public void move(int direction) {
		float newPoint = x + speed * direction;
		int value = insideGround((int) newPoint, (int) y, (int) newPoint
				+ width, (int) y + height);
		if (value > 0) {
			int point = closestHighPoint(value, 1);
			int heightClimbValue = 6;
			if ((y + height) - point <= heightClimbValue) {
				if (insideGround((int) newPoint, point - height, (int) newPoint
						+ width, point) < 0) {
					y = point - height;
					x = newPoint;
				}
			}
		} else {
			x = newPoint;
		}
	}

	public void setInfo(List<String> info) {
		kickTime = 0;
		if (Integer.parseInt(info.get(0)) == 1)
			left = true;
		else
			left = false;
		if (Integer.parseInt(info.get(1)) == 1)
			right = true;
		else
			right = false;
		if (Integer.parseInt(info.get(2)) == 1)
			aUp = true;
		else
			aUp = false;
		if (Integer.parseInt(info.get(3)) == 1)
			aDown = true;
		else
			aDown = false;
		if (Integer.parseInt(info.get(4)) == 1)
			pDown = true;
		else
			pDown = false;
		if (Integer.parseInt(info.get(5)) == 1)
			pUp = true;
		else
			pUp = false;
		if (Integer.parseInt(info.get(6)) == 1)
			fire = true;
		else
			fire = false;
		selectedBullet = Integer.parseInt(info.get(7));
	}

	public String getInfo() {
		String ret = name + " ";
		ret += col + " ";
		ret += (int) x + " ";
		ret += (int) y + " ";
		ret += (int) angle + " ";
		ret += power + " ";
		return ret;
	}

	public boolean withinBounds(int xx, int yy) {
		return xx >= 0 && yy >= 0 && xx < MainGame.WIDTH
				&& yy < MainGame.HEIGHT;
	}
}
