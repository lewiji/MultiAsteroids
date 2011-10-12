package entities;

public interface Entity {
	
	public void update(int delta);
	
	public Object getDrawable();
	
	public float getX();

	public float getY();
	
	public void goneOffScreen();
}
