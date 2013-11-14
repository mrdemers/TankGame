package main.levels;

import static main.levels.ClientLevel.lightMap;
import main.MainGame;

public class Light {
	public float x, y;
	public float radius;
	public float falloff;
	public float power; // 0 - 1
	public int color;
	
	public Light(float x, float y, float radius) {
		this(x, y, radius, 1.0f, 0);
	}
	
	public Light(float x, float y, float radius, float power, int color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
		this.power = power;
		for (int xx = (int)(x-radius); xx < x + radius; xx++) {
			if (xx < 0 || xx >= MainGame.WIDTH) continue;
			for (int yy = (int)(y-radius); yy < y + radius; yy++) {
				if (yy < 0 || yy >= MainGame.HEIGHT-100) continue;
				double distance = Math.sqrt((xx - x) * (xx - x) + (yy - y) * (yy - y));
				int val = (int)((255 - (distance/radius) * 255.0) * power);
				if (val < 0) val = 0;
				if (val > 255) val = 255;
				if (val > (lightMap[xx + yy * MainGame.WIDTH] & 0xff)) {
					lightMap[xx + yy * MainGame.WIDTH] = (color << 8) | val;
				}
			}
		}
	}
	
	public void move(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void updateMap() {
		for (int xx = (int)(x-radius); xx < x + radius; xx++) {
			if (xx < 0 || xx >= MainGame.WIDTH) continue;
			for (int yy = (int)(y-radius); yy < y + radius; yy++) {
				if (yy < 0 || yy >= MainGame.HEIGHT-100) continue;
				double distance = Math.sqrt((xx - x) * (xx - x) + (yy - y) * (yy - y));
				if (distance > radius) continue;
				int val = (int)((255 - (distance/radius) * 255.0) * power);
				if (val < 0) val = 0;
				if (val > 255) val = 255;
				int num = (lightMap[xx + yy * MainGame.WIDTH] & 0xff);
				num += val;
				if (num > 255) num = 255;
				lightMap[xx + yy * MainGame.WIDTH] = (color << 8) |num;  
			}
		}
	}
}
