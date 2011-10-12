package entities;

import java.util.Random;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

import core.Constants;

public class Asteroid extends Entity {
	private float velocity = 0.01f;
	private float rotation = 0.0f;
	private float rotationVelocity = 0.01f;
	private Polygon shape;
	private float size;
	
	public Asteroid(float aSize) {
		shape = new Polygon();
		Random rnd = new Random();
		size = aSize;
		
		shape.addPoint(0.0f * aSize, 0.0f  * aSize);
		shape.addPoint(60.0f * aSize, 20.0f * aSize);
		shape.addPoint(80.0f * aSize, 40.0f * aSize);
		shape.addPoint(70.0f * aSize, 50.0f * aSize);
		shape.addPoint(40.0f * aSize, 38.0f * aSize);
		shape.addPoint(20.0f * aSize, 20.0f * aSize);
		shape.addPoint(0.0f * aSize, 15.0f * aSize);
		shape.setCenterX(rnd.nextInt(Constants.CONTAINER_WIDTH));
		shape.setCenterY(rnd.nextInt(Constants.CONTAINER_HEIGHT));
		
		if (aSize == Constants.ASTEROID_SIZE_SMALLER) {
			velocity *= 2;
		}
		
		if (aSize == Constants.ASTEROID_SIZE_SMALLEST) {
			velocity *= 4;
		}
		
		rotation = (float) (rnd.nextFloat() * Math.PI);
		rotationVelocity = (float) (rnd.nextFloat() * rotationVelocity);
		
		shape = (Polygon) shape.transform(Transform.createRotateTransform(rotation));
	}
	
	public Asteroid(float aSize, float f, float g) {
		this(aSize);
		shape.setCenterX(f);
		shape.setCenterY(g);
	}

	@Override
	public void update(int delta) {
		shape.setCenterX((float) (shape.getCenterX() - Math.sin(rotation) * velocity * delta));
		shape.setCenterY((float) (shape.getCenterY() - -Math.cos(rotation) * velocity * delta));
		shape = (Polygon) shape.transform(Transform.createRotateTransform(rotationVelocity, shape.getCenterX(), shape.getCenterY()));
	}

	@Override
	public Shape getDrawable() {
		return shape;
	}

	@Override
	public float getX() {
		return shape.getCenterX();
	}

	@Override
	public float getY() {
		return shape.getCenterY();
	}

	@Override
	public void goneOffScreen() {
		// TODO Auto-generated method stub

	}

	public void setY(int y) {
		shape.setCenterY(y);
	}
	
	public void setX(int x) {
		shape.setCenterX(x);
	}

	@Override
	public void handleCollision(Asteroid asteroidOther) {
		this.explode();
	}

	private void explode() {
		if (size == Constants.ASTEROID_SIZE_BIGGEST) {
			AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_SMALLER, this.getX() + 40, this.getY()));
			AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_SMALLER, this.getX() - 40, this.getY()));
			AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_SMALLER, this.getX(), this.getY() + 40));
			AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_SMALLER, this.getX(), this.getY() - 40));
		} else if (size == Constants.ASTEROID_SIZE_SMALLER) {
			AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_SMALLEST, this.getX() + 20, this.getY()));
			AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_SMALLEST, this.getX() - 20, this.getY()));
			AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_SMALLEST, this.getX(), this.getY() + 20));
		}
		AsteroidFactory.removeAsteroid(this);
		this.remove();
	}

	@Override
	public void handleCollision(Bullet bulletOther) {
		this.explode();
	}

	@Override
	public void handleCollision(Ship shipOther) {
		this.explode();
	}

}
