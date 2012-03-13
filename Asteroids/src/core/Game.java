/**
 * 
 */
package core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

import server.EntityRequest;
import server.ConnectionRequest;
import server.ConnectionResponse;
import server.EntityResponse;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import entities.Asteroid;
import entities.Bullet;
import entities.Entity;
import entities.Ship;

/**
 * @author a8011484
 *
 */
public class Game extends BasicGame {
	Ship playerShip;
	
	Client client;
	
	
	public Game() {
		super("Asteroids");
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		Iterator<Entity> entityIterator = Entity.getAllEntities().iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();
			entity.render(g);
		}
		
		

	}

	@Override
	public void init(GameContainer container) throws SlickException {
		playerShip = new Ship();
		
		connectToServer();
	}

	private void connectToServer() {
		client = new Client();
		client.start();
		try {
			client.connect(5000, "localhost", 2112, 2113);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Kryo kryo = client.getKryo();
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
		
		ConnectionRequest connectionRequest = new ConnectionRequest();
		connectionRequest.setPlayerId(playerShip.getPlayerId());
		client.sendTCP(connectionRequest);
		
		client.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof ConnectionResponse) {
			         ConnectionResponse response = (ConnectionResponse)object;
			         System.out.println(response.getPlayerId());
			         
			      }
			      else if (object instanceof EntityResponse) {
			    	  Entity.entities.clear();
			    	  Entity.entities = ((EntityResponse)object).entities;
			      }
			   }
			});
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		/*updatePlayerShip(container, delta, input);		
		updateBullets(container, delta);
		detectCollisions();*/
		
		updateEntities();
		
	}
	
	private void updateEntities() {
		EntityRequest request = new EntityRequest();
		client.sendTCP(request);
	}
	

	private void detectCollisions() {
		//collisions.detect();
	}

	private void updateBullets(GameContainer container, int delta) {
		Iterator<Entity> bulletIterator = Entity.getEntitiesByClass(Bullet.class).iterator();
		while (bulletIterator.hasNext()) {
			Bullet bullet = (Bullet) bulletIterator.next();
			
			bullet.update(delta);	
			
			if (bullet.getPosition().x > container.getWidth() 
					|| bullet.getPosition().x < 0 
					|| bullet.getPosition().y > container.getHeight() 
					|| bullet.getPosition().y < 0 ) {
				bulletIterator.remove();
				bullet.remove();
				bullet = null;
			}
		}
	}

	private void updatePlayerShip(GameContainer container, int delta,
			Input input) {
		playerShip.update(input.getMouseX(), input.getMouseY(), delta);
		
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			playerShip.thrust();
			playerShip.particleSystem.getEmitter(0).setEnabled(true);
		} else {
			playerShip.particleSystem.getEmitter(0).setEnabled(false);
		}
		
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			new Bullet(playerShip.getAngle(), playerShip.getPosition().x, playerShip.getPosition().y, playerShip.getPlayerId());
		}
		
		if (playerShip.getPosition().x > container.getWidth()) {
			playerShip.setX(0);
		}
		
		if (playerShip.getPosition().x < 0) {
			playerShip.setX(container.getWidth());
		}
		
		if (playerShip.getPosition().y > container.getHeight()) {
			playerShip.setY(0);
		}
		
		if (playerShip.getPosition().y < 0) {
			playerShip.setY(container.getHeight());
		}
	}


	public static void main(String[] args) {
		try { 
		    AppGameContainer container = new AppGameContainer(new Game()); 
		    container.setDisplayMode(Constants.CONTAINER_WIDTH,Constants.CONTAINER_HEIGHT,false); 
		    container.setVSync(true);
		    container.start();
		} catch (SlickException e) { 
		    e.printStackTrace(); 
		}

	}

}
