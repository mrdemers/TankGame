package main.gui;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.*;

public class Button {
	/* includes normal, roll-over, and clicked */
	public Image[] images = new Image[3];
	public Image currentImage;
	private boolean useImages;
	
	/* if the button is just a color with text */
	public Color[] colors = new Color[3];
	public Color currentColor;
	
	public Shape shape;
	public String text;
	
	public Button(Shape shape, Image[] images) {
		this.shape = shape;
		this.images = images;
		currentImage = images[0];
		useImages = true;
	}
	
	public Button(Shape shape, Image normal, Image rollover, Image pressed) {
		this.shape = shape;
		images[0] = normal;
		images[1] = rollover;
		images[2] = pressed;
		currentImage = images[0];
		useImages = true;
	}
	
	public Button(String text, Shape shape, Color normal, Color rollover, Color pressed) {
		this.text = text;
		this.shape = shape;
		colors[0] = normal;
		colors[1] = rollover;
		colors[2] = pressed;
		currentColor = colors[0];
		useImages = false;
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		
	}
	
	boolean outside = false;
	boolean pressed = false;
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		if (shape.contains(input.getMouseX(), input.getMouseY())) {
			outside = false;
			if (input.isMouseButtonDown(0)) {
				if (useImages)
					currentImage = images[2];
				else
					currentColor = colors[2];
				pressed = true;
			} else {
				if (useImages)
					currentImage = images[1];
				else
					currentColor = colors[1];
			}
		} else if (!outside) {
			pressed = false;
			if (useImages)
				currentImage = images[0];
			else
				currentColor = colors[0];
			outside = true;
		}
		
		if (pressed && !input.isMouseButtonDown(0)) {
			pressed = false;
			onClick();
		}
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		if (useImages) {
			g.drawImage(currentImage, (int)shape.getX(), (int)shape.getY());
		} else {
			g.setColor(currentColor);
			g.fill(shape);
			g.setColor(Color.black);
			g.setAntiAlias(true);
			g.draw(shape);
			g.setAntiAlias(false);
			int textWidth = 9;
			int textHeight = 15;
			int x = (int)shape.getCenterX() - (text.length() * textWidth)/2;
			int y = (int)shape.getCenterY() - textHeight/2;
			g.drawString(text, x, y);
		}
		
		g.drawString("" + gc.getInput().getMouseX(), 100, 10);
	}
	
	public void onClick() {
		
	}
}
