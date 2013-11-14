package main.gui;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class RadioButton {
	private boolean selected;
	private String info;
	private String descriptionText;
	private Circle circle;
	private RadioButtonGroup radioButtonGroup;
	private Color color;
	private int size = 5;
	
	public RadioButton(RadioButtonGroup rbg, String info, int x, int y) {
		this(rbg, info, x, y, "");
	}
	
	public RadioButton(RadioButtonGroup rbg, String info, int x, int y, String text) {
		this(rbg, info, x, y, text, Color.white);
	}
	
	public RadioButton(RadioButtonGroup rbg, String info, int x, int y, String text, Color col) {
		this.radioButtonGroup = rbg;
		this.info = info;
		this.descriptionText = text;
		this.circle = new Circle(x, y, size);
		this.color = col;
		selected = false;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean value) {
		this.selected = value;
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) {
		Input input = gc.getInput();
		if (input.isMouseButtonDown(0) && circle.contains(input.getMouseX(), input.getMouseY())) {
			radioButtonGroup.setSelected(this);
		}
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) {
		g.setColor(color);
		g.fill(circle);
		g.setColor(Color.black);
		g.draw(circle);
		if (selected) {
			g.fillOval(circle.getX() + 2,circle.getY() + 2, size * 2 - 4, size * 2 - 4);
		}
		if (descriptionText.length() > 0) {
			g.drawString(descriptionText, circle.getMinX() + size * 2 + 1, circle.getMinY() - 4);
		}
	}
	
	public String getInfo() {
		return info;
	}
}
