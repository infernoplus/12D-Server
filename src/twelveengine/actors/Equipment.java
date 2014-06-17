package twelveengine.actors;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.network.NetworkCore;
import twelvelib.net.packets.*;
import twelveutil.*;

public class Equipment extends Item {
	public String type;
	public boolean stacks;
	
	public String fileCreateOnUse;
	
	public int total;
	
	public float impulse;
	
	//These are used to flag an update
	public boolean netTot;
	
	public Equipment(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Equipment";
		
		type = tag.getProperty("type", "Default");
		stacks = tag.getProperty("stacks", true);
		fileCreateOnUse = tag.getProperty("createwhenused", "none");
		total = tag.getProperty("total", 4);
		impulse = tag.getProperty("impulse", 1);
	}
	
	public void netStep(NetworkCore net) {
		super.netStep(net);
	}
	
	public void useEquipment(Biped b) {
		if(total > 0) {
			Actor a = game.createTag(fileCreateOnUse, b.location.copy(), MathUtil.multiply(MathUtil.normalize(b.look.copy()), impulse), new Quat());
			setTotal(total-1);
			game.addActor(a, -1);
		}
	}
	
	public void setTotal(int i) {
		total = i;
		netTot = true;
	}
	
	public String equipmentType() {
		return type;
	}
	
	public String getName() {
		return name + ": " + total;
	}
}