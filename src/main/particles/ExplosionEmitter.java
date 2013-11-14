package main.particles;

import main.Art;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.*;
public class ExplosionEmitter implements ParticleEmitter{
	//X and Y coordinates
	public float x, y;
	
	public int timeAlive;
	public int lifeTime = 500;
	public float size = 10;
	public int numParticles = 40;
	
	private boolean enabled = true;
	
	public ExplosionEmitter(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public boolean completed() {
		return timeAlive > lifeTime;
	}

	public Image getImage() {
		return Art.explosion;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isOriented() {
		return false;
	}

	public void resetState() {
		timeAlive = 0;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void update(ParticleSystem ps, int delta) {
		if (timeAlive == 0) {
			for (int i = 0; i < numParticles; i++) {
				Particle p = ps.getNewParticle(this, lifeTime);
				p.setColor(1, 1, 1, 0.5f);
				p.setPosition(x, y);
				p.setSize(size);
				double randomDirection = Math.random() * -(Math.PI + Math.PI/16 + Math.PI/16) - Math.PI/16;
				float speed = 0.05f;
				float vx = (float)(Math.cos(randomDirection) * Math.random());
				float vy = (float)(Math.sin(randomDirection) * Math.random());
				p.setVelocity(vx,vy,speed);
			}
		}
		timeAlive += delta;
	}

	public void updateParticle(Particle particle, int delta) {
		particle.adjustSize(-.05f);
		particle.adjustColor(.01f, 0f, 0f, -.02f);
	}

	public boolean useAdditive() {
		return false;
	}

	public boolean usePoints(ParticleSystem ps) {
		return false;
	}

	public void wrapUp() {
	}

}
