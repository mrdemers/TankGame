package main.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

public class TextField {
	private Rectangle rectangle;
	private String text;
	private int cursorPosition;
	private boolean focus;
	private String label = "";
	private int charsAllowed; // How many characters can the box show without
								// going outside
	private int textWidth = 9;
	private int backTime, moveLTime, moveRTime;

	public TextField(int x, int y, int width, int height) {
		this(x, y, width, height, "");
	}

	public TextField(int x, int y, int width, int height, String label) {
		rectangle = new Rectangle(x, y, width, height);
		focus = false;
		text = "";
		this.label = label;
		charsAllowed = width / textWidth - 1;
	}

	public boolean hasFocus() {
		return focus;
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		Input input = gc.getInput();
		if (!hasFocus()) {
			if (input.isMouseButtonDown(0)
					&& rectangle.contains(input.getMouseX(), input.getMouseY())) {
				focus = true;
			}
		} else {
			if (input.isMouseButtonDown(0)
					&& !rectangle
							.contains(input.getMouseX(), input.getMouseY())) {
				focus = false;
			}
			if (input.isKeyDown(Input.KEY_BACK)) {
				backTime++;
			} else {
				backTime = 0;
			}
			if (input.isKeyDown(Input.KEY_LEFT)) {
				moveLTime++;
			} else {
				moveLTime = 0;
			}
			if (input.isKeyDown(Input.KEY_RIGHT)) {
				moveRTime++;
			} else {
				moveRTime = 0;
			}
			if (input.isKeyPressed(Input.KEY_LEFT) || (moveLTime > 30 && moveLTime%3==0)) {
				if (cursorPosition > 0) {
					cursorPosition--;
				}
			} else if (input.isKeyPressed(Input.KEY_RIGHT) || (moveRTime > 30 && moveRTime%3==0)) {
				if (cursorPosition < text.length()) {
					cursorPosition++;
				}
			} else if (input.isKeyPressed(Input.KEY_BACK)
					|| (backTime > 30 && backTime % 3 == 0)) {
				if (cursorPosition == 0) {
				} else if (cursorPosition < text.length()) {
					text = text.substring(0, cursorPosition - 1)
							+ text.substring(cursorPosition);
					cursorPosition--;
				} else {
					text = text.substring(0, cursorPosition - 1);
					cursorPosition--;
				}
			} else if (input.isKeyPressed(Input.KEY_DELETE)) {
				if (cursorPosition == text.length()) {
				} else if (cursorPosition > 0) {
					text = text.substring(0, cursorPosition)
							+ text.substring(cursorPosition + 1);
				} else {
					text = text.substring(1);
				}
			} else if ((input.isKeyDown(Input.KEY_RCONTROL) || input.isKeyDown(Input.KEY_LCONTROL)) &&
					input.isKeyPressed(Input.KEY_V)) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Clipboard clipboard = toolkit.getSystemClipboard();
				String result = "";
				try { 
					result = (String) clipboard.getData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (cursorPosition == 0) {
					text = result + text;
					cursorPosition += result.length();
				} else if (cursorPosition != text.length()) {
					text = text.substring(0, cursorPosition) + result + text.substring(cursorPosition);
					cursorPosition += result.length();
				} else {
					text += result;
					cursorPosition += result.length();
				}
			} else {
				String temp = getKeyPresses(input);
				if (cursorPosition == text.length()) {
					text += temp;
					cursorPosition += temp.length();
				} else if (cursorPosition == 0) {
					text = temp + text;
				} else {
					text = text.substring(0, cursorPosition) + temp
							+ text.substring(cursorPosition);
					cursorPosition += temp.length();
				}
			}
		}
	}

	private int startDrawPosition;

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setColor(Color.white);
		g.fill(rectangle);
		g.setColor(Color.black);
		g.draw(rectangle);
		if (cursorPosition > startDrawPosition + charsAllowed)
			startDrawPosition++;
		else if (cursorPosition < startDrawPosition + 1) {
			startDrawPosition -= charsAllowed / 2;
			if (startDrawPosition < 0) {
				startDrawPosition = 0;
			}
		}
		String toDraw = text;
		if (toDraw.length() > charsAllowed) {
			toDraw = text.substring(startDrawPosition);
			if (toDraw.length() > charsAllowed) {
				toDraw = toDraw.substring(0, charsAllowed);
			}
		} else {
			startDrawPosition = 0;
		}
		g.drawString(toDraw, rectangle.getX() + 2, rectangle.getY());
		if (!label.equals("")) {
			g.drawString(label, rectangle.getX() - label.length() * textWidth,
					rectangle.getY());
		}
		if (focus) {
			int pos = (int) rectangle.getX() + 2
					+ (cursorPosition - startDrawPosition) * textWidth;
			double draw = Math.sin(System.currentTimeMillis()/150);
			if (draw > 0 || backTime > 30 || moveLTime > 30 || moveRTime > 30)
				g.drawLine(pos, rectangle.getY() + 2, pos, rectangle.getMaxY() - 2);
		}
	}

	public String getKeyPresses(Input input) {
		String returnMe = "";
		int temp = Input.KEY_1 - 1;
		boolean shift = input.isKeyDown(Input.KEY_RSHIFT)
				|| input.isKeyDown(Input.KEY_LSHIFT);
		while (temp++ < Input.KEY_SPACE) {
			if (temp == Input.KEY_BACK || temp == Input.KEY_DELETE
					|| temp == Input.KEY_RSHIFT || temp == Input.KEY_LSHIFT
					|| temp == Input.KEY_RETURN || temp == Input.KEY_TAB
					|| temp == Input.KEY_LCONTROL || temp == Input.KEY_RCONTROL)
				continue;
			if (input.isKeyPressed(temp)) {
				if (temp == Input.KEY_PERIOD)
					returnMe += shift ? ">" : ".";
				else if (temp == Input.KEY_COMMA)
					returnMe += shift ? "<" : ",";
				else if (temp == Input.KEY_MINUS)
					returnMe += shift ? "_" : "-";
				else if (temp == Input.KEY_EQUALS)
					returnMe += shift ? "+" : "=";
				else if (temp == Input.KEY_SEMICOLON)
					returnMe += shift ? ":" : ";";
				else if (temp == Input.KEY_APOSTROPHE)
					returnMe += shift ? "\"" : "'";
				else if (temp == Input.KEY_SPACE)
					returnMe += " ";
				else {
					String s = Input.getKeyName(temp);
					returnMe += shift ? s.toUpperCase() : s.toLowerCase();
				}
			}
		}
		return returnMe;
	}

	public int getX() {
		return (int) rectangle.getX();
	}

	public int getY() {
		return (int) rectangle.getY();
	}

	public int getWidth() {
		return (int) rectangle.getWidth();
	}

	public int getHeight() {
		return (int) rectangle.getHeight();
	}

	public String getText() {
		return text;
	}
}
