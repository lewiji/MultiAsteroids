package entities;

import java.util.Random;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

import core.Constants;

public class Asteroid extends Entity {
	private float velocity = 0.01f;
	private float rotation = 0.0f;
	private Polygon shape;
	
	public Asteroid() {
		shape = new Polygon();
		Random rnd = new Random();
		
		shape.addPoint(0.0f, 0.0f);
		shape.addPoint(60.0f, 20.0f);
		shape.addPoint(80.0f, 40.0f);
		shape.addPoint(70.0f, 50.0f);
		shape.addPoint(40.0f, 38.0f);
		shape.addPoint(20.0f, 20.0f);
		shape.addPoint(0.0f, 15.0f);
		shape.setCenterX(rnd.nextInt(Constants.CONTAINER_WIDTH));
		shape.setCenterY(rnd.nextInt(Constants.CONTAINER_HEIGHT));
		
		rotation = (float) (rnd.nextFloat() * Math.PI);
	}

	@Override
	public void update(int delta) {
		shape.setCenterX((float) (shape.getCenterX() - Math.sin(rotation) * velocity * delta));
		shape.setCenterY((float) (shape.getCenterY() - -Math.cos(rotation) * velocity * delta));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCollision(Bullet bulletOther) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCollision(Ship shipOther) {
		System.out.println("bam");
		
	}

}
