// String name = "Ming Gao";
// String date = "1/25/22";
// Purpose: to make the hexagonal grid

public class HexGrid {
	private byte dir;
	private int[][] grid;
	
	public HexGrid(int n, byte d) {
		grid = new int[n + 1][];
		int[] r = new int[1];
		grid[0] = r;
		for (int i = 1; i <= n; i++) {
			r = new int[i * 6];
			grid[i] = r;
		}
		dir = d;
	}
	
	// presses a coordinate on the hexagonal grid and rotates that point and all adjacent points
	// @param r The distance from the centre
	// @param t The number of the point in the ring
	// @return If this results in a win
	public boolean press(int r, int t) {
		if (r == 0) {
			grid[0][0] = (grid[0][0] + 1) % dir;
			for (int i = 0; i < 6; i++) {
				grid[1][i] = (grid[1][i] + 1) % dir;
			}
		}
		else {
			grid[r][t] = (grid[r][t] + 1) % dir; // centre
			int ccw = (t - 1 + r * 6) % (r * 6);
			grid[r][ccw] = (grid[r][ccw] + 1) % dir; // counterclockwise one
			int cw = (t + 1) % (r * 6);
			grid[r][cw] = (grid[r][cw] + 1) % dir; // clockwise one
			if (t % r == 0) { // corner case
				int down = t / r * (r - 1);
				grid[r - 1][down] = (grid[r - 1][down] + 1) % dir; // down one level
				if (r + 1 < grid.length) { // up one level
					int up = t / r * (r + 1);
					grid[r + 1][up] = (grid[r + 1][up] + 1) % dir; // directly up
					int uccw = (t / r * (r + 1) - 1 + (r + 1) * 6) % ((r + 1) * 6);
					grid[r + 1][uccw] = (grid[r + 1][uccw] + 1) % dir; // up and counterclockwise one
					int ucw = t / r * (r + 1) + 1;
					grid[r + 1][ucw] = (grid[r + 1][ucw] + 1) % dir; // up and clockwise one
				}
			}
			else { // edge case
				int down = t / r * (r - 1) % ((r - 1) * 6) + t % r - 1;
				grid[r - 1][down] = (grid[r - 1][down] + 1) % dir; // directly down one level
				int dcw = (t / r * (r - 1) % ((r - 1) * 6) + t % r) % ((r - 1) * 6);
				grid[r - 1][dcw] = (grid[r - 1][dcw] + 1) % dir; // down one level and clockwise
				if (r + 1 < grid.length) {
					int ucw = t / r * (r + 1) + t % r + 1;
					grid[r + 1][ucw] = (grid[r + 1][ucw] + 1) % dir; // up one level and clockwise
					int uccw = t / r * (r + 1) + t % r;
					grid[r + 1][uccw] = (grid[r + 1][uccw] + 1) % dir; // up one level and counterclockwise
				}
			}
		}
		for (int i = 0; i < grid.length - 1; i++) {
			for (int j = 0; j < i * 6 || j == 0; j++) {
				if (grid[i][j] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int[][] getGrid() {
		return grid;
	}
	
	// gives the player a hint
	// @return An integer array of length 3 that contains the coordinates of the point and how many times that point must be pressed
	public int[] hint() {
		int[] hint = {0, 0, 0};
		for (int i = grid.length - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (grid[i][j] != 0) {
					hint[0] = i - 1;
					hint[1] = j;
					hint[2] = dir - grid[i][j];
					return hint;
				}
			}
			for (int j = 1; j < 5; j++) {
				for (int k = 1; k < i; k++) {
					if (grid[i][j * i + k] != 0) {
						hint[0] = i - 1;
						hint[1] = j * (i - 1) + k;
						hint[2] = dir - grid[i][j * i + k];
						return hint;
					}
				}
			}
			for (int j = 1; j < i - 1; j++) {
				if (grid[i][5 * i + j] != 0) {
					hint[0] = i - 2;
					hint[1] = 5 * (i - 2) + j;
					hint[2] = dir - grid[i][5 * i + j];
					return hint;
				}
			}
		}
		return hint;
	}
}