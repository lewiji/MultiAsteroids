package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import core.Constants;
import core.EntityCollisions;
import entities.Asteroid;
import entities.Bullet;
import entities.Entity;
import entities.Ship;

public class AsteroidsServer extends BasicGame {
	
	ConcurrentHashMap<Integer, Asteroid> asteroids = new ConcurrentHashMap<Integer, Asteroid>();
	ConcurrentHashMap<Integer, Ship> ships = new ConcurrentHashMap<Integer, Ship>();
	ConcurrentHashMap<Integer, Bullet> bullets = new ConcurrentHashMap<Integer, Bullet>();

	public AsteroidsServer() {
		super("AsteroidsServer");
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		try { 
		    AppGameContainer container = new AppGameContainer(new AsteroidsServer()); 
		    container.setDisplayMode(Constants.CONTAINER_WIDTH,Constants.CONTAINER_HEIGHT,false); 
		    container.setVSync(true);
		    container.start();
		    
		} catch (SlickException e) { 
		    e.printStackTrace(); 
		}

	}

	public Server server = new Server(32768, 4096);
	
	EntityCollisions collisions = new EntityCollisions(Constants.CONTAINER_WIDTH, 
			   Constants.CONTAINER_HEIGHT, 
			   Constants.COLLISION_CELL_SIZE);

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		Iterator<Asteroid> asterIter = asteroids.values().iterator();
		while (asterIter.hasNext()) {
			Asteroid roid = asterIter.next();
			roid.render(g);
		}
		
		Iterator<Ship> shipIter = ships.values().iterator();
		while (shipIter.hasNext()) {
			Ship ship = shipIter.next();
			ship.render(g);
			
		}
		
		Iterator<Bullet> bulletIter = bullets.values().iterator();
		while (bulletIter.hasNext()) {
			Bullet bullet = bulletIter.next();
			bullet.render(g);
		}
		

		
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		try {
			server.start();
			server.bind(2112, 2113);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Kryo kryo = server.getKryo();
		Log.set(Log.LEVEL_INFO);
		
		KryoRegistration.register(kryo);
		
		server.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof ConnectionRequest) {
			         ConnectionRequest request = (ConnectionRequest)object;
			         System.out.println("Connection request received");
			         
			         ConnectionResponse response = new ConnectionResponse();
			         response.setPlayerId(ships.size());
			         Ship ship = new Ship();
			         ship.playerId = response.getPlayerId();
			         ships.put(response.getPlayerId(), ship);
			         connection.sendTCP(response);
			      }
			      else if (object instanceof AsteroidRequest) {
			    	  AsteroidResponse response = new AsteroidResponse();
			    	  
			    	  Iterator<Asteroid> roidIter = asteroids.values().iterator();
			    	  while (roidIter.hasNext()) {
			    		  Asteroid roid = roidIter.next();
			    		  AsteroidPOJO pojo = new AsteroidPOJO();
			    		  
			    		  pojo.x = roid.getPosition().x;
			    		  pojo.y = roid.getPosition().y;
			    		  pojo.size = roid.size;
			    		  pojo.rot = roid.rotation;
			    		  pojo.id = roid.id;
			    		  
			    		 response.asteroids.add(pojo);
			    	  }

		    		  connection.sendTCP(response);
			      }
			      else if (object instanceof ShipRequest) {
			    	  ShipRequest request = (ShipRequest)object;
			    	  Ship ship = ships.get(request.playerId);
			    	  if (ship != null) {
			    		  if (ship.toBeDestroyed) {
			    			  ShipDestroyResponse shipDestroy = new ShipDestroyResponse();
			    			  connection.sendTCP(shipDestroy);
			    			  ship.toBeDestroyed = false;
			    		  }
			    	  }
			    	  Ship newShip = new Ship();
						newShip.position.x = request.x;
						newShip.position.y = request.y;
						newShip.rotation = request.rot;
						newShip.playerId = request.playerId;
						newShip.invulnerable = request.invulnerable;
						ships.put(request.playerId, newShip);
						
						Iterator<Ship> iter = ships.values().iterator();
						
						while (iter.hasNext()) {
							Ship aShip = iter.next();
							if (aShip.getPlayerId() != request.playerId) {
								ShipResponse response = new ShipResponse();
								response.playerId = aShip.playerId;
								response.rot = aShip.rotation;
								response.x = aShip.position.x;
								response.y = aShip.position.y;
								connection.sendTCP(response);
							}
						}
						
			      }
			      else if (object instanceof BulletUpdate) {
			    	  BulletUpdate request = (BulletUpdate)object;
			    	  Bullet bullet = new Bullet(request.angle, request.x, request.y, request.playerId);
			    	  bullet.id = bullets.size();
			    	  while (bullets.get(bullet.id) != null) {
			    		  bullet.id += 1;
			    	  }
			    	  bullets.put(bullet.id, bullet);
			      }
			      else if (object instanceof BulletRequest) {
			    	  Iterator<Bullet> iter = bullets.values().iterator();

		    		  BulletResponse response = new BulletResponse();
		    		  
			    	  while (iter.hasNext()) {
			    		  Bullet bullet = iter.next();
			    		  BulletPOJO pojo = new BulletPOJO();
			    		  
			    		  pojo.x = bullet.position.x;
			    		  pojo.y = bullet.position.y;
			    		  pojo.bulletId = bullet.id;
			    		  pojo.playerId = bullet.getPlayerId();
			    		  
			    		  response.bullets.add(pojo);
			    	  }

		    		  connection.sendTCP(response);
			      }
			   }
			}); 
		
		for (int i = 0; i<6; i++) {
			Asteroid roid = new Asteroid(i, Constants.ASTEROID_SIZE_BIGGEST);
			asteroids.put(i, roid);
		}
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Iterator<Asteroid> asterIter = asteroids.values().iterator();
		while (asterIter.hasNext()) {
			Asteroid roid = asterIter.next();
			roid.update(delta);
		}
		
		Iterator<Bullet> bulletIter = bullets.values().iterator();
		while (bulletIter.hasNext()) {
			Bullet bullet = bulletIter.next();
			bullet.update(delta);
			if (bullet.toBeDestroyed) {
				bulletIter.remove();
			}
		}
		
		Iterator<Ship> shipIter = ships.values().iterator();
		while (shipIter.hasNext()) {
			Ship ship = shipIter.next();
			ship.update(delta);
			
		}
		
		EntityCollisions collisions = new EntityCollisions(Constants.CONTAINER_WIDTH, 
														   Constants.CONTAINER_HEIGHT,
														   Constants.COLLISION_CELL_SIZE);
		ArrayList<Entity> entityList = new ArrayList<Entity>();
		entityList.addAll(asteroids.values());
		entityList.addAll(bullets.values());
		entityList.addAll(ships.values());
		collisions.detect(entityList);
		
		Iterator<Asteroid> roidIter = asteroids.values().iterator();
		while (roidIter.hasNext()) {
			Asteroid roid = roidIter.next();
			if (roid.toBeDestroyed) {
				
				// Explode asteroid into smaller pieces
				if (roid.size == Constants.ASTEROID_SIZE_BIGGEST) {
					for (int i = 0; i < 3; i++) {
						Asteroid newRoid = new Asteroid(asteroids.size(), Constants.ASTEROID_SIZE_SMALLER, roid.position.x + roid.radius/2, roid.position.y + roid.radius/2);
						while (asteroids.get(newRoid.id) != null) {
							newRoid.id += 1;
						}
						asteroids.put(newRoid.id, newRoid);
					}
				} else if (roid.size == Constants.ASTEROID_SIZE_SMALLER) {
					for (int i = 0; i < 6; i++) {
						Asteroid newRoid = new Asteroid(asteroids.size(), Constants.ASTEROID_SIZE_SMALLEST, roid.position.x + roid.radius/2, roid.position.y + roid.radius/2);
						while (asteroids.get(newRoid.id) != null) {
							newRoid.id += 1;
						}
						asteroids.put(newRoid.id, newRoid);
					}
				}
				// Destroy original asteroid
				roidIter.remove();
			}
		}		
	}
}
