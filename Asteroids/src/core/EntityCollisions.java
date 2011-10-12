package core;

import entities.Entity;

public class EntityCollisions {

	public void detect() {
		for (Entity entity : Entity.entityList) {
			for (Entity entityOther : Entity.entityList) {
				if (!entity.equals(entityOther)) {
					if (entity.getDrawable().intersects(entityOther.getDrawable())) {
						entity.handleCollision(entityOther);
						entityOther.handleCollision(entity);
					}
				}
			}
		}
	}

}
