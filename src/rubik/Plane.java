package rubik;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class Plane extends Object3D implements Comparable<Plane>{

	public Color color;
	
	public int debugNum = -1;
	
	public Vector3 normal;
	public Vector3 right;//the vector that points toward the center of each face.
	
	public float dist;//used for calculating occlusion
	
	public void draw(Camera c, Graphics g)
	{
		if(normal.dot(c.forward) >= 0) return; //backface culling
		
		Color temp = g.getColor();
		g.setColor(color);
		//if(normal.dot(c.forward) >= 0) g.setColor(color.darker());; 
		
		/**
		 * up
		 * ^  inside of square
		 * X > right 
		 */
		
		Vector3 up = normal.cross(right).normalize(); 
		
		Vector3 ll = position.sub(right.mult(.5f)).sub(up.mult(.5f));
		
		Vector3 lr = ll.add(right);
		Vector3 ul = ll.add(up);
		Vector3 ur = ul.add(right);
		
		Point llp = c.projectPoint(ll);
		Point lrp = c.projectPoint(lr);
		Point ulp = c.projectPoint(ul);
		Point urp = c.projectPoint(ur);
		
		Polygon p = new Polygon();
		p.addPoint(llp.x, llp.y);
		p.addPoint(lrp.x, lrp.y);
		p.addPoint(urp.x, urp.y);
		p.addPoint(ulp.x, ulp.y);
		
		
		g.fillPolygon(p);
		
//		g.drawLine(llp.x, llp.y, lrp.x, lrp.y); wireframe rendering
//		g.drawLine(lrp.x, lrp.y, urp.x, urp.y);
//		g.drawLine(urp.x, urp.y, ulp.x, ulp.y);
//		g.drawLine(ulp.x, ulp.y, llp.x, llp.y);
		
		Point pixelPos = c.projectPoint(position);
		
		if(debugNum >= 0) 
			{
			//g.setColor(Color.DARK_GRAY);
			//g.drawString(debugNum+"", pixelPos.x, pixelPos.y);
			}
		
		g.setColor(temp);
		
	}
	
	
	
	@Override
	  public int compareTo(Plane other) {
	    
	    return Float.compare(other.dist, dist);
	  }
	
	
}
