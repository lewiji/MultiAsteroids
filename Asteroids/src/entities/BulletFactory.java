package entities;

import java.util.ArrayList;
import java.util.List;

public class BulletFactory {
	public static List<Bullet> bullets = new ArrayList<Bullet>();
	
	public static void addBullet(Bullet aBullet) {
		bullets.add(aBullet);
	}
	
	public static void removeBullet(Bullet aBullet) {
		bullets.remove(aBullet);
		aBullet.remove();
	}
}
