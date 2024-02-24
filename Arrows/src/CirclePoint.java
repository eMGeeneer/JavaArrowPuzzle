// String name = "Ming Gao";
// String date = "1/25/22";
// Purpose: to detect when a circle has been pressed

public class CirclePoint {
	private int x;
	private int y;
	private static int[] c = new int[2];
	private static boolean gridType;
	
	public CirclePoint(int n1, int n2) {
		x = n1;
		y = n2;
	}
	
	public static void setCentre(int cx, int cy) {
		c[0] = cx;
		c[1] = cy;
	}
	
	public static void setGridType(boolean b) {
		gridType = b; // false for hexagonal grid and true for square grid
	}
	
	// returns if two points are within 51 pixels of each other
	// @returns If they are with 51 pixels of each other
	public boolean equals(Object o) {
		CirclePoint c = (CirclePoint) o;
		return (c.x - x) * (c.x - x) + (c.y - y) * (c.y - y) < 2602;
	}
	
	// returns a hashCode based on the general location of the point by mapped the screen to a grid and shifting every other column down half a unit if the grid is hexagonal
	// @returns The hashCode
	public int hashCode() {
		if (gridType) {
			return (x - c[0]) / 126 * 1000 + (y - c[1]) / 126;
		}
		return (x - c[0]) / 151 * 1000 + (y - c[1] + Math.abs((x - c[0]) / 151) % 2 * 75) / 151;
	}
}
