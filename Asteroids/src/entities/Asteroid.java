package entities;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

import core.Constants;

public class Asteroid extends Entity {
	private float velocity = 0.01f;
	public float rotation = 0.0f;
	private float rotationVelocity = 0.01f;
	private Polygon shape;
	public float size;
	
	public Asteroid() {
		
	}
	
	public Asteroid(float aSize, float x, float y, float rot, float rotVelocity) {
		shape = new Polygon();
		size = aSize;
		position.x = x;
		position.y = y;
		
		shape.addPoint(0.0f * aSize, 0.0f  * aSize);
		shape.addPoint(60.0f * aSize, 20.0f * aSize);
		shape.addPoint(80.0f * aSize, 40.0f * aSize);
		shape.addPoint(70.0f * aSize, 50.0f * aSize);
		shape.addPoint(40.0f * aSize, 38.0f * aSize);
		shape.addPoint(20.0f * aSize, 20.0f * aSize);
		shape.addPoint(0.0f * aSize, 15.0f * aSize);
		shape.setCenterX(position.x);
		shape.setCenterY(position.y);
		
		radius = shape.getBoundingCircleRadius();
		
		if (aSize == Constants.ASTEROID_SIZE_SMALLER) {
			velocity *= 2;
		}
		
		if (aSize == Constants.ASTEROID_SIZE_SMALLEST) {
			velocity *= 4;
		}
		
		rotation = rot;
		rotationVelocity = rotVelocity;
		
		
		shape = (Polygon) shape.transform(Transform.createRotateTransform(rotation));
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
		
		shape.setCenterX(position.x);
		shape.setCenterY(position.y);
		shape = (Polygon) shape.transform(Transform.createRotateTransform(rotationVelocity, position.x, position.y));
	}

	@Override
	public Shape getDrawable() {
		return shape;
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
		g.draw(shape);
	}

}
