package twelveengine.actors;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.physics.BulletRigidBody;

public class Scenery extends Physical {
	public AnimationGroup animations;
	
	public Scenery(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Scenery";
		
		createPhysicsObject();
		setLocation(l);
		setRotation(r);
		setVelocity(v);
	}
	
	public void createPhysicsObject() {
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		BulletRigidBody r = game.bsp.bullet.createStaticRigidBody(startTransform, collision, "world");
		r.setOwner(this);
		physics = r;
	}
	
	//Normal animation system
	public void step() {
		super.step();
		if(animate) {
			lastFrame = frame;
			if(frame < animation.frames.length-1)
				frame++;
			else
				if(loop)
					frame = 0;
		}
		physics();
		hitboxUpdate();
	}
	
	//TODO: this.
	public void physics() {
		//Get current state of bullet physics object
		Vector3f c = physics.getCenterOfMassPosition(new Vector3f());
		Vector3f v = physics.getLinearVelocity(new Vector3f());
		Quat4f q = physics.getOrientation(new Quat4f());
		//Apply it to actor.
		location = new Vertex(c.x, c.y, c.z);
		rotation = new Quat(q.x, q.y, q.z, q.w);
		velocity = new Vertex(v.x, v.y, v.z);
	}
}
