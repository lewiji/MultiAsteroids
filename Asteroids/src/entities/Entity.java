package entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Entity {
	public int id;
	public Vector2f position = new Vector2f();
	public float radius = 0;
	
	public abstract void update(int delta);
	
	public abstract Image getImage();
	
	public abstract void loadImage();
	
	public Vector2f getPosition() {
		return position;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public abstract void goneOffScreen();
	
	public abstract void render(Graphics g);
	
	public boolean toBeDestroyed = false;
	
	public void handleCollision(Entity entity) {
		if (entity.getClass() == Asteroid.class) {
			handleCollision((Asteroid) entity);
		} 
		if (entity.getClass() == Bullet.class) {
			handleCollision((Bullet) entity);
		}
		if (entity.getClass() == Ship.class) {
			handleCollision((Ship) entity);
		}
	}
	
	public abstract void handleCollision(Asteroid asteroidOther);
	
	public abstract void handleCollision(Bullet bulletOther);
	
	public abstract void handleCollision(Ship shipOther);

	public boolean collidesWith(Entity entity) {
		if (entity.getImage() == null) {
			entity.loadImage();
		}
		if (this.getImage() == null) {
			this.loadImage();
		}
		return (((entity.position.x + entity.getImage().getWidth()) >= this.position.x) &&
			 (entity.position.x <= (this.position.x + this.getImage().getWidth())) && 
			 ((entity.position.y + entity.getImage().getHeight()) > this.position.y) &&
			 (entity.position.y < (this.position.y + this.getImage().getHeight())));
	}
}
