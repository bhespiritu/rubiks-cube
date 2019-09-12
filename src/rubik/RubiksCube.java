package rubik;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RubiksCube extends JPanel implements KeyListener, MouseListener, MouseWheelListener {

	// for the sake of simplicity, each square of the cube is going to 1 square unit
	// in world space.

	// red is front face, white is top, yellow is the bottom.

	public boolean isPerspective = false;

	public ArrayList<Plane> squares = new ArrayList<Plane>(); // TODO: Visual Test, remove later plz.
	
	public Vector3[] handles = new Vector3[6];
	// F U B D L R

	Camera mainCamera;

	public float spacing = 1.1f;

	public static Color red = Color.RED;
	public static Color blue = Color.BLUE;
	public static Color yellow = Color.YELLOW;
	public static Color orange = new Color(255, 140, 0);
	public static Color white = Color.WHITE;
	public static Color green = Color.GREEN;

	public static int redNum = 0;
	public static int blueNum = 1;
	public static int yellowNum = 2;
	public static int orangeNum = 3;
	public static int whiteNum = 4;
	public static int greenNum = 5;

	private float roll = (float) (2 * Math.PI / 3) + .5f;

	private Vector3 oPos, oFor, oUp;

	public Face front, right, left, down, back, up;

	public int frontDir = 0;
	
	private int selectedSide = -1;

	private String queue = "";

	private RotatingFace currentRotatingFace;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Rubik's Cube");
		RubiksCube cube = new RubiksCube();
		frame.setContentPane(cube);
		frame.addKeyListener(cube);
		frame.addMouseListener(cube);
		frame.addMouseWheelListener(cube);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		for (;;) {
			cube.timestep();
			cube.repaint();
			try {
				Thread.sleep(1000 / 24);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	Axes a = new Axes();

	public RubiksCube() {

		setPreferredSize(new Dimension(300, 200));
		setBackground(Color.BLACK);
		mainCamera = new Camera();
		if(isPerspective)
			mainCamera = new PerspectiveCamera();

		mainCamera.position.z += 5;
		
		mainCamera.position.x -= 5;
		mainCamera.position.y -= 5;

		mainCamera.forward = mainCamera.position.mult(-1).normalize();

		oPos = mainCamera.position;
		oFor = mainCamera.forward;
		oUp = mainCamera.up;

		front = new Face();
		front.axis = 2;

		int i = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				front.colors[i] = greenNum;
				Plane p = new Plane();
				p.position = new Vector3(x, y, 1.5f).mult(spacing);
				p.color = green;
				p.normal = Vector3.FORWARD;
				p.right = Vector3.RIGHT;
				p.debugNum = i;
				front.planes[i] = p;
				squares.add(front.planes[i]);
				i++;
			}
		}

		back = new Face();
		back.axis = 2;
		i = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = 1; x >= -1; x--) {
				back.colors[i] = orangeNum;
				Plane p = new Plane();
				p.position = new Vector3(x, y, -1.5f).mult(spacing);
				p.color = orange;
				p.normal = Vector3.FORWARD.mult(-1);
				p.right = Vector3.RIGHT;
				p.debugNum = i;
				back.planes[i] = p;
				squares.add(back.planes[i]);
				i++;
			}
		}

		down = new Face();
		down.axis = 1;

		i = 0;
		for (int y = 1; y >= -1; y--) {
			for (int x = -1; x <= 1; x++) {
				down.colors[i] = yellowNum;
				Plane p = new Plane();
				p.position = new Vector3(x, 1.5f, y).mult(spacing);
				p.color = yellow;
				p.normal = Vector3.UP;
				p.right = Vector3.FORWARD;
				p.debugNum = i;
				down.planes[i] = p;
				squares.add(down.planes[i]);
				i++;
			}
		}

		up = new Face();
		up.axis = 1;

		i = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				up.colors[i] = whiteNum;
				Plane p = new Plane();
				p.position = new Vector3(x, -1.5f, y).mult(spacing);
				p.color = white;
				p.normal = Vector3.UP.mult(-1);
				p.right = Vector3.FORWARD;
				p.debugNum = i;
				up.planes[i] = p;
				squares.add(up.planes[i]);
				i++;
			}
		}

		left = new Face();
		left.axis = 0;

		i = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				left.colors[i] = redNum;
				Plane p = new Plane();
				p.position = new Vector3(-1.5f, y, x).mult(spacing);
				p.color = red;
				p.normal = Vector3.RIGHT.mult(-1);
				p.right = Vector3.FORWARD;
				p.debugNum = i;
				left.planes[i] = p;
				squares.add(left.planes[i]);
				i++;
			}
		}

		right = new Face();
		right.axis = 0;
		i = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = 1; x >= -1; x--) {

				right.colors[i] = blueNum;
				Plane p = new Plane();
				p.position = new Vector3(1.5f, y, x).mult(spacing);
				p.color = blue;
				p.normal = Vector3.RIGHT;
				p.right = Vector3.FORWARD;
				p.debugNum = i;
				right.planes[i] = p;
				squares.add(right.planes[i]);
				i++;
			}
		}
		
		handles[0] = front.planes[4].position.copy();
		handles[1] = up.planes[4].position.copy();
		handles[2] = back.planes[4].position.copy();
		handles[3] = down.planes[4].position.copy();
		handles[4] = left.planes[4].position.copy();
		handles[5] = right.planes[4].position.copy();
		
	}

	private void debugColorScheme() {
		up.colors[0] = 0;
		up.colors[1] = 1;
		up.colors[2] = 1;
		up.colors[3] = 2;
		up.colors[4] = 3;
		up.colors[5] = 1;
		up.colors[6] = 5;
		up.colors[7] = 5;
		up.colors[8] = 3;
		up.updateColors();
	}

	public float time = 0;

	private Vector3 mouse;

	public void timestep() {
		
		PerspectiveCamera pc = dummy;
		if(mainCamera instanceof PerspectiveCamera)
		{
			pc = (PerspectiveCamera) mainCamera;
		}
		
		pc.yaw = (float) (Math.atan2(-pc.position.z, pc.position.x)+Math.PI/2);
		pc.pitch = (float) (Math.acos(pc.position.y/pc.position.magnitude())-Math.PI/2);
		//pc.pitch = (float) (Math.PI/4);
		

		if (currentRotatingFace != null) {
			currentRotatingFace.progress += 0.1f;
			currentRotatingFace.update();

			if (currentRotatingFace.progress > 1) {
				finishFrontRotate();

			}
		} else if (!queue.isEmpty()) {

			char c = queue.charAt(0);
			queue = queue.substring(1);
			// System.out.println(c + " " + queue);
			boolean cap = !Character.isUpperCase(c);
			c = Character.toLowerCase(c);
			if (c == 'f')
				frontRotate(cap);
			if (c == 'x')
				xRotate(cap, false);
			if (c == 'y')
				yRotate(cap, false);
			if (c == 'q')
				xRotate(cap);
			if (c == 'e')
				yRotate(cap);
			if (c == 'r') {
				String command = cap ? "efE" : "eFE";
				queue = command + queue;
			}
			if (c == 'b') {
				String command = cap ? "eefEE" : "eeFEE";
				queue = command + queue;
			}
			if (c == 'l') {
				String command = cap ? "Efe" : "EFe";
				queue = command + queue;
			}
			if (c == 'd') {
				String command = cap ? "Qfq" : "QFq";
				queue = command + queue;
			}
			if (c == 'u') {
				String command = cap ? "qfQ" : "qFQ";
				queue = command + queue;
			}
			if (c == 's') {
				scramble();
			}
		}

		time += 0.1;
		time %= Math.PI * 2;
		// a.forward = mainCamera.forward;
		// a.right = Vector3.RIGHT.rotate(a.forward, time).normalize();

	}

	public void scramble() {

		int move;
		int prevMove = 0;
		boolean prevClock = false;
		boolean clockWise;
		for (int i = 0; i < 25; i++) {

			move = (int) (Math.random() * 6);
			clockWise = Math.random() < .5;
			if (i != 0) {
				if (prevMove == move) {
					if (clockWise = !prevClock)
						clockWise = prevClock;
				}
			}
			switch (move) {
			case 0:
				queue += clockWise ? 'f' : 'F';
				break;
			case 1:
				queue += clockWise ? 'r' : 'R';
				break;
			case 2:
				queue += clockWise ? 'l' : 'L';
				break;
			case 3:
				queue += clockWise ? 'b' : 'B';
				break;
			case 4:
				queue += clockWise ? 'u' : 'U';
				break;
			case 5:
				queue += clockWise ? 'd' : 'D';
				break;
			}
			prevMove = move;
			prevClock = clockWise;
		}
	}

	public void frontRotate(boolean clockwise) {
		currentRotatingFace = new RotatingFace();
		currentRotatingFace.clockwise = clockwise;
		currentRotatingFace.dir = frontDir;
		// System.out.print(frontDir);
		for (int i = 0; i < 9; i++) {
			currentRotatingFace.addPlane(front.planes[i], i);
		}
		currentRotatingFace.addPlane(up.planes[6], 9);
		currentRotatingFace.addPlane(up.planes[7], 10);
		currentRotatingFace.addPlane(up.planes[8], 11);

		currentRotatingFace.addPlane(right.planes[0], 12);
		currentRotatingFace.addPlane(right.planes[3], 13);
		currentRotatingFace.addPlane(right.planes[6], 14);

		currentRotatingFace.addPlane(left.planes[2], 15);
		currentRotatingFace.addPlane(left.planes[5], 16);
		currentRotatingFace.addPlane(left.planes[8], 17);

		currentRotatingFace.addPlane(down.planes[0], 18);
		currentRotatingFace.addPlane(down.planes[1], 19);
		currentRotatingFace.addPlane(down.planes[2], 20);

	}

	public void yRotate(boolean clockwise) {
		yRotate(clockwise, true);
	}

	public void yRotate(boolean clockwise, boolean moveCamera) {

		up.rotate(clockwise);
		down.rotate(!clockwise);

		if (clockwise) {
			Face temp = front;
			front = right;
			right = back;
			back = left;
			left = temp;

		} else {
			Face temp = front;
			front = left;
			left = back;
			back = right;
			right = temp;

		}
		front.rotateCube(1, !clockwise);
		right.rotateCube(1, !clockwise);
		back.rotateCube(1, !clockwise);
		left.rotateCube(1, !clockwise);

		if (moveCamera) {
			int sign = clockwise ? 1 : -1;

			mainCamera.position = mainCamera.position.rotate(1, (float) (Math.PI / 2) * sign);
			mainCamera.up = mainCamera.up.rotate(1, (float) (Math.PI / 2) * sign);
			mainCamera.forward = mainCamera.forward.rotate(1, (float) (Math.PI / 2) * sign);
		}
	}

	public void xRotate(boolean clockwise) {
		xRotate(clockwise, true);
	}

	public void xRotate(boolean clockwise, boolean moveCamera) {

		right.rotate(!clockwise);
		left.rotate(clockwise);

		if (!clockwise) {
			back.rotate(clockwise);
			back.rotate(clockwise);
			back.rotateCube(2, clockwise);
			back.rotateCube(2, clockwise);
			up.rotate(clockwise);
			up.rotate(clockwise);
			up.rotateCube(1, clockwise);
			up.rotateCube(1, clockwise);

			Face temp = front;
			front = down;
			down = back;
			back = up;
			up = temp;

		} else {
			down.rotate(clockwise);
			down.rotate(clockwise);
			down.rotateCube(1, clockwise);
			down.rotateCube(1, clockwise);
			back.rotate(clockwise);
			back.rotate(clockwise);
			back.rotateCube(2, clockwise);
			back.rotateCube(2, clockwise);

			Face temp = front;
			front = up;
			up = back;
			back = down;
			down = temp;

		}

		if (moveCamera) {
			int sign = clockwise ? -1 : 1;

			mainCamera.position = mainCamera.position.rotate(0, (float) (Math.PI / 2) * sign);
			mainCamera.up = mainCamera.up.rotate(0, (float) (Math.PI / 2) * sign);
			mainCamera.forward = mainCamera.forward.rotate(0, (float) (Math.PI / 2) * sign);
		}

		front.rotateCube(0, clockwise);
		up.rotateCube(0, clockwise);
		back.rotateCube(0, clockwise);
		down.rotateCube(0, clockwise);

		// back.rotate(clockwise);back.rotate(clockwise);
		// back.rotateCube(2, clockwise); back.rotateCube(2, clockwise);
	}

	public void finishFrontRotate() {
		currentRotatingFace.progress = 0;
		currentRotatingFace.update();

		for (int j = 0; j < 3; j++) {

			int temp = front.colors[0];
			front.colors[0] = front.colors[6];
			front.colors[6] = front.colors[8];
			front.colors[8] = front.colors[2];
			front.colors[2] = temp;

			temp = front.colors[1];
			front.colors[1] = front.colors[3];
			front.colors[3] = front.colors[7];
			front.colors[7] = front.colors[5];
			front.colors[5] = temp;

			for (int i = 0; i < 3; i++) {
				temp = left.colors[8];
				left.colors[8] = down.colors[0];
				down.colors[0] = down.colors[1];
				down.colors[1] = down.colors[2];
				down.colors[2] = right.colors[6];
				right.colors[6] = right.colors[3];
				right.colors[3] = right.colors[0];
				right.colors[0] = up.colors[8];
				up.colors[8] = up.colors[7];
				up.colors[7] = up.colors[6];
				up.colors[6] = left.colors[2];
				left.colors[2] = left.colors[5];
				left.colors[5] = temp;
			}

			if (currentRotatingFace.clockwise)
				break;
		}

		front.updateColors();
		left.updateColors();
		right.updateColors();
		up.updateColors();
		down.updateColors();
		back.updateColors();

		currentRotatingFace = null;

	}

	public static Color colorfromNum(int num) {
		if (num == redNum)
			return red;
		if (num == whiteNum)
			return white;
		if (num == orangeNum)
			return orange;
		if (num == blueNum)
			return blue;
		if (num == yellowNum)
			return yellow;
		if (num == greenNum)
			return green;
		return Color.BLACK;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Point mouseLocation = this.getMousePosition();

		if (mouseLocation == null)
			mouseLocation = new Point(0, 0);
		mouseLocation.x -= getX();
		mouseLocation.y -= getY();
		mouse = mainCamera.screenToWorld(mouseLocation, getWidth(), getHeight());

		int closest = -1;
		float closestDist = 10000;
		for (int i = 0; i < handles.length; i++) {
			Vector3 p = handles[i];
			Vector3 relative = mainCamera.forward.cross(p.sub(mouse));
			float dir = mainCamera.position.dot(p);
			float dist = relative.magnitude();
			if (dist <= .5 && dir > 0) {
				if (dist < closestDist) {
					closest = i;
					closestDist = dist;
				}
			}
		}
		if (closest != -1) {
			//System.out.println(closest + " " + mouse);
			a.position = handles[closest];
			selectedSide = closest;
		} else
		{
			a.position = mouse;
		}
		

		g.translate(getWidth() / 2, getHeight() / 2);

		for (int i = 0; i < squares.size(); i++) {
			squares.get(i).dist = squares.get(i).position.sub(mainCamera.position).sqrMagnitude();
		}

		Collections.sort(squares);

		for (int i = 0; i < squares.size(); i++) {
			squares.get(i).draw(mainCamera, g);
		}
		a.draw(mainCamera, g);
		g.translate(-getWidth() / 2, -getHeight() / 2);

	}

	PerspectiveCamera dummy = new PerspectiveCamera();
	
	private float distance = 8;
	
	@Override
	public void keyPressed(KeyEvent e) {
		float rightAngle = (float) (Math.PI / 20);
		
		Vector3 camRight = mainCamera.up.cross(mainCamera.forward);
		//Vector3 camUp = camRight.cross(mainCamera.forward);
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {

			mainCamera.position = mainCamera.position.rotate(mainCamera.up, rightAngle).mult(distance);
			
			//pc.yaw += rightAngle;
			//mainCamera.up = mainCamera.up.rotate(1, rightAngle);
			mainCamera.forward = mainCamera.forward.rotate(mainCamera.up, rightAngle);
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			mainCamera.position = mainCamera.position.rotate(mainCamera.up, -rightAngle).mult(distance);
			//mainCamera.up = mainCamera.up.rotate(1, -rightAngle);
			mainCamera.forward = mainCamera.forward.rotate(mainCamera.up, -rightAngle);
			//pc.yaw -= rightAngle;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			mainCamera.position = mainCamera.position.rotate(camRight, -rightAngle).mult(distance);
			//mainCamera.up = mainCamera.up.rotate(camRight, -rightAngle);
			mainCamera.forward = mainCamera.forward.rotate(camRight, -rightAngle);
			//pc.pitch += rightAngle;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			mainCamera.position = mainCamera.position.rotate(camRight, rightAngle).mult(distance);
			//mainCamera.up = mainCamera.up.rotate(camRight, rightAngle);
			mainCamera.forward = mainCamera.forward.rotate(camRight, rightAngle);
			//pc.pitch -= rightAngle;
		}

		if (e.getKeyCode() == KeyEvent.VK_NUMPAD5) {
			mainCamera.position = oPos;
			mainCamera.forward = oFor;
			mainCamera.up = oUp;

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// System.out.print(e.getKeyChar());
		queue += e.getKeyChar();

		// System.out.println(e.getKeyCode());

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(currentRotatingFace != null) return;
		boolean clockwise = SwingUtilities.isLeftMouseButton(e);
		String command;
		switch(selectedSide)
		{
		case 0: //front
			frontRotate(clockwise);
			break;
		case 1: //up
			command = clockwise ? "qfQ" : "qFQ";
			queue = command + queue;
			break;
		case 2: //back
			command = clockwise ? "eefEE" : "eeFEE";
			queue = command + queue;
			break;
		case 3: //down
			command = clockwise ? "Qfq" : "QFq";
			queue = command + queue;
			break;
		case 4: //left
			command = clockwise ? "Efe" : "EFe";
			queue = command + queue;
			break;
		case 5: //right
			command = clockwise ? "efE" : "eFE";
			queue = command + queue;
			break;
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(mainCamera instanceof PerspectiveCamera)
		{
			distance += e.getWheelRotation()*0.1f;
			mainCamera.position = mainCamera.position.normalize().mult(distance);
		}
		else
			mainCamera.zoom -= e.getWheelRotation();
		
	}

}
