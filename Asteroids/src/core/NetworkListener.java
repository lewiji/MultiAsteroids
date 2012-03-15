package core;

import java.util.Iterator;

import server.AsteroidDestroyResponse;
import server.AsteroidPOJO;
import server.AsteroidResponse;
import server.BulletPOJO;
import server.BulletResponse;
import server.ConnectionResponse;
import server.ShipDestroyResponse;
import server.ShipResponse;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import entities.Asteroid;
import entities.Bullet;
import entities.Ship;

public class NetworkListener extends Listener {
	public void received (Connection connection, Object object) {

		if (object instanceof ConnectionResponse) {
	         ConnectionResponse response = (ConnectionResponse)object;
	         System.out.println(response.getPlayerId());
	         Game.playerShip.playerId = response.getPlayerId();
		}
		else if (object instanceof AsteroidResponse) {
			AsteroidResponse response = (AsteroidResponse)object;
			Iterator<AsteroidPOJO> iter = response.asteroids.iterator();
			while (iter.hasNext()) {
				AsteroidPOJO pojo = iter.next();
				Asteroid asteroid = Game.asteroids.get(pojo.id);
				if (asteroid != null) {
					Game.asteroids.remove(asteroid);
				}
				Game.asteroids.put(pojo.id, new Asteroid(pojo.id, pojo.size, pojo.x, pojo.y));
			}
		}
		else if (object instanceof AsteroidDestroyResponse) {
			AsteroidDestroyResponse response = (AsteroidDestroyResponse)object;
			Game.asteroids.remove(response.id);
			Game.asteroidExplosionFx.play();
		}
		else if (object instanceof ShipResponse) {
			ShipResponse response = (ShipResponse)object;
			Ship ship = Game.ships.get(response.playerId);
			if (ship != null) {
				Game.ships.remove(ship);
			}
			Ship newShip = new Ship();
			newShip.position.x = response.x;
			newShip.position.y = response.y;
			newShip.rotation = response.rot;
			Game.ships.put(response.playerId, newShip);
		}
		else if (object instanceof ShipDestroyResponse) {
			Game.playerShip.killShip();
			Game.explosionFx.play();
		}
		else if (object instanceof BulletResponse) {
			BulletResponse response = (BulletResponse)object;
			Game.bullets.clear();
			
			Iterator<BulletPOJO> iter = response.bullets.iterator();
			while (iter.hasNext()) {
				BulletPOJO next = iter.next();
				Bullet bullet = new Bullet(0, next.x, next.y, next.playerId);
				Game.bullets.put(next.bulletId, bullet);
			}
		}
   }
}
