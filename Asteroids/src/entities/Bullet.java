package entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;

import core.Constants;

public class Bullet extends Entity {
	private float velocity = 0.5f;
	private float rotation = 0.0f;
	private Image sprite;
	private int playerId;
	
	public Bullet(float rot, float x, float y, int aPlayerId) {
		rotation = rot;
		position.x = x;
		position.y = y;
		
		playerId = aPlayerId;
	}
	
	public void update(int delta) {
		if (sprite == null) {
			loadImage();
		}
		position.x = (float) (position.x - Math.sin(rotation) * velocity * delta);
		position.y = (float) (position.y - -Math.cos(rotation) * velocity * delta);
		
		if (position.x > Constants.CONTAINER_WIDTH ||
			position.x < 0 ||
			position.y > Constants.CONTAINER_HEIGHT ||
			position.y < 0) {
			this.toBeDestroyed = true;
		}
	}

	@Override
	public Image getImage() {
		return sprite;
	}

	@Override
	public void goneOffScreen() {
		// TODO Auto-generated method stub
		
	}
	
	public int getPlayerId() {
		return playerId;
	}

	@Override
	public void handleCollision(Asteroid asteroidOther) {
		this.toBeDestroyed = true;
		
	}

	@Override
	public void handleCollision(Bullet bulletOther) {
		
		
	}

	@Override
	public void handleCollision(Ship shipOther) {
		//BulletFactory.removeBullet(this);
		
	}
	
	@Override
	public void render(Graphics g) {
		if (sprite == null) {
			loadImage();
		}
		sprite.draw(position.x, position.y);
		
	}

	@Override
	public void loadImage() {
		try {
			sprite = new Image("resources/img/bullet.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sprite.setFilter(sprite.FILTER_NEAREST);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		radius = sprite.getWidth();
	}
}
