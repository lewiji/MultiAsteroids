/**
 * 
 */
package core;

import java.util.Iterator;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;

import entities.Bullet;
import entities.BulletFactory;
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
		
		for (Ship ship : ShipFactory.ships) {
			g.draw((Shape) ship.getDrawable());
		}
		
		for (Bullet bullet : BulletFactory.bullets) {
			g.draw((Shape) bullet.getDrawable());
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		playerShip = new Ship();
		ShipFactory.addShip(playerShip);
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		updatePlayerShip(container, delta, input);		
		updateBullets(container, delta);
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
		    container.setDisplayMode(800,600,false); 
		    container.setVSync(true);
		    container.start();
		} catch (SlickException e) { 
		    e.printStackTrace(); 
		}

	}

}
