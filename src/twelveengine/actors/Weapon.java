package twelveengine.actors;

import twelveengine.Game;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;

public class Weapon extends Item {
	
	public boolean primary = false;
	public boolean secondary = false;
	
	public String primaryFile;
	public String secondaryFile;
	
	public int rof;
	
	public float impulse;
	public int projectiles;
	
	public Vertex spread;
	
	public int shotCount = 0;
	public int shotTimer = 0;
	
	public Weapon(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Weapon";
		
		primaryFile = tag.getProperty("primaryfire", "");
		secondaryFile = tag.getProperty("secondaryfire", "");
		
		rof = tag.getProperty("rateoffire", 30);
		impulse = tag.getProperty("impulse", 256);
		projectiles= tag.getProperty("projectiles", 1);
		spread = new Vertex(tag.getProperty("spreadx", 0.1f),tag.getProperty("spready", 0.1f),0);
	}
}