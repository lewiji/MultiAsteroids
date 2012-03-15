package entities;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import core.Constants;

public class Asteroid extends Entity {
	private float velocity = 0.01f;
	public float rotation = 0.0f;
	private float rotationVelocity = 0.01f;
	private Image sprite;
	public float size;
	public boolean exploded = false;
	
	public Asteroid() {
		
	}
	
	public Asteroid(float aSize, float x, float y, float rot, float rotVelocity) {
		
		size = aSize;
		position.x = x;
		position.y = y;
		
		
		if (aSize == Constants.ASTEROID_SIZE_SMALLER) {
			velocity *= 2;
		}
		
		if (aSize == Constants.ASTEROID_SIZE_SMALLEST) {
			velocity *= 4;
		}
		
		rotation = rot;
		rotationVelocity = rotVelocity;
	}
	
	public Asteroid (int id, float aSize, float x, float y) {
		this(aSize, x, y, 0, 0);
		this.id = id;
		Random rnd = new Random();
		rotation = (float) (rnd.nextFloat() * Math.PI);
		rotationVelocity = (float) (rnd.nextFloat() * Math.PI * rotationVelocity);
	}
	
	public Asteroid (int id, float aSize) {
		this(id, aSize, 0, 0);
		Random rnd = new Random();
		position.x = (float) (rnd.nextFloat() * (Constants.CONTAINER_WIDTH - radius));
		position.y = (float) (rnd.nextFloat() * (Constants.CONTAINER_HEIGHT - radius));
	}

	@Override
	public void update(int delta) {
		position.x = (float) (position.x - Math.sin(rotation) * velocity * delta);
		position.y = (float) (position.y - -Math.cos(rotation) * velocity * delta);
		
		if (position.x > Constants.CONTAINER_WIDTH + radius) {
			position.x = 0 - radius;
		}
		
		if (position.x < 0 - radius) {
			position.x = Constants.CONTAINER_WIDTH + radius;
		}
		
		if (position.y > Constants.CONTAINER_HEIGHT + radius) {
			position.y = 0 - radius;
		}
		
		if (position.y < 0 - radius) {
			position.y = Constants.CONTAINER_HEIGHT + radius;
		}
		
	}

	@Override
	public Image getImage() {
		if (sprite == null) {
			loadImage();
		}
		return sprite;
	}

	@Override
	public void goneOffScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCollision(Asteroid asteroidOther) {
		
	}

	private void explode() {
		
		toBeDestroyed = true;
	}

	@Override
	public void handleCollision(Bullet bulletOther) {
		this.explode();
	}

	@Override
	public void handleCollision(Ship shipOther) {
		if (!shipOther.invulnerable)
			this.explode();
	}

	@Override
	public void render(Graphics g) {
		getImage().draw(position.x, position.y);
	}

	@Override
	public void loadImage() {
		try {
			if (size == Constants.ASTEROID_SIZE_BIGGEST) {
				sprite = new Image("resources/img/asteroid.png");
			} else if (size == Constants.ASTEROID_SIZE_SMALLER) {
				sprite = new Image("resources/img/asteroid_smaller.png");
			} else {
				sprite = new Image("resources/img/asteroid_smallest.png");
			}
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			sprite.setFilter(Image.FILTER_NEAREST);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		radius = sprite.getWidth();
		sprite.setRotation(rotation);
	}

}
