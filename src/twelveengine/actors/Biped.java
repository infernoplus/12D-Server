package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.network.*;
import twelveengine.physics.BulletRigidBody;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Biped extends Physical {
	public Vertex move;
	public Vertex look;
	
	public float maxHealth;
	public float health;
	
	public Vertex moving;
	
	public boolean jumping = false;
	public boolean onGround = false;
	public Collision ground = null;
	
	public float eye;
	
	public float moveSpeed;
	public float acceleration;
	
	public float jumpHeight;
	public float airControl;
	public float friction;
	
	public boolean primary = false;
	public boolean secondary = false;
	
	//These are used to flag a network update
	public boolean netInv;
	
	public Biped(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Biped";
		
		move = new Vertex(0, 1, 0);
		look = new Vertex(0, 1, 0);
		moving = new Vertex();
		
		eye = tag.getProperty("eye", 3f);
		
		jumpHeight = tag.getProperty("jumpheight", 1.5f);
		airControl = tag.getProperty("aircontrol", 0.08f);
		friction = tag.getProperty("friction", 1.0f);
		
		moveSpeed = tag.getProperty("movespeed", 3.0f);
		acceleration = tag.getProperty("acceleration", 0.9f);
		
		maxHealth = tag.getProperty("maxhealth", 100f);
		health = maxHealth;
		
		createPhysicsObject();
		setLocation(l);
		setRotation(r);
		setVelocity(v);
	}
	
	//Creates and adds this actors physics object to the bullet physics method.
	//TODO: Why exactly did I decided to call them both RigidBody.....
	public void createPhysicsObject() {
		BulletRigidBody r = game.bsp.bullet.createDynamicRigidBody(mass, new Transform(), collision, "player");
		r.setSleepingThresholds(0.0f, 0.0f);
		r.setAngularFactor(0.0f);
		r.setFriction(friction);
		r.setOwner(this);
		physics = r;
	}
	
	public void step() {
		super.step();
		if(dead)
			return;
		physics();
		hitboxUpdate();
	}
	
	public void netStep(NetworkCore net) {
		super.netStep(net);
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
		
		onGround = game.bsp.bullet.isOnGround(physics, new Vector3f(0,0,-3.0f));
		
		Vertex x = new Vertex(v.x, v.y, v.z);
		float i = MathUtil.magnitude(x);
		float k = MathUtil.magnitude(MathUtil.add(x, moving));
		//System.out.println("velocity " + MathUtil.toString(velocity) + " | magnitude i: " + i + " magnitude k: " + k + " movespeed: " + moveSpeed + " | push force: " + MathUtil.toString(moving));
		
		//TODO: k for now but needs lots of work to make it feel right
		if(i >= moveSpeed)
			;
		else
			push(moving);
		
		moving = new Vertex();
		
		if(physics.getActivationState() != BulletRigidBody.ISLAND_SLEEPING) {
			netLoc = true; 
			netRot = true;
		}
	}
	
	public void health() {
		if(health <= 0) {
			kill();
		}
		else {
			if(health < maxHealth)
				setHealth(health + 0.1f);
			if(health > maxHealth)
				setHealth(maxHealth);
		}
	}
	
	public void jump() {
		if(onGround) {
			push(new Vertex(0, 0, jumpHeight*2));
			jumping = true;
		}
	}
	
	public void setHealth(float h) {
		health = h;
		//game.engine.network.packetsOut.add(new Packet42Health(nid, health));
	}
	
	public void damage(float d, Vertex i, Actor a) {
		setHealth(health-d);
		//game.engine.network.packetsOut.add(new Packet13Push(nid, i.x, i.y, i.z));
		//attacker = a;
	}
	
	public void kill() {
		//dropAll();
		//TODO: make actual method for messages (IE FOR REPRINTING IN CONSOLE)
		/*if(attacker != null)
			game.engine.network.sendMessage(name + " was fragged by " + attacker.name);
		else
			game.engine.network.sendMessage(name + " died.");*/
		dead = true;
		physics.destroy();
		destroy();
	}
	
	public void destroy() {
		super.destroy();
		physics.destroy();
	}
}
