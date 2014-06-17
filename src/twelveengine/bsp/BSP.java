package twelveengine.bsp;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.physics.*;
import twelveutil.*;

public class BSP {	
	public Game game;
	public String name;
	public String file;
	
	//Bullet physics representation of the game world BSP.
	public BulletBSP bullet;
	
	public Part parts[];
	
	public BSP (Game w, String f) {
		game = w;
		file = f;
		
		readBsp();
		
		try {
			bullet = new BulletBSP(this, parts);
		}
		catch(Exception e) {
			System.err.println("Failed to build bullet bsp ~ ");
			e.printStackTrace();
		}
		
		
		//TODO: ....
		//Real talk, this is like the only time we will ever read and create assets and then dispose of them instead of cacheing them. 
		//We only build the PhysModel of the bsp so we can hand it to bullet so that bullet can turn it into a trimesh.
		int i = 0;
		while(i < parts.length) {
			parts[i].collision = null;
			i++;
		}
		System.gc();
	}
	
	public void step() {
		if(bullet != null)
			bullet.step();
	}
	
	public void readBsp() {
		Tag tag = game.tagger.openTag(file);
		
		//Load model and collision files
		TagSubObject meshList = tag.getObject("meshes");
		int i = 0;
		int j = meshList.getTotalObjects();
		parts = new Part[j];
		while(i < j) {
			TagSubObject mesh = meshList.getObject(i);
			parts[i] = readStatic(mesh.getProperty("model", "NULL"), mesh.getProperty("collision", "NULL"), TagUtil.makeVertex(mesh.getObject("location")), TagUtil.makeQuat(mesh.getObject("rotation")), mesh.getProperty("scale", 1.0f));
			i++;
		}
	}
	
	public Part readStatic(String m, String c, Vertex l, Quat r, float d) {
	    PhysModel p = new PhysModel(c);
	    return new Part(p, l, r, d);
}
	
	public String toString() {
		return "BSP:" + name;
	}

}
