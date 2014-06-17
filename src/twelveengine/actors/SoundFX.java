package twelveengine.actors;

import twelveengine.Game;
import twelveengine.data.*;

public class SoundFX extends Actor {	
	public float radius;
	public float volume;
	public boolean loop;
	
	//NEVER INSTANTIATE AN ACTOR OR ACTOR SUBCLASS MANUALLY / use engine.game.addObject(); Actor classes are just template for a tag like assets/object/character/generic to fill out and become an object in the world through
	public SoundFX(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":SoundFX";
		
		radius = tag.getProperty("radius", 10f);
		volume = tag.getProperty("volume", 1f);
		loop = tag.getProperty("loop", false);
	}
	
	//TODO: set this up for network. Server says play and all the clients obey. 
	public void play() {

	}
	
	public void pause() {

	}
	
	public void stop() {

	}
	
	public void step() {
		
	}
}
