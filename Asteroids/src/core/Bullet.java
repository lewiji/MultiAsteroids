package core;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

public class Bullet extends Entity {
	private float velocity = 0.6f;
	private float rotation = 0.0f;
	private Shape shape;
	
	public Bullet(float rot, float x, float y) {
		rotation = rot;
		shape = new Circle(x, y, 1.0f);
	}
	
	public void update(int delta) {
		shape.setCenterX((float) (shape.getCenterX() - Math.sin(rotation) * velocity * delta));
		shape.setCenterY((float) (shape.getCenterY() - -Math.cos(rotation) * velocity * delta));
	}

	public float getX() {
		return shape.getCenterX();
	}

	public float getY() {
		return shape.getCenterY();
	}

	@Override
	public Object getDrawable() {
		return shape;
	}

	@Override
	public void goneOffScreen() {
		// TODO Auto-generated method stub
		
	}
}
