package twelveengine.actors;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.network.NetworkCore;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Pawn extends Biped {
	
	public float reach;
	public float toss;
	
	public ArrayList<Item> inventory;
	
	public boolean fp = false;
	
	public boolean netInv;
	
	public Pawn(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Pawn";
		
		inventory = new ArrayList<Item>();
		reach = tag.getProperty("reach", 3);
		toss = tag.getProperty("toss", 5);
	}
	
	public void netStep(NetworkCore net) {
		super.netStep(net);
		if(netInv) {
			//TODO: SEND A PACKET30INVENTORY
			netInv = false;
		}
	}
	
	//Called when synchronizing gamestate. Sends all the information about this actor to the specified client (via connection hash)
	public void netState(NetworkCore net, int hash) {
		super.netState(net, hash);
		//TODO: SEND A PACKET30INVENTORY
	}
	
	public void movement(Vertex a) {
		if(onGround) {
			Vertex m = new Vertex(0,0,0);
			m = MathUtil.add(m, moveOnLookX(move, a.x));
			m = MathUtil.add(m, moveOnLookY(move, a.y));
			m = MathUtil.multiply(MathUtil.normalize(m), moveSpeed);
			push(m);
		}
		else {		
			Vertex m = new Vertex(0,0,0);
			m = MathUtil.add(m, moveOnLookX(move, a.x));
			m = MathUtil.add(m, moveOnLookY(move, a.y));
			m = MathUtil.multiply(MathUtil.multiply(MathUtil.normalize(m), moveSpeed), airControl);
			push(m);
		}
	}
	
	public Vertex moveOnLookX(Vertex b, float d) {
		Vertex c = MathUtil.normalize(MathUtil.multiply(b, new Vertex(d,d,d)));
		return c;
	}
	
	public Vertex moveOnLookY(Vertex b, float d) {
		Vertex e = new Vertex(-b.y, b.x, b.z);
		Vertex c = MathUtil.normalize(MathUtil.multiply(e, new Vertex(d,d,d)));
		return c;
	}
	
	public void pickup(Item i) {
		System.out.println("picked: " + i.getName());
		inventory.add(i);
		i.picked(this);
		game.sendPacket(new Packet31Pickup(nid, i.nid));
		netInv = true;
	}
	
	public void drop(Item i, Vertex l, Vertex v, Quat r) {
		System.out.println("droped: " + i.getName());
		inventory.remove(i);
		i.drop(l, v, r);
		game.sendPacket(new Packet32Drop(nid, i.nid));
		netInv = true;
	}
	
	/** DEBUG **/
	public String inventoryContents() {
		int i = 0;
		String s = "";
		while(i < inventory.size()) {
			if(inventory.get(i) != null)
				s += (i+1) + ": " + inventory.get(i).getName() + "\n";
			else
				s += (i+1) + ":\n";
			i++;
		}
		return s;
	}
	
}
