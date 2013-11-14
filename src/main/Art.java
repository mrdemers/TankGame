package main;

import org.newdawn.slick.*;

public class Art {
	public static Image tank = load("tank.png");
	public static Image tankBarrel = load("tankBarrel.png");
	public static Image background = load("background.png");
	public static Image sky = load("sky.png");
	public static Image cloud1 = load("cloud1.png");
	public static Image cloud2 = load("cloud2.png");
	public static Image cloud3 = load("cloud3.png");
	public static Image explosion = load("explosion.png");
	public static Image[][] arrows = loadSheet("arrows.png", 60, 80);
	public static Image[][] numbers = loadSheet("numbers.png", 40, 60);
	public static Image numberOfPlayersInfo = load("numberofplayers.png");
	public static Image menuBackground = load("menuBackground.png");
	public static Image powerBar = load("powerbar.png");
	public static Image powerMeter = load("powermeter.png");
	public static Image smallBullet = load("smallBullet.png");
	public static Image mediumBullet = load("mediumBullet.png");
	public static Image largeBullet = load("largeBullet.png");
	public static Image splitBullet = load("splitBullet.png");
	
	public static Image load(String fileName) {
		try {
			return new Image("res/" + fileName);
		} catch (SlickException e) {
			System.out.println("Problem reading: " + fileName);
			e.printStackTrace();
		}
		return null;
	}
	
	public static Image[][] loadSheet(String fileName, int xCut, int yCut) {
		Image[][] images = new Image[0][0];
		Image image;
		try {
			image = load(fileName);
			int xc = image.getWidth()/xCut;
			int yc = image.getHeight()/yCut;
			images = new Image[yc][xc];
			for (int i = 0; i < xc; i++) {
				for (int j = 0; j < yc; j++) {
					images[j][i] = image.getSubImage(i * xCut, j * yCut, xCut, yCut);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return images;
	}
	
	public static Color getColor(String col) {
		if (col.equals("red")) {
			return Color.red;
		}
		if (col.equals("blue")) {
			return Color.blue;
		}
		if (col.equals("black")) {
			return Color.gray;
		}
		if (col.equals("green")) {
			return Color.green;
		}
		if (col.equals("yellow")) {
			return Color.yellow;
		}
		return Color.white;
	}
	
	public static String getColor(Color col) {
		if (col.equals(Color.red)) {
			return "red";
		}
		if (col.equals(Color.blue)) {
			return "blue";
		}
		if (col.equals(Color.black)) {
			return "black";
		}
		if (col.equals(Color.green)) {
			return "green";
		}
		if (col.equals(Color.yellow)) {
			return "yellow";
		}
		return "white";
	}
}
