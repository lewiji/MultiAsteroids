/**
 * 
 */
package core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;

import entities.Asteroid;
import entities.AsteroidFactory;
import entities.Bullet;
import entities.BulletFactory;
import entities.Entity;
import entities.Ship;
import entities.ShipFactory;

/**
 * @author a8011484
 *
 */
public class Game extends BasicGame {
	Ship playerShip;
	
	public Game() {
		super("Asteroids");
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
		Iterator<Ship> shipIterator = ShipFactory.ships.iterator();
		while (shipIterator.hasNext()) {
			Ship ship = shipIterator.next();
			g.draw((Shape) ship.getDrawable());
		}
		
		Iterator<Bullet> bulletIterator = BulletFactory.bullets.iterator();
		while (bulletIterator.hasNext()) {
			Bullet bullet = bulletIterator.next();
			g.draw((Shape) bullet.getDrawable());
		}
		
		Iterator<Asteroid> asteroidIterator = AsteroidFactory.asteroids.iterator();
		while (asteroidIterator.hasNext()) {
			Asteroid asteroid = asteroidIterator.next();
			g.draw((Shape) asteroid.getDrawable());
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		playerShip = new Ship();
		ShipFactory.addShip(playerShip);
		AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_BIGGEST));

		AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_BIGGEST));

		AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_BIGGEST));
		AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_BIGGEST));

		AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_BIGGEST));

		AsteroidFactory.addAsteroid(new Asteroid(Constants.ASTEROID_SIZE_BIGGEST));
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		updatePlayerShip(container, delta, input);		
		updateBullets(container, delta);
		updateAsteroids(container, delta);
		detectCollisions();
	}
	
	private void updateAsteroids(GameContainer container, int delta) {
		Iterator<Asteroid> asteroidIterator = AsteroidFactory.asteroids.iterator();
		while (asteroidIterator.hasNext()) {
			Asteroid asteroid = asteroidIterator.next();
			
			asteroid.update(delta);
			
			if (asteroid.getX() > container.getWidth()) {
				asteroid.setX(0);
			}
			
			if (asteroid.getX() < 0) {
				asteroid.setX(container.getWidth());
			}
			
			if (asteroid.getY() > container.getHeight()) {
				asteroid.setY(0);
			}
			
			if (asteroid.getY() < 0) {
				asteroid.setY(container.getHeight());
			}
		}
	}
	
	private void detectCollisions() {
		EntityCollisions collisions = new EntityCollisions();
		collisions.detect();
	}

	private void updateBullets(GameContainer container, int delta) {
		Iterator<Bullet> bulletIterator = BulletFactory.bullets.iterator();
		while (bulletIterator.hasNext()) {
			Bullet bullet = bulletIterator.next();
			
			bullet.update(delta);	
			
			if (bullet.getX() > container.getWidth() 
					|| bullet.getX() < 0 
					|| bullet.getY() > container.getHeight() 
					|| bullet.getY() < 0 ) {
				bulletIterator.remove();
				BulletFactory.removeBullet(bullet);
				bullet = null;
			}
		}
	}

	private void updatePlayerShip(GameContainer container, int delta,
			Input input) {
		playerShip.update(input.getMouseX(), input.getMouseY(), delta);
		
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			playerShip.thrust();
		}
		
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			BulletFactory.addBullet(new Bullet(playerShip.getAngle(), playerShip.getX(), playerShip.getY(), playerShip.getPlayerId()));
		}
		
		if (playerShip.getX() > container.getWidth()) {
			playerShip.setX(0);
		}
		
		if (playerShip.getX() < 0) {
			playerShip.setX(container.getWidth());
		}
		
		if (playerShip.getY() > container.getHeight()) {
			playerShip.setY(0);
		}
		
		if (playerShip.getY() < 0) {
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
