package twelveengine.data;

import twelveengine.data.Quat;
import twelveengine.data.Vertex;

public class AnimationNode {
	public int frame;
	public Vertex location;
	public Quat rotation;
	public AnimationNode(int id, Vertex x, Quat y) {
		frame = id;
		location = x;
		rotation = y;
	}
}