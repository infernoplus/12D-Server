package twelveengine.actors;

import java.util.ArrayList;

import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.network.NetworkCore;
import twelveengine.physics.BulletRigidBody;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Item extends RigidBody {
	public Actor owner;
	public boolean autoPick; //If a player walks over this they will automatically pick it up.
	
	public Item(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Item";
		
		autoPick = tag.getProperty("autopickup", false);
		owner = null;
	}
	
	public void step() {
		if(owner == null)
			super.step();
		else
			held();
	}
	
	public void createPhysicsObject() {
		BulletRigidBody r = game.bsp.bullet.createDynamicRigidBody(mass, new Transform(), collision, "item");
		r.setOwner(this);
		physics = r;
	}
	
	//If this item is in somethings inventory and a game tick passes we call this method. It's to update it's location and to perform various actions on it.
	public void held() {
		
	}
	
	//When this item i picked up into somethings inventory call this method.
	public void picked(Actor a) {
		owner = a;
		physics.destroy();
	}
	
	//When this item is dropped out of somethings inventory call this method. v = vector to drop the item, magnitude of v = velocity
	public void drop(Vertex l, Vertex v, Quat r) {
		createPhysicsObject();
		setLocation(l);
		setVelocity(v);
		setRotation(r);
		location = l;
		rotation = r;
		owner = null;
	}
}
