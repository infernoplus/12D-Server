package twelveengine.data;

import twelveutil.MathUtil;

public class Vertex {
	public float x;
	public float y;
	public float z;	
	
	//Name is mostly unused but is important for some things
	public String name;
	
	public Vertex() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vertex(float a, float b, float c) {
		x = a;
		y = b;
		z = c;
	}
	
	public Vertex(float a, float b, float c, String n) {
		x = a;
		y = b;
		z = c;
		name = n;
	}
	
	public Vertex copy() {
		return new Vertex(x,y,z);
	}
}
