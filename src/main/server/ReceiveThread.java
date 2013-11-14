package main.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.newdawn.slick.SlickException;

import main.MainGame;
import main.bullets.Bullet;
import main.levels.ClientLevel;
import main.levels.Level;
import main.tanks.Tank;

public class ReceiveThread extends Thread {
	private ArrayList<Tank> tanks;
	private DatagramSocket socket;
	private Level level;
	private boolean running = true;
	long lastTime;
	long currentTime;
	int packetsSent;
	long timePassed = 0;
	boolean hasLevel = false;
	ArrayList<Bullet> myList = new ArrayList<Bullet>();

	public ReceiveThread(ArrayList<Tank> tanks, DatagramSocket s, Level level) {
		this.socket = s;
		this.tanks = tanks;
		this.level = level;
		currentTime = System.currentTimeMillis();
		lastTime = currentTime;
		if (level != null) 
			hasLevel = true;
	}

	public void run() {
		System.out.println("Start receiveing in client");
		while (running) {
			currentTime = System.currentTimeMillis();
			boolean received = receivePacket();
			if (received) {
				packetsSent++;
				timePassed += currentTime - lastTime;
				if (timePassed > 1000) {
					//System.out.println(packetsSent + " packets per second");
					packetsSent = 0;
					timePassed -= 1000;
				}
			}
			lastTime = currentTime;
		}
	}

	public synchronized boolean receivePacket() {
		try {
			// receive server packet
			byte[] buf = new byte[512];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			// translate packet
			String received = new String(packet.getData(), 0,
					packet.getLength());
			translateMessage(received);
			return true;
		} catch (IOException ex) {
			System.out.print("Problem recieving");
			ex.printStackTrace();
			running = false;
		}
		return false;
	}

	public void translateMessage(String message) {
		StringTokenizer part = new StringTokenizer(message);
		ArrayList<String> lines = new ArrayList<String>();
		while (part.hasMoreTokens()) {
			lines.add(part.nextToken());
		}
		int i = 0;
		String head;
		while (i <= lines.size() - 2) {
			head = lines.get(i++);
			if (head.equals("bullet")) {
				if (!hasLevel) return;
				int id = Integer.parseInt(lines.get(i++));
				int posX = (int) Float.parseFloat(lines.get(i++));
				int posY = (int) Float.parseFloat(lines.get(i++));
				int type = Integer.parseInt(lines.get(i++));
				int isDead = Integer.parseInt(lines.get(i++));
				if (isDead == 1) {
					((ClientLevel) level).toRemove.add(id);
					if ((posX < MainGame.WIDTH || posX > 0) && posY < MainGame.HEIGHT-100) {
						Level.destroyGround(posX, posY, Bullet.getDmgRadius(type)[0], Bullet.getDmgRadius(type)[1], level);
						((ClientLevel)level).isDirty = true;
						((ClientLevel) level).addParticles(posX, posY);
					}
				} else {
					boolean found = false;
					for (Bullet b : myList) {
						if (b.id == id) {
							found = true;
						}
					}
					for (Bullet b : ((ClientLevel) level).bullets) {
						if (b.id == id) {
							for (int j = 0; j < myList.size(); j++) {
								if (b.id == myList.get(j).id){
									myList.remove(j);
								}
							}
							b.setPos(posX, posY);
							if (MainGame.useLighting) {
								try {
									((ClientLevel)level).isDirty = true;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							found = true;
							break;
						}
					}
					if (!found) {
						Bullet b = new Bullet(posX, posY, type, id);
						((ClientLevel) level).toAdd.add(b);
						myList.add(b);
					}
				}
			} else if (head.equals("info")) {
				gettingInfo = true;
				String name = lines.get(i++);
				Tank t = getTank(name);
				
				if (t != null) {
					if (t.getX() < 0) {
						i += 5;
						continue;
					}
					i++;
					int x = Integer.parseInt(lines.get(i++));
					int y = Integer.parseInt(lines.get(i++)); 
					t.setPos(x, y);
					t.angle = (int) Float.parseFloat(lines.get(i++));
					t.power = Float.parseFloat(lines.get(i++));
				} else {
					String col = lines.get(i++);
					int x = Integer.parseInt(lines.get(i++));
					int y = Integer.parseInt(lines.get(i++));
					i += 2;
					Tank newTank = new Tank(col, name, x, y);
					((ClientLevel) level).toAddTank.add(newTank);
				}
			} else if (head.equals("level") && !hasLevel) {
				System.out.println("getting level");
				gettingInfo = false;
				int seed = Integer.parseInt(lines.get(i++));
				this.level = new ClientLevel(seed);
				hasLevel = true;
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean hasLevel()  {
		return hasLevel;
	}
	boolean gettingInfo = false;
	public boolean gettingInfo() {
		return gettingInfo;
	}
	public Level getLevel() {
		return level;
	}
	public void setTanks(ArrayList<Tank> list) {
		this.tanks = list;
	}
	public Tank getTank(String name) {
		if (tanks == null) return Tank.dead;
		for (Tank t : tanks) {
			if (t.name.equals(name)) {
				return t;
			}
		}
		return null;
	}
}
