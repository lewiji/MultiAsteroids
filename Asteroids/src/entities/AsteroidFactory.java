package entities;

import java.util.ArrayList;
import java.util.List;

public class AsteroidFactory {
	public static List<Asteroid> asteroids = new ArrayList<Asteroid>();
	
	public static void addAsteroid(Asteroid anAsteroid) {
		asteroids.add(anAsteroid);
	}
	
	public static void removeAsteroid(Asteroid anAsteroid) {
		asteroids.remove(anAsteroid);
	}
}
