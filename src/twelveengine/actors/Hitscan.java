package twelveengine.actors;

import twelveengine.Game;
import twelveengine.data.*;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Hitscan extends Actor {

	public Actor hit;
	
	public int lifeSpan;
	
	public float damage;
	
	public Hitscan(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Hitscan";
		
		lifeSpan = tag.getProperty("lifespan", 10);
		damage = tag.getProperty("damage", 5);
	}
	
	public void step() {
		if(lifeSpan > 0)
			lifeSpan--;
		else
			kill();
	}
}