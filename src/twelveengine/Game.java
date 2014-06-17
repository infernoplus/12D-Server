package twelveengine;

import java.util.ArrayList;

import twelveengine.actors.*;
import twelveengine.bsp.*;
import twelveengine.data.*;
import twelveengine.network.*;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Game {
	
	//Core Stuff
	public Engine engine;
	
	//This games scenario and it's important values
	public Scenario scenario;
	
	//Engine values
	public static int stepTime = 30;
	public static int netTime = 30;
	public int time = 0;
	
	//World, be wary when switching BSPs
	public BSP bsp;
	
	//Tagreader is used by actors to load tag files which give properties and values for creating stuff
	public TagReader tagger = new TagReader();
	//All games objects in the world are in here.
	public ArrayList<Actor> actors = new ArrayList<Actor>();
	//Used for respawning stuff on a timer. Like weapons and powerups.
	public ArrayList<Respawner> respawners = new ArrayList<Respawner>();
	
	//Use getAnimation() and etc. Do not instantiate it yourself, the only exception to this is if it will be directly modified
	//EXAMPLE: You have 15 cyborgs on the map, instead of loading 15 cyborg models you load 1 and the model data is shared between all 15 cyborgs. Same for animations and what not.
	public ArrayList<AnimationGroup> animations = new ArrayList<AnimationGroup>();
	public ArrayList<ConvexHull> hulls = new ArrayList<ConvexHull>();
	
	public Game(Engine e, String s) {
		engine = e;
		loadLevel(s);
	}
	
	public void loadLevel(String s) {
		scenario = new Scenario(this, s);
		bsp = new BSP(this, scenario.bsp);
		System.out.println("Map: " + s + " loaded sucsessfully.");
	}
	
	public void step() {
		//Garbage time
		int i = 0;
		while(i < actors.size()) {
			if(actors.get(i).garbage) {
				removeActor(actors.get(i).nid);
				i--;
			}
			i++;
		}
		//Game step
		//Step actors, and bsp
		bsp.step(); //Update bsp and bullet physics objects.
		i = 0;
		while(i < actors.size()) {
			actors.get(i).step();
			i++;
		}
		//Respawners check
		i = 0;
		while(i < respawners.size()) {
			respawners.get(i).step();
			i++;
		}
	}
	
	public void netStep(NetworkCore net) {
		//Send packets
		int i = 0;
		while(i < actors.size()) {
			Actor a = actors.get(i);
			a.netStep(net);
			i++;
		}
	}
	
	public Actor spawnPlayer(String name) {
		Actor a = createTag(scenario.getPlayerTag(), getSpawn(), new Vertex(), new Quat());
		a.name = name;
		addActor(a);
		
		/*		
		int i = 0;
		while(i < scenario.getSpawnEquipmentCount()) {
			Actor b = createTag(scenario.getSpawnEquipmentTag(i), new Vertex(), new Vertex(), new Quat());
			addActor(b);
			((Biped)a).take((Item)b);
			i++;
		}*/
		return a;
	}
	
	public Vertex getSpawn() {
		int i = 0;
		ArrayList<Vertex> spawns = new ArrayList<Vertex>();
		while(i < scenario.playerSpawns.size()) {
			spawns.add(scenario.playerSpawns.get(i));
			i++;
		}
		return spawns.get((int)(Math.random()*spawns.size()));
	}
	
	int gnid = 0;
	public int newNid() {
		gnid++;
		return gnid;
	}
	
	public Actor createTag(String f, Vertex l, Vertex v, Quat r) {
		String s = f.split("\\.")[f.split("\\.").length-1];
		Actor a = null;
		int n = newNid();
		
		if(s.equals("actor"))
			a = new Actor(this, n, f, l, v, r);
		
		if(s.equals("physical"))
			a = new Physical(this, n, f, l, v, r);
		
		if(s.equals("rigidbody"))
			a = new RigidBody(this, n, f, l, v, r);
		
		if(s.equals("scenery"))
			a = new Scenery(this, n, f, l, v, r);
		
		if(s.equals("biped"))
			a = new Biped(this, n, f, l, v, r);
		
		if(s.equals("pawn"))
			a = new Pawn(this, n, f, l, v, r);
		
		if(s.equals("item"))
			a = new Item(this, n, f, l, v, r);
		
		if(s.equals("weapon"))
			a = new Weapon(this, n, f, l, v, r);
		
		if(s.equals("equipment"))
			a = new Equipment(this, n, f, l, v, r);
		
		if(s.equals("projectile"))
			a = new Projectile(this, n, f, l, v, r);
		
		if(s.equals("hitscan"))
			a = new Hitscan(this, n, f, l, v, r);
		
		if(s.equals("soundfx"))
			a = new SoundFX(this, n, f, l, v, r);
		
		if(a == null) {
			System.err.println("UNRECOGNIZED TAG FORMAT: " + f);
		}
		return a;
	}
	
	public Actor addActor(Actor a) {
		actors.add(a);
		Packet10Instantiate i = new Packet10Instantiate(a.nid, a.file, a.location.x, a.location.y, a.location.z, a.velocity.x, a.velocity.y, a.velocity.z, a.rotation.x, a.rotation.y, a.rotation.z, a.rotation.w);
		engine.network.send(i);
		return a;
	}
	
	//For simulated objects
	public Actor addActor(Actor a, int simulated) {
		actors.add(a);
		if(a.nid != -1) {
			//Packet10Instantiate i = new Packet10Instantiate(a.nid, a.file, a.location.x, a.location.y, a.location.z, a.velocity.x, a.velocity.y, a.velocity.z, a.rotation.x, a.rotation.y, a.rotation.z);
			//i.port = simulated;
			//engine.network.packetsOut.add(i);
		}
		return a;
	}
	
	public Actor getActor(int n) {
		int i = 0;
		while(i < actors.size()) {
			if(actors.get(i).nid == n)
				return actors.get(i);
			i++;
		}
		return null;
	}
	
	public void removeActor(int n) {
		int i = 0;
		while(i < actors.size()) {
			if(actors.get(i).nid == n) {
				//engine.network.packetsOut.add(new Packet9Destroy(actors.get(i).nid));
				actors.remove(i);
			}
			i++;
		}
	}
	
	public AnimationGroup getAnimation(String s) {
		int i = 0;
		while(i < animations.size()) {
			if(animations.get(i).file.equals(s)) {
				return animations.get(i);
			}
			i++;
		}
		AnimationGroup a = new AnimationGroup(s);
		animations.add(a);
		return a;
	}
	
	public ConvexHull getHull(String s) {
		int i = 0;
		while(i < hulls.size()) {
			if(hulls.get(i).file.equals(s)) {
				return hulls.get(i);
			}
			i++;
		}
		ConvexHull c = new ConvexHull(s);
		hulls.add(c);
		return c;
	}
	
	public void pauseSounds() {
		
	}
	
	public void resumeSounds() {
		
	}
	
	public void adjustSoundVolume() {
		
	}
	
	public void unloadGame() {

	}

	public void checkSoundArray() {
		
	}
	
	public boolean isOnline() {
		return engine.network.online;
	}
	
	public void sendPacket(Packet p) {
		engine.network.send(p);
	}
	
}
