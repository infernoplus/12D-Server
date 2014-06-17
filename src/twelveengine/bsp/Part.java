package twelveengine.bsp;

import java.util.ArrayList;

import twelveengine.data.*;

public class Part {
	//This class is essentially a container for a part of a bsp. 
	//It's so I can just have one object instead of like 6 arrays for each part of a bsp.
	public PhysModel collision;
	public Vertex location;
	public Quat rotation;
	public float scale;
	
	public Part(PhysModel p, Vertex l, Quat r, float s) {
		collision = p;
		location = l;
		rotation = r;
		scale = s;
	}
}