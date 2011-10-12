package entities;

import java.util.ArrayList;
import java.util.List;

public class ShipFactory {
	public static List<Ship> ships = new ArrayList<Ship>();
	
	public static void addShip(Ship aShip) {
		ships.add(aShip);
	}
	
	public static void removeShip(Ship aShip) {
		ships.remove(aShip);
	}
}
