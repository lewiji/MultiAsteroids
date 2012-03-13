package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public abstract class Entity {
	protected Vector2f position = new Vector2f();
	protected float radius = 0;
	
	public static HashMap<Class<? extends Entity>, List<Entity>> entities = new HashMap<Class<? extends Entity>, List<Entity>>();
	
	public Entity() {
		if (entities.get(this.getClass()) == null) {
			ArrayList<Entity> someEntities = new ArrayList<Entity>();
			someEntities.add(this);
			entities.put(this.getClass(), someEntities);
		} else {
			entities.get(this.getClass()).add(this);
		}
	}
	
	public void remove() {
		List<Entity> someEntities = entities.get(this.getClass());
		someEntities.remove(this);
	}
	
	public static List<Entity> getEntitiesByClass(Class<? extends Entity> entityClass) {
		List<Entity> theEntities = entities.get(entityClass);
		if (theEntities == null) {
			theEntities = new ArrayList<Entity>();
		}
		return theEntities;
	}
	
	public static List<Entity> getAllEntities() {
		ArrayList<Entity> entitiesList = new ArrayList<Entity>();
		Iterator<Entry<Class<? extends Entity>, List<Entity>>> entityMapIterator = entities.entrySet().iterator();
		while (entityMapIterator.hasNext()) {
			entitiesList.addAll(entityMapIterator.next().getValue());
		}
		return entitiesList;
	}
	
	public abstract void update(int delta);
	
	public abstract Shape getDrawable();
	
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
}
