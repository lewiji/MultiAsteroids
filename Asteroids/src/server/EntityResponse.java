package server;

import java.util.HashMap;
import java.util.List;

import entities.Entity;

public class EntityResponse extends RequestResponse {
	public static HashMap<Class<? extends Entity>, List<Entity>> entities = new HashMap<Class<? extends Entity>, List<Entity>>();
}
