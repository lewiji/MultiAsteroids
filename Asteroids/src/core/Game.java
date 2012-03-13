/**
 * 
 */
package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import server.AsteroidRequest;
import server.AsteroidResponse;
import server.ConnectionRequest;
import server.ConnectionResponse;
import server.KryoRegistration;
import server.PlayerShipRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import entities.Asteroid;
import entities.Bullet;
import entities.Ship;

/**
 * @author a8011484
 *
 */
public class Game extends BasicGame {
	Ship playerShip;
	
	Client client;	
	
	HashMap<Integer, Asteroid> asteroids = new HashMap<Integer, Asteroid>();
	ArrayList<Ship> ships = new ArrayList<Ship>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	
	boolean rendering = false;
	
	public Game() {
		super("Asteroids");
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
		HashMap<Integer, Asteroid> asteroidsCopy = (HashMap<Integer, Asteroid>) asteroids.clone();

		for (int i = 0; i < asteroids.size(); i++) {
			Asteroid entity = asteroids.get(i);
			entity.render(g);
		}
		
		/*Iterator<Ship> shipIter = ships.iterator();

		while (shipIter.hasNext()) {
			Ship entity = shipIter.next();
			entity.render(g);
		}
		
		Iterator<Bullet> bulletIter = bullets.iterator();

		while (bulletIter.hasNext()) {
			Bullet entity = bulletIter.next();
			entity.render(g);
		}*/
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
		KryoRegistration.register(kryo);
		
		ConnectionRequest connectionRequest = new ConnectionRequest();
		client.sendTCP(connectionRequest);
		
		client.addListener(new Listener() {
			public void received (Connection connection, Object object) {

				if (object instanceof ConnectionResponse) {
				         ConnectionResponse response = (ConnectionResponse)object;
				         System.out.println(response.getPlayerId());
				         playerShip.playerId = response.getPlayerId();
				}
				else if (object instanceof AsteroidResponse) {
					AsteroidResponse response = (AsteroidResponse)object;
					Asteroid roid = asteroids.get(response.id);
					if (roid != null) {
						roid.position.x = response.x;
						roid.position.y = response.y;
						roid.rotation = response.rot;
						roid.size = response.size;
					} else {
						Asteroid newRoid = new Asteroid(response.size, response.x, response.y);
						newRoid.rotation = response.rot;
						asteroids.put(response.id, newRoid);
					}
					
				}
		   }
		});
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		//updatePlayerShip(container, delta, input);		
		/*updateBullets(container, delta);
		detectCollisions();*/
		
		updateAsteroids();
		
	}
	
	private void updateAsteroids() {
			AsteroidRequest request = new AsteroidRequest();
			client.sendTCP(request);
	}
	

	private void detectCollisions() {
		//collisions.detect();
	}

	/*private void updateBullets(GameContainer container, int delta) {
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
	}*/

	private void updatePlayerShip(GameContainer container, int delta,
			Input input) {
		playerShip.update(input.getMouseX(), input.getMouseY(), delta);
		
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			playerShip.thrust();
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
		
		PlayerShipRequest shipRequest = new PlayerShipRequest();
		shipRequest.ship = playerShip;
		client.sendTCP(shipRequest);
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
