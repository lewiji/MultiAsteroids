package server;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitter.ColorRecord;
import org.newdawn.slick.particles.ConfigurableEmitter.LinearInterpolator;
import org.newdawn.slick.particles.ConfigurableEmitter.RandomValue;
import org.newdawn.slick.particles.ConfigurableEmitter.Range;
import org.newdawn.slick.particles.ConfigurableEmitter.SimpleValue;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.ClassSerializer;

import entities.Asteroid;
import entities.Entity;
import entities.Ship;

public class KryoRegistration {
	public static void register(Kryo kryo) {
		kryo.register(ConnectionRequest.class);
		kryo.register(ConnectionResponse.class);
		kryo.register(EntityRequest.class);
		kryo.register(EntityResponse.class);
		kryo.register(PlayerShipRequest.class);
		kryo.register(AsteroidRequest.class);
		kryo.register(AsteroidResponse.class);
		kryo.register(Asteroid.class);
		kryo.register(Vector2f.class);
		kryo.register(Polygon.class);
		kryo.register(float[].class);
		kryo.register(HashMap.class);
		kryo.register(Entity.class);
		kryo.register(Class.class, new ClassSerializer(kryo));
		kryo.register(ArrayList.class);
		kryo.register(Ship.class);
		kryo.register(PlayerShipResponse.class);
		kryo.register(ShipRequest.class);
		kryo.register(ShipResponse.class);
		kryo.register(BulletUpdate.class);
		kryo.register(BulletResponse.class);
		kryo.register(BulletRequest.class);
		kryo.register(AsteroidDestroyResponse.class);
		kryo.register(AsteroidPOJO.class);
		kryo.register(BulletPOJO.class);
		kryo.register(ShipDestroyResponse.class);
	}
}
