package core;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity {
	public static List<Entity> entities = new ArrayList<Entity>();
	
	public abstract void update(int delta);
	
	public abstract Object getDrawable();
	
	public abstract float getX();

	public abstract float getY();
	
	public abstract void goneOffScreen();
	
	public Entity() {
		entities.add(this);
	}
}
