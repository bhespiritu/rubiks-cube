package rubik;

public class Face {
	public int[] colors = new int[9]; // left to right, top to bottom
	public Plane[] planes = new Plane[9];
	public int axis;


	public void updateColors() {
		for (int i = 0; i < 9; i++) {
			planes[i].color = RubiksCube.colorfromNum(colors[i]);
			planes[i].debugNum = i;
		}
	}

	public void rotate(boolean clockwise) {
		int temp;
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 2; i++) {
				temp = colors[0];
				colors[0] = colors[3];
				colors[3] = colors[6];
				colors[6] = colors[7];
				colors[7] = colors[8];
				colors[8] = colors[5];
				colors[5] = colors[2];
				colors[2] = colors[1];
				colors[1] = temp;

			}

			if (clockwise)
				break;
		}
		updateColors();
	}
	
	public void rotateCube(int axis, boolean clockwise) {
		int sign = clockwise ? -1 : 1;
		for(int i = 0; i < 9; i++)
		{
			planes[i].position = planes[i].position.rotate(axis, (float) (sign*(Math.PI/2)));
			planes[i].normal = planes[i].normal.rotate(axis, (float) (sign*(Math.PI/2)));
			planes[i].right = planes[i].right.rotate(axis, (float) (sign*(Math.PI/2)));
		}
	}

}
