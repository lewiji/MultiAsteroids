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
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.ClassSerializer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import core.Constants;
import core.EntityCollisions;
import entities.Asteroid;
import entities.Entity;

public class AsteroidsServer extends BasicGame {

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
		kryo.register(ConnectionRequest.class);
		kryo.register(ConnectionResponse.class);
		kryo.register(EntityRequest.class);
		kryo.register(EntityResponse.class);
		kryo.register(Asteroid.class);
		kryo.register(Vector2f.class);
		kryo.register(Polygon.class);
		kryo.register(float[].class);
		kryo.register(HashMap.class);
		kryo.register(Entity.class);
		kryo.register(Class.class, new ClassSerializer(kryo));
		kryo.register(ArrayList.class);
		
		server.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof ConnectionRequest) {
			         ConnectionRequest request = (ConnectionRequest)object;
			         System.out.println("Connection request received from " + request.getPlayerId());
			         
			         ConnectionResponse response = new ConnectionResponse();
			         response.setPlayerId(request.getPlayerId());
			         connection.sendTCP(response);
			      }
			      else if (object instanceof EntityRequest) {
			    	  EntityRequest request = (EntityRequest)object;
			    	  System.out.println("Entity request recieved from " + request.getPlayerId());
			    	  
			    	  EntityResponse response = new EntityResponse();
			    	  response.entities = Entity.entities;
			    	  connection.sendTCP(response);
			      }
			   }
			}); 
		
		
		new Asteroid(Constants.ASTEROID_SIZE_BIGGEST);
		new Asteroid(Constants.ASTEROID_SIZE_BIGGEST);
		new Asteroid(Constants.ASTEROID_SIZE_BIGGEST);
		new Asteroid(Constants.ASTEROID_SIZE_BIGGEST);
		new Asteroid(Constants.ASTEROID_SIZE_BIGGEST);
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
		ArrayList<Entity> asteroidsList = (ArrayList<Entity>) Entity.getEntitiesByClass(Asteroid.class);
		Iterator<Entity> asteroidIterator = asteroidsList.iterator();
		while (asteroidIterator.hasNext()) {
			Asteroid asteroid = (Asteroid) asteroidIterator.next();
			asteroid.update(delta);
		}
		
	}
}
