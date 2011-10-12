package entities;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class Ship implements Entity {
	private Polygon ship = null;
	private Vector2f thrust = new Vector2f();
	private float rotation = 0.0f;
	
	private float maxThrust = 0.4f;
	private float acceleration = 0.02f;
	
	private static int playerCount = 0;
	private int playerId;
	
	public Ship() {
		ship = new Polygon();
		ship.addPoint(0.0f, 0.0f);
		ship.addPoint(20.0f, 0.0f);
		ship.addPoint(10.0f, 20.0f);
		playerId = playerCount;
		playerCount++;
	}
	
	private void pointAtMouse(float mouseX, float mouseY) {
		
		float shipAngle = (float) (Math.atan2( mouseY - ship.getCenterY(), mouseX - ship.getCenterX()));
		shipAngle += Math.toRadians(-90);
		
		ship = (Polygon) ship.transform(Transform.createRotateTransform(shipAngle - rotation, ship.getCenterX(), ship.getCenterY()));
				
		rotation = shipAngle;
	}
	
	public void update(float mouseX, float mouseY, int delta) {
		pointAtMouse(mouseX, mouseY);
		ship.setCenterY(ship.getCenterY() - (thrust.y * delta));
		ship.setCenterX(ship.getCenterX() - (thrust.x * delta));
	}
	
	public void thrust() {
		thrust.x += Math.sin(rotation) * acceleration;
		thrust.y += -Math.cos(rotation) * acceleration;
		
		if (thrust.x > maxThrust) {
			thrust.x = maxThrust;
		} else if (thrust.x < -maxThrust) {
			thrust.x = -maxThrust;
		}
		
		if (thrust.y > maxThrust) {
			thrust.y = maxThrust;
		} else if (thrust.y < -maxThrust) {
			thrust.y = -maxThrust;
		}
	}
	
	public float getX() {
		return ship.getCenterX();
	}
	
	public float getY() {
		return ship.getCenterY();
	}
	
	public void setX(float x) {
		ship.setCenterX(x);
	}
	
	public void setY(float y) {
		ship.setCenterY(y);
	}

	public float getAngle() {
		return rotation;
	}

	@Override
	public void update(int delta) {
		ship.setCenterY(ship.getCenterY() - (thrust.y * delta));
		ship.setCenterX(ship.getCenterX() - (thrust.x * delta));
	}

	@Override
	public Object getDrawable() {
		return ship;
	}

	@Override
	public void goneOffScreen() {
		
	}

	public int getPlayerId() {
		return playerId;
	}
}
