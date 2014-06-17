package twelveengine.data;

import twelveengine.Game;
import twelveengine.actors.Item;
import twelveutil.MathUtil;

public class Respawner {
	public Game game;
	public Vertex location;
	public String file;
	public int respawnTime;
	
	public Item item = null;
	public boolean moved = false;
	public int currentTime = 0;
	
	public Respawner(Game g, Vertex a, String o) {
		game = g;
		location = a;
		file = o;
		respawnTime = 2400;
		respawn();
	}
	
	public void step() {
		if(moved) {
			currentTime++;
			if(currentTime >= respawnTime) {
				respawn();
			}
		}
		if(item != null && !moved) {
			if(MathUtil.length(item.location, location) > 5 || item.dead || item.garbage) {
				System.out.println(item.toString() + " was moved");
				moved = true;
			}
			else {
				//item.idle = 0;
			}
		}
	}
	
	public void respawn() {
		item = (Item)(game.createTag(file, location, new Vertex(), new Quat()));
		game.addActor(item);
		currentTime = 0;
		moved = false;
	}
	
}