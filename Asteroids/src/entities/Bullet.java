package entities;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

public class Bullet extends Entity {
	private float velocity = 0.5f;
	private float rotation = 0.0f;
	private Shape shape;
	private int playerId;
	
	public Bullet(float rot, float x, float y, int aPlayerId) {
		rotation = rot;
		shape = new Circle(x, y, 3.0f);
		playerId = aPlayerId;
	}
	
	public void update(int delta) {
		shape.setCenterX((float) (shape.getCenterX() - Math.sin(rotation) * velocity * delta));
		shape.setCenterY((float) (shape.getCenterY() - -Math.cos(rotation) * velocity * delta));
	}

	public float getX() {
		return shape.getCenterX();
	}

	public float getY() {
		return shape.getCenterY();
	}

	@Override
	public Shape getDrawable() {
		return shape;
	}

	@Override
	public void goneOffScreen() {
		// TODO Auto-generated method stub
		
	}
	
	public int getPlayerId() {
		return playerId;
	}

	@Override
	public void handleCollision(Asteroid asteroidOther) {
		BulletFactory.removeBullet(this);
		
	}

	@Override
	public void handleCollision(Bullet bulletOther) {
		
		
	}

	@Override
	public void handleCollision(Ship shipOther) {
		//BulletFactory.removeBullet(this);
		
	}
}
