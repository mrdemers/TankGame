package main.bullets;

import main.levels.Level;

public class SplitBullet extends Bullet{
	int flightTime = 0;
	boolean added = false;
	public SplitBullet(float x, float y, float dX, float dY, int type) {
		super(x, y, dX, dY, type);
	}
	
	public void update(Level level, int delta) {
		super.update(level, delta);
		flightTime += delta;
		if (flightTime > 400 && this.dY >= 0 && !added) {
			level.addBullet(new Bullet(x, y, dX-1, dY, 2));
			level.addBullet(new Bullet(x, y, dX+1, dY, 2));
			added = true;
		}
	}
}
