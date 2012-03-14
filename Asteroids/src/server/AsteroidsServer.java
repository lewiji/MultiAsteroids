package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
import entities.Ship;

public class AsteroidsServer extends BasicGame {
	
	HashMap<Integer, Asteroid> asteroids = new HashMap<Integer, Asteroid>();
	HashMap<Integer, Ship> ships = new HashMap<Integer, Ship>();
	HashMap<Integer, Bullet> bullets = new HashMap<Integer, Bullet>();

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

	public Server server = new Server();
	
	EntityCollisions collisions = new EntityCollisions(Constants.CONTAINER_WIDTH, 
			   Constants.CONTAINER_HEIGHT, 
			   Constants.COLLISION_CELL_SIZE);

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		Iterator<Asteroid> asterIter = ((HashMap<Integer, Asteroid>) asteroids.clone()).values().iterator();
		while (asterIter.hasNext()) {
			Asteroid roid = asterIter.next();
			roid.render(g);
		}
		
		Iterator<Ship> shipIter = ((HashMap<Integer, Ship>) ships.clone()).values().iterator();
		while (shipIter.hasNext()) {
			Ship ship = shipIter.next();
			ship.render(g);
		}
		
		Iterator<Bullet> bulletIter = ((HashMap<Integer, Bullet>) bullets.clone()).values().iterator();
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
		Log.set(Log.LEVEL_DEBUG);
		
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
			    	  for (int i = 0; i < asteroids.size(); i++) {
			    		  AsteroidResponse response = new AsteroidResponse();
			    		  Asteroid roid = asteroids.get(i);
			    		  response.x = roid.getPosition().x;
			    		  response.y = roid.getPosition().y;
			    		  response.size = roid.size;
			    		  response.rot = roid.rotation;
			    		  response.id = i;
			    		  connection.sendTCP(response);
			    	  }
			      }
			      else if (object instanceof ShipRequest) {
			    	  ShipRequest request = (ShipRequest)object;
			    	  Ship ship = ships.get(request.playerId);
			    	  if (ship != null) {
			    		  ships.remove(request.playerId);
			    	  }
			    	  Ship newShip = new Ship();
						newShip.position.x = request.x;
						newShip.position.y = request.y;
						newShip.rotation = request.rot;
						newShip.playerId = request.playerId;
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
			    	  
			    	  while (iter.hasNext()) {
			    		  Bullet bullet = iter.next();
			    		  BulletResponse response = new BulletResponse();
			    		  response.x = bullet.position.x;
			    		  response.y = bullet.position.y;
			    		  response.bulletId = bullet.id;
			    		  response.playerId = bullet.getPlayerId();
			    		  connection.sendTCP(response);
			    	  }
			      }
			   }
			}); 
		
		for (int i = 0; i<6; i++) {
			Asteroid roid = new Asteroid(Constants.ASTEROID_SIZE_BIGGEST);
			asteroids.put(i, roid);
		}
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Iterator<Asteroid> asterIter = ((HashMap<Integer, Asteroid>) asteroids.clone()).values().iterator();
		while (asterIter.hasNext()) {
			Asteroid roid = asterIter.next();
			roid.update(delta);
		}
		
		Iterator<Bullet> bulletIter = ((HashMap<Integer, Bullet>) bullets.clone()).values().iterator();
		while (bulletIter.hasNext()) {
			Bullet bullet = bulletIter.next();
			bullet.update(delta);
		}
	}
}
