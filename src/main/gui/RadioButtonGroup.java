package main.gui;

import java.util.ArrayList;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class RadioButtonGroup {
	private ArrayList<RadioButton> buttons = new ArrayList<RadioButton>();
	private int x, y;
	public RadioButtonGroup() {
		
	}
	
	public RadioButtonGroup(RadioButton ... buttons) {
		add(buttons);
	}
	
	public RadioButtonGroup(String preset, int x, int y) {
		this.x = x;
		this.y = y;
		if (preset.equals("colors")) {
			RadioButton red = new RadioButton(this, "red", x, y, "Red", Color.red);
			RadioButton blue = new RadioButton(this, "blue", x, y + 20, "Blue", Color.blue);
			RadioButton black = new RadioButton(this, "black", x, y + 40, "Black", Color.gray);
			RadioButton green = new RadioButton(this, "green", x, y + 60, "Green", Color.green);
			RadioButton yellow = new RadioButton(this, "yellow", x, y + 80, "Yellow", Color.yellow);
			add(red, blue, black, green, yellow);
			setSelected(red);
		}
	}
	
	public void add(RadioButton button) {
		buttons.add(button);
	}
	
	private void add(RadioButton ... buttons) {
		for (int i = 0; i < buttons.length; i++) {
			this.buttons.add(buttons[i]);
		}
	}
	
	public RadioButton getSelected() {
		for (RadioButton b : buttons) {
			if (b.isSelected()) {
				return b;
			}
		}
		return null;
	}
	
	public void setSelected(RadioButton rad) {
		for (RadioButton b : buttons) {
			if (b == rad) b.setSelected(true);
			else b.setSelected(false);
		}
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) {
		for (RadioButton b : buttons) {
			b.update(gc, sbg, delta);
		}
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) {
		for (RadioButton b : buttons) {
			b.render(gc, sbg, g);
		}
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
}
