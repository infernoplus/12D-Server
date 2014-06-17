package twelveengine.actors;

import twelveengine.Game;
import twelveengine.data.*;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Projectile extends Physical {
	public Actor owner;
	
	public boolean expended = false;
	
	public int life;
	
	public float damage;
	public float damageRadius;
	public float force;
	
	public Projectile(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Projectile";
		
		owner = null;
		
		life = tag.getProperty("lifespan", 90);
		damage = tag.getProperty("damage", 10f);
		damageRadius = tag.getProperty("damageradius", 10f);
		force = tag.getProperty("force", 5f);
	}
}
