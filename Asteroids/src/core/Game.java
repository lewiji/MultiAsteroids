/**
 * 
 */
package core;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import server.AsteroidDestroyResponse;
import server.AsteroidPOJO;
import server.AsteroidRequest;
import server.AsteroidResponse;
import server.BulletPOJO;
import server.BulletRequest;
import server.BulletResponse;
import server.BulletUpdate;
import server.ConnectionRequest;
import server.ConnectionResponse;
import server.KryoRegistration;
import server.ShipDestroyResponse;
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
	
	public static ConcurrentHashMap<Integer, Asteroid> asteroids = new ConcurrentHashMap<Integer, Asteroid>();
	public static ConcurrentHashMap<Integer, Ship> ships = new ConcurrentHashMap<Integer, Ship>();
	public static ConcurrentHashMap<Integer, Bullet> bullets = new ConcurrentHashMap<Integer, Bullet>();
	
	public Game() {
		super("Asteroids");
	}

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
		
		playerShip.render(g);
		
		/*Font font = new Font("Verdana", Font.PLAIN, 40);
		UnicodeFont tfont = new UnicodeFont(font , 20, false, false);
		tfont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
	    tfont.addAsciiGlyphs();
	    tfont.addGlyphs(400, 600);
	    tfont.loadGlyphs();
	    tfont.drawString(0,0, "hello", Color.white);*/
		
		
		
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
		client = new Client(16384, 4096);
		client.start();
		while (!client.isConnected()) {
			String address = JOptionPane.showInputDialog(null,
					  "Enter an IP Address:",
					  "Connection",
					  JOptionPane.QUESTION_MESSAGE);
			
			try {
				client.connect(5000, address, 2112, 2113);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
					Iterator<AsteroidPOJO> iter = response.asteroids.iterator();
					while (iter.hasNext()) {
						AsteroidPOJO pojo = iter.next();
						Asteroid asteroid = asteroids.get(pojo.id);
						if (asteroid != null) {
							asteroids.remove(asteroid);
						}
						asteroids.put(pojo.id, new Asteroid(pojo.id, pojo.size, pojo.x, pojo.y));
					}
				}
				else if (object instanceof AsteroidDestroyResponse) {
					AsteroidDestroyResponse response = (AsteroidDestroyResponse)object;
					asteroids.remove(response.id);
				}
				else if (object instanceof ShipResponse) {
					ShipResponse response = (ShipResponse)object;
					Ship ship = Game.ships.get(response.playerId);
					if (ship != null) {
						ships.remove(ship);
					}
					Ship newShip = new Ship();
					newShip.position.x = response.x;
					newShip.position.y = response.y;
					newShip.rotation = response.rot;
					ships.put(response.playerId, newShip);
				}
				else if (object instanceof ShipDestroyResponse) {
					playerShip.killShip();
				}
				else if (object instanceof BulletResponse) {
					BulletResponse response = (BulletResponse)object;
					bullets.clear();
					
					Iterator<BulletPOJO> iter = response.bullets.iterator();
					while (iter.hasNext()) {
						BulletPOJO next = iter.next();
						Bullet bullet = new Bullet(0, next.x, next.y, next.playerId);
						bullets.put(next.bulletId, bullet);
					}
				}
		   }
		});
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		updateAsteroids();
		updatePlayerShip(container, delta, input);
		client.sendTCP(new BulletRequest());
	}
	
	private void updateAsteroids() {
			AsteroidRequest request = new AsteroidRequest();
			client.sendTCP(request);
	}
	

	private void updatePlayerShip(GameContainer container, int delta,
			Input input) {
		playerShip.update(input.getMouseX(), input.getMouseY(), delta);
		
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			playerShip.thrust();
		} 
		
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			BulletUpdate request = new BulletUpdate();
			request.angle = (float) (playerShip.getAngle() + Math.toRadians(180));
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
		shipRequest.invulnerable = playerShip.invulnerable;
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
