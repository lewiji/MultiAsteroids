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

import core.Constants;
import core.EntityCollisions;
import entities.Asteroid;
import entities.Bullet;
import entities.Ship;

public class AsteroidsServer extends BasicGame {
	
	HashMap<Integer, Asteroid> asteroids = new HashMap<Integer, Asteroid>();
	ArrayList<Ship> ships = new ArrayList<Ship>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();

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
		// TODO Auto-generated method stub
		
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
			         ships.add(ship);
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
		
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).update(delta);
		}
		
	}
}
