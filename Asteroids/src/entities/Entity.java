package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Shape;

public abstract class Entity {
	public static List<Entity> entityList = new ArrayList<Entity>();
	
	public Entity() {
		entityList.add(this);
	}
	
	public void remove() {
		entityList.remove(this);
	}
	
	public abstract void update(int delta);
	
	public abstract Shape getDrawable();
	
	public abstract float getX();

	public abstract float getY();
	
	public abstract void goneOffScreen();
	
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
}
