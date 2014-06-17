package twelveengine.actors;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.network.*;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Actor {
	public Game game;
	
	public String file;
	public String name;//Name of the tag generally.
	
	public String internalName; //Class names
	public int nid; //Network ID, used to sync actors over the network. //No 2 actors should ever have the same nid for any reason. //nid < 0 == don't sync. (negative numbers)
	public String sid; //Script ID, used to get actors by a name.
	
	public Vertex location, lastLocation;
	public Quat rotation, lastRotation;
	public Vertex velocity, lastVelocity;
	
	//When true, stops the client from doing come calculations. This allows the server or player cloud to do them instead.
	//Used for a variety of things, EX for deciding whether to do physics on non-local players or doing local damage calculations
	//If it is not set in the tag file then it defaults to false. 
	public boolean noClientSimulate;
	
	public boolean dead = false;
	public boolean garbage = false;
	
	public float radius;
	
	protected Tag tag;
	
	public boolean netLoc;
	public boolean netRot;
	
	//This the "abstract" super class for all actors (aka objects) in the game world.
	//All actors will take a tag file of the same class or super class in their constructor, data to generate the actor will be read from that file. 
	//All actors will also take a network ID (NID) and a pointer to the game they are in.
	//Additionally all actors will take !class specfic values like location rotation and velocity
	public Actor(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		internalName = "Actor";
		game = w;
		nid = n;
		
		tag = game.tagger.openTag(f);
		
		file = f;
		name = tag.getProperty("name", "DEFAULT");
		noClientSimulate = tag.getProperty("noclientsimulate", false);
		radius = tag.getProperty("radius", 10);
		
		location = l;
		velocity = v;
		rotation = r;
		
		sid = "";
	}
	
	//Called on each new frame
	public void step() {

	}
	
	//This method is called to send update packets to clients. 
	public void netStep(NetworkCore net) {
		if(netLoc) {
			net.send(new Packet20Location(nid, false, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z));
			netLoc = false;
		}
		if(netRot) {
			net.send(new Packet21Rotation(nid, false, rotation.x, rotation.y, rotation.z, rotation.w));
			netRot = false;
		}
	}
	
	//Called when synchronizing gamestate. Sends all the information about this actor to the specified client (via connection hash)
	public void netState(NetworkCore net, int hash) {
		net.send(new Packet10Instantiate(nid, file, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z, rotation.x, rotation.y, rotation.z, rotation.w), hash);
	}
	
	//Do not modify location rotation and velocity directly, use these. Some actors may handle these differntly VIA overide.
	public void move(Vertex a) {
		location = MathUtil.add(location, a);
		netLoc = true;
	}
	
	public void rotate(Quat a) {
		rotation = MathUtil.add(rotation, a);
		netRot = true;
	}
	
	public void push(Vertex a) {
		velocity = MathUtil.add(velocity, a);
		netLoc = true;
	}
	
	public void setLocation(Vertex a) {
		location = a;
		netLoc = true;
	}
	
	public void setRotation(Quat a) {
		rotation = a;
		netRot = true;
	}
	
	public void setVelocity(Vertex a) {
		velocity = a;
		netLoc = true;
	}
	
	//Method used to apply damage, overridden to actually have effect in subclass :P
	//Vertex v is a directional vector to push this actor. For explosions. Magnitude of push = magnitude of vertex.
	public void damage(float d, Vertex v, Actor a) {
		
	}
	
	/** Register any new script invokes here**/
	//Invoke is called from scripts or the console to modify an instance of an actor ingame.
	//Always include the super class invoke. The most basic functions of this are in the top levels.
	public void invoke(String m, String p[]) {
		if(m.equals("location")) {
			setLocation(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("velocity")) {
			setVelocity(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("rotation")) {
			setRotation(new Quat(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2]),Float.parseFloat(p[3])));
			return;
		}
		if(m.equals("move")) {
			move(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("push")) {
			push(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("rotate")) {
			rotate(new Quat(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2]),Float.parseFloat(p[3])));
			return;
		}
		if(m.equals("sid")) {
			sid = p[0];
			return;
		}
		if(m.equals("effect")) {
			//Server, so fuck this shit
			return;
		}
		if(m.equals("kill")) {
			kill();
			return;
		}
		if(m.equals("destroy")) {
			destroy();
			return;
		}
	}
	
	//Name of the object, sometimes this will be unique. Mostly not.
	public String getName() {
		return name;
	}
	
	//Information about the object.
	public String toString() {
		return internalName + "." + name + "@" + MathUtil.toString(location) + ":" + nid;
	}
	
	//Type gives the class that this is and all its super classes in order. Such as Actor:Item:Weapon:PlasmaWeapon
	public String getType() {
		return internalName;
	}
	
	//Puts actor in a state of "dead" which will eventually be destroyed and removed from the game. EX: killed player plays death animation and lays on ground for a while then is destroyed.
	public void kill() {
		dead = true;
		destroy();
	}
	
	//Flags this item to get lost, specifically for the game to remove it on the next step during garbage clean up
	public void destroy() {
		dead = true;
		garbage = true;
		if(game.isOnline())
			game.sendPacket(new Packet12Destroy(nid));
	}
	
	//Unloads any files that need to go away.
	public void unload() {
		
	}
}
