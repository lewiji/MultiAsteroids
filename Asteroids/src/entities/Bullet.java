package entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

import core.Constants;

public class Bullet extends Entity {
	private float velocity = 0.5f;
	private float rotation = 0.0f;
	private Shape shape;
	private int playerId;
	
	public Bullet(float rot, float x, float y, int aPlayerId) {
		rotation = rot;
		position.x = x;
		position.y = y;
		shape = new Circle(position.x, position.y, 3.0f);
		radius = shape.getBoundingCircleRadius();
		playerId = aPlayerId;
	}
	
	public void update(int delta) {
		position.x = (float) (position.x - Math.sin(rotation) * velocity * delta);
		position.y = (float) (position.y - -Math.cos(rotation) * velocity * delta);
		shape.setCenterX(position.x);
		shape.setCenterY(position.y);
		
		if (position.x > Constants.CONTAINER_WIDTH ||
			position.x < 0 ||
			position.y > Constants.CONTAINER_HEIGHT ||
			position.y < 0) {
			this.toBeDestroyed = true;
		}
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
		this.toBeDestroyed = true;
		
	}

	@Override
	public void handleCollision(Bullet bulletOther) {
		
		
	}

	@Override
	public void handleCollision(Ship shipOther) {
		//BulletFactory.removeBullet(this);
		
	}
	
	@Override
	public void render(Graphics g) {
		g.draw(shape);
		
	}
}
