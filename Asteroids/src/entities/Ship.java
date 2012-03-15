package entities;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import core.Constants;

public class Ship extends Entity {
	private Image ship;
	private Vector2f thrust = new Vector2f();
	public float rotation = 0.0f;
	
	private float maxThrust = 0.4f;
	private float acceleration = 0.015f;
	
	public int playerId;
	public int playerScore = 0;
	
	private boolean dead = false;
	private int deadTime = Constants.DEAD_TIME;
	public boolean invulnerable = false;
	private int invulnerableTime = Constants.INVULNERABILITY_TIME;
	
	public Ship() {
		position.x = 0;
		position.y = 0;
		
		resetShip();		
	}
	
	private void pointAtMouse(float mouseX, float mouseY) {
		
		float newShipAngle = (float) (Math.atan2( mouseY - position.y, mouseX - position.x));
		newShipAngle += Math.toRadians(90);
		rotation = (float) newShipAngle;
	}
	
	public void update(float mouseX, float mouseY, int delta) {
		pointAtMouse(mouseX, mouseY);
		update(delta);
	}
	
	public void thrust() {
		thrust.x -= Math.sin(rotation) * acceleration;
		thrust.y -= -Math.cos(rotation) * acceleration;
		
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
		if (ship == null) {
			loadImage();
		}
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
	public Image getImage() {
		return ship;
	}

	@Override
	public void goneOffScreen() {
		
	}

	public int getPlayerId() {
		return playerId;
	}
	
	public int getPlayerScore(){
		return playerScore;
	}
	
	public void addPlayerScore(int i){
		this.playerScore += i;
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
		toBeDestroyed = true;
	}
	
	@Override
	public void render(Graphics g) {
		if (ship == null) {
			loadImage();
		}
		ship.setRotation((float) Math.toDegrees(rotation));
		if (invulnerable) {
			ship.draw(position.x, position.y);
		} else {
			ship.draw(position.x, position.y);
		}
	}

	@Override
	public void loadImage() {
		try {
			ship = new Image("resources/img/ship.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		try {
			ship.setFilter(Image.FILTER_NEAREST);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		radius = ship.getWidth();
	}
}
