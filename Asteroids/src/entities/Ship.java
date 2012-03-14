package entities;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;

import core.Constants;

public class Ship extends Entity {
	private Polygon ship = null;
	private Vector2f thrust = new Vector2f();
	public float rotation = 0.0f;
	
	private float maxThrust = 0.4f;
	private float acceleration = 0.015f;
	
	public int playerId;
	
	
	private boolean dead = false;
	private int deadTime = Constants.DEAD_TIME;
	public boolean invulnerable = false;
	private int invulnerableTime = Constants.INVULNERABILITY_TIME;
	
	public Ship() {
		position.x = 0;
		position.y = 0;
		
		ship = new Polygon();
		ship.addPoint(0.0f, 0.0f);
		ship.addPoint(20.0f, 0.0f);
		ship.addPoint(10.0f, 20.0f);
		ship.setCenterX(position.x);
		ship.setCenterY(position.y);
		
		resetShip();
		
		radius = ship.getBoundingCircleRadius();
		
        
		
	}
	
	private void pointAtMouse(float mouseX, float mouseY) {
		
		float newShipAngle = (float) (Math.atan2( mouseY - position.y, mouseX - position.x));
		newShipAngle += Math.toRadians(-90);
		ship = (Polygon) ship.transform(Transform.createRotateTransform(newShipAngle - rotation, 
																		position.x, position.y));
		rotation = newShipAngle;
	}
	
	public void update(float mouseX, float mouseY, int delta) {
		pointAtMouse(mouseX, mouseY);
		update(delta);
	}
	
	public void thrust() {
		thrust.x += Math.sin(rotation) * acceleration;
		thrust.y += -Math.cos(rotation) * acceleration;
		
		if (thrust.x > maxThrust) {
			thrust.x = maxThrust;
		} else if (thrust.x < -maxThrust) {
			thrust.x = -maxThrust;
		}
		
		if (thrust.y > maxThrust) {
			thrust.y = maxThrust;
		} else if (thrust.y < -maxThrust) {
			thrust.y = -maxThrust;
		}
	}
	
	public void setX(float x) {
		position.x = x;
	}
	
	public void setY(float y) {
		position.y = y;
	}

	public float getAngle() {
		return rotation;
	}

	@Override
	public void update(int delta) {
		position.x = position.x - (thrust.x * delta);
		position.y = position.y - (thrust.y * delta);
		

		
		if (dead && deadTime > 0) {
			position.x = -100;
			position.y = -100;
			deadTime -= delta;
		} else if (dead && deadTime <= 0) {
			resetShip();
		}
		
		if (invulnerable && invulnerableTime > 0) {
			invulnerableTime -= delta;
		} else if (invulnerable && invulnerableTime <= 0) {
			invulnerableTime = Constants.INVULNERABILITY_TIME;
			invulnerable = false;
		}
	}
	
	private void resetShip() {
		Random random = new Random();
		position.x = random.nextFloat() * Constants.CONTAINER_WIDTH;
		position.y = random.nextFloat() * Constants.CONTAINER_HEIGHT;
		dead = false;
		thrust.x = 0;
		thrust.y = 0;
		deadTime = Constants.DEAD_TIME;
		invulnerableTime = Constants.INVULNERABILITY_TIME;
		invulnerable = true;
	}

	@Override
	public Shape getDrawable() {
		return ship;
	}

	@Override
	public void goneOffScreen() {
		
	}

	public int getPlayerId() {
		return playerId;
	}

	@Override
	public void handleCollision(Asteroid asteroidOther) {
		if (!invulnerable) {
			killShip();
		}
	}

	@Override
	public void handleCollision(Bullet bulletOther) {
		if (!invulnerable && bulletOther.getPlayerId() != playerId) {
			killShip();
		}
		
	}

	@Override
	public void handleCollision(Ship shipOther) {
		if (!invulnerable) {
			killShip();
		}
		
	}
	
	public void killShip() {
		dead = true;
	}
	
	@Override
	public void render(Graphics g) {
		ship.setCenterX(position.x);
		ship.setCenterY(position.y);
		if (invulnerable) {
			g.setColor(Color.green);
			g.draw(ship);
			g.setColor(Color.white);
		} else {
			g.draw(ship);
		}
	}
}
