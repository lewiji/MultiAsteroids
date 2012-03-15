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
import org.newdawn.slick.Sound;

import server.AsteroidRequest;
import server.BulletRequest;
import server.BulletUpdate;
import server.ConnectionRequest;
import server.KryoRegistration;
import server.ShipRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import entities.Asteroid;
import entities.Bullet;
import entities.Ship;

/**
 * @author a8011484
 *
 */
public class Game extends BasicGame {
	public static Ship playerShip;
	
	public static Client client;
	public static Sound laserFx = null;
	public static Sound explosionFx = null;
	public static Sound asteroidExplosionFx = null;
	public static Sound thrustFx = null;
	
	public static ConcurrentHashMap<Integer, Asteroid> asteroids = new ConcurrentHashMap<Integer, Asteroid>();
	public static ConcurrentHashMap<Integer, Ship> ships = new ConcurrentHashMap<Integer, Ship>();
	public static ConcurrentHashMap<Integer, Bullet> bullets = new ConcurrentHashMap<Integer, Bullet>();
	
	public Game() {
		super("Asteroids");
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
		
		Iterator<Bullet> bulletIter = bullets.values().iterator();
		while (bulletIter.hasNext()) {
			Bullet bullet = bulletIter.next();
			bullet.render(g);
		}
		
		Iterator<Ship> shipIter = ships.values().iterator();
		while (shipIter.hasNext()) {
			Ship ship = shipIter.next();
			ship.render(g);
		}
		
		Iterator<Asteroid> asterIter = asteroids.values().iterator();
		while (asterIter.hasNext()) {
			Asteroid roid = asterIter.next();
			roid.render(g);
		}
		
		
		
		playerShip.render(g);
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		playerShip = new Ship();
		
		try {
			laserFx = new Sound("resources/sound/Laser_Shoot2.wav");
			explosionFx = new Sound("resources/sound/Explosion4.wav");
			asteroidExplosionFx = new Sound("resources/sound/Explosion9.wav");
			thrustFx = new Sound("resources/sound/Thrust.wav");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		connectToServer();
	}

	private void connectToServer() {
		client = new Client();
		client.start();
		while (!client.isConnected()) {
			try {
				String address = JOptionPane.showInputDialog(null,
						  "Enter an IP Address:",
						  "Connection",
						  JOptionPane.QUESTION_MESSAGE);
			
				client.connect(5000, address, 2112, 2113);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Kryo kryo = client.getKryo();
		KryoRegistration.register(kryo);
		
		ConnectionRequest connectionRequest = new ConnectionRequest();
		client.sendTCP(connectionRequest);
		client.addListener(new NetworkListener());
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
			thrustFx.play();
		} 
		
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			BulletUpdate request = new BulletUpdate();
			request.angle = (float) (playerShip.getAngle() + Math.toRadians(180));
			request.x = playerShip.getPosition().x;
			request.y = playerShip.getPosition().y;
			request.playerId = playerShip.playerId;
			client.sendTCP(request);
			
			laserFx.play();
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
