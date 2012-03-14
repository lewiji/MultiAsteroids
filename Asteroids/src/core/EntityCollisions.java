package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import entities.Entity;

public class EntityCollisions {
	// Implementation of spatial hashing
	int width;
	int height;
	int cellSize;
	int cols;
	int rows;
	HashMap<Integer, List<Entity>> buckets;
	
	public EntityCollisions(int aWidth, int aHeight, int aCellSize) {
		width = aWidth;
		height = aHeight;
		cellSize = aCellSize;
		
		cols = width / cellSize;
		rows = height / cellSize;
		buckets = new HashMap<Integer, List<Entity>>(cols * rows);
	}

	public void detect(ArrayList<Entity> entityList) {
		clearBuckets();
		
		for (int i = 0; i < entityList.size(); i++) {
			registerObject(entityList.get(i));
		}
		
		Iterator<Entity> entityIterator = entityList.iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();
			ArrayList<Entity> nearbyEntities = getNearbyEntities(entity);
			Iterator<Entity> nearbyEntitiesIterator = nearbyEntities.iterator();
			
			while (nearbyEntitiesIterator.hasNext()) {
				Entity otherEntity = nearbyEntitiesIterator.next();
				
				if (!otherEntity.equals(entity) &&						
						otherEntity.getDrawable().intersects(entity.getDrawable())) {
					// Deal with collisions (individual classes have collision logic)
					entity.handleCollision(otherEntity);
					otherEntity.handleCollision(entity);
					
					if (otherEntity.toBeDestroyed) {
						// remove other entity if it's marked as destroyed
						nearbyEntitiesIterator.remove();
						otherEntity = null;
					}
				}
				if (entity.toBeDestroyed) {
					// If original entity is to be destroyed, no need to detect further collisions
					break;
				}
			}
			if (entity.toBeDestroyed) {
				// Remove entity if destroyed, and break out of loop
				entityIterator.remove();
				entity = null;
				break;
			}
		}
	}
	
	private ArrayList<Entity> getNearbyEntities(Entity entity) {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		ArrayList<Integer> bucketIds = getIdForObj(entity);
		for (Integer id : bucketIds) {
			entities.addAll(buckets.get(id));
		}
		return entities;
	}

	private void registerObject(Entity entity) {
		ArrayList<Integer> cellIds = getIdForObj(entity);
		for (Integer id : cellIds) {
				buckets.get(id).add(entity);
			
		}
	}

	private ArrayList<Integer> getIdForObj(Entity entity) {
		ArrayList<Integer> bucketsObjIsIn = new ArrayList<Integer>();
		
		Vector2f min = new Vector2f(entity.getPosition().x - entity.getRadius(), 
								  	entity.getPosition().y - entity.getRadius());
		Vector2f max = new Vector2f(entity.getPosition().x + entity.getRadius(), 
				  					entity.getPosition().y + entity.getRadius());
		
		int topLeft = addBucket(min);
		if (topLeft > 0 && topLeft < buckets.size() - 1 && !bucketsObjIsIn.contains(topLeft)) {
			bucketsObjIsIn.add(topLeft);
		}
		
		int topRight = addBucket(new Vector2f(max.x, min.y));
		if (topRight > 0 && topRight < buckets.size() - 1 && !bucketsObjIsIn.contains(topRight)) {
			bucketsObjIsIn.add(topRight);
		}
		
		int bottomRight = addBucket(max);
		if (bottomRight > 0 && bottomRight < buckets.size() - 1 && !bucketsObjIsIn.contains(bottomRight)) {
			bucketsObjIsIn.add(bottomRight);
		}
		
		int bottomLeft = addBucket(new Vector2f(min.x, max.y));
		if (bottomLeft > 0 && bottomLeft < buckets.size() - 1 && !bucketsObjIsIn.contains(bottomLeft)) {
			bucketsObjIsIn.add(bottomLeft);
		}
		
		return bucketsObjIsIn;
	}
	
	private int addBucket(Vector2f vector) {
		int cellPosition = (int) ((Math.floor(vector.x / cellSize)) + 
								  (Math.floor(vector.y / cellSize)) * cols);
		return cellPosition;
	}

	private void clearBuckets() {
		buckets.clear();
		for (int i = 0; i < rows*cols; i++) {
			buckets.put(i, new ArrayList<Entity>());
		}
	}
}
