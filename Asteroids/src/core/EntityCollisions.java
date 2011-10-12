package core;

import java.util.ArrayList;

import entities.Entity;

public class EntityCollisions {

	public void detect() {
		ArrayList<Entity> entityList = (ArrayList<Entity>) Entity.entityList;
		
		for (int i = 0; i < entityList.size(); i++) {
			Entity entity = entityList.get(i);
			
			for (int j = 0; j < entityList.size(); j++) {
				Entity entityOther = entityList.get(j);
				
				if (!entity.equals(entityOther)) {
					if (entity.getDrawable().intersects(entityOther.getDrawable())) {
						entity.handleCollision(entityOther);
						entityOther.handleCollision(entity);
						break;
					}
				}
			}
		}
	}
}
