package rubik;

import javax.swing.JPanel;

public class RotatingFace extends Object3D{

	public static final int FRONT = 0;
	public static final int TOP = 1;
	public static final int BACK = 2;
	public static final int BOTTOM = 3;
	public static final int LEFT = 4;
	public static final int RIGHT = 5;
	
	public int dir;
	/* 0 front
	 * 1 top
	 * 2 back
	 * 3 bottom
	 * 4 left
	 * 5 right
	 */
	
	private Vector3 perp;
	
	public boolean clockwise = true;
	
	public float progress = 0;
	
	public Plane[] planes = new Plane[21]; 
	//0-9 is the main face 10-21 is the wrapping around line
	
	public Vector3[] origins = new Vector3[21];
	public Vector3[] normals = new Vector3[21];
	public Vector3[] rights = new Vector3[21];
	
	public RotatingFace() {
		
	}
	
	public void addPlane(Plane p, int i)
	{
		planes[i] = p;
		origins[i] = p.position;
		normals[i] = p.normal;
		rights[i] = p.right;
	}
	
	public void update()
	{
		
		
		int clock = clockwise ? 1: -1;
		
		for(int i = 0; i < 21; i++)
		{
			//if(planes[i] == null) continue;
			Plane p = planes[i];
			p.position = origins[i].rotate(2, (float) ((Math.PI/2)*progress)*clock);
			p.normal = normals[i].rotate(2, (float) ((Math.PI/2)*progress)*clock);
			p.right = rights[i].rotate(2, (float) ((Math.PI/2)*progress)*clock);
		}

		
	}
	
}
