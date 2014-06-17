package twelveengine.actors;

import javax.vecmath.*;
import com.bulletphysics.linearmath.*;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.network.*;
import twelveengine.physics.*;
import twelveutil.*;

public class RigidBody extends Physical {
	//A rigidboy actor is an actor that has a physics model and uses bulletphysics.
	//Extend this if you want rigid body physics on an actor.
	public RigidBody(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":RigidBody";
		
		createPhysicsObject();
		setLocation(l);
		setRotation(r);
		setVelocity(v);
	}
	
	//Creates and adds this actors physics object to the bullet physics method.
	//TODO:Ragdollssss.....
	public void createPhysicsObject() {
		BulletRigidBody r = game.bsp.bullet.createDynamicRigidBody(mass, new Transform(), collision, "object");
		r.setOwner(this);
		physics = r;
	}
	
	public void step() {
		super.step();
		physics();
		hitboxUpdate();
	}
	
	public void netStep(NetworkCore net) {
		super.netStep(net);
	}
	
	//TODO: this.
	public void physics() {
		if(physics == null)
			return;
		//Get current state of bullet physics object
		Vector3f c = physics.getCenterOfMassPosition(new Vector3f());
		Vector3f v = physics.getLinearVelocity(new Vector3f());
		Quat4f q = physics.getOrientation(new Quat4f());
		//Apply it to actor.
		location = new Vertex(c.x, c.y, c.z);
		rotation = new Quat(q.x, q.y, q.z, q.w);
		velocity = new Vertex(v.x, v.y, v.z);
		if(physics.getActivationState() != BulletRigidBody.ISLAND_SLEEPING) {
			netLoc = true; 
			netRot = true;
		}
	}
	
	public void destroy() {
		super.destroy();
		physics.destroy();
	}
}