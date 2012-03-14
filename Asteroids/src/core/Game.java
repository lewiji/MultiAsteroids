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
import server.BulletRequest;
import server.BulletResponse;
import server.BulletUpdate;
import server.ConnectionRequest;
import server.ConnectionResponse;
import server.KryoRegistration;
import server.ShipRequest;
import server.ShipResponse;

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
	
	public static HashMap<Integer, Asteroid> asteroids = new HashMap<Integer, Asteroid>();
	public static HashMap<Integer, Ship> ships = new HashMap<Integer, Ship>();
	public static HashMap<Integer, Bullet> bullets = new HashMap<Integer, Bullet>();
	
	public Game() {
		super("Asteroids");
	}

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
		
		playerShip.render(g);
		/*
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
					Asteroid roid = Game.asteroids.get(response.id);
					if (roid != null) {
						asteroids.remove(roid);
					} 
					Asteroid newRoid = new Asteroid(response.size, response.x, response.y);
					newRoid.rotation = response.rot;
					asteroids.put(response.id, newRoid);
					
				}
				else if (object instanceof ShipResponse) {
					ShipResponse response = (ShipResponse)object;
					Ship ship = Game.ships.get(response.playerId);
					if (ship != null) {
						asteroids.remove(ship);
					}
					Ship newShip = new Ship();
					newShip.position.x = response.x;
					newShip.position.y = response.y;
					newShip.rotation = response.rot;
					ships.put(response.playerId, newShip);
				}
				else if (object instanceof BulletResponse) {
					BulletResponse response = (BulletResponse)object;
					Bullet bullet = bullets.get(response.bulletId);
					if (bullet != null) {
						bullets.remove(bullet);
					}
					Bullet newBullet = new Bullet(0, response.x, response.y, response.playerId);
					bullets.put(response.bulletId, newBullet);
				}
		   }
		});
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		
		//updateBullets(container, delta);
		//detectCollisions();
		
		updateAsteroids();
		updatePlayerShip(container, delta, input);
		client.sendTCP(new BulletRequest());
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
			BulletUpdate request = new BulletUpdate();
			request.angle = playerShip.getAngle();
			request.x = playerShip.getPosition().x;
			request.y = playerShip.getPosition().y;
			request.playerId = playerShip.playerId;
			client.sendTCP(request);
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
		
		ShipRequest shipRequest = new ShipRequest();
		shipRequest.playerId = playerShip.playerId;
		shipRequest.x = playerShip.position.x;
		shipRequest.y = playerShip.position.y;
		shipRequest.rot = playerShip.rotation;
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
