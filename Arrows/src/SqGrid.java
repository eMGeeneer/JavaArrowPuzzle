// Purpose: to make the square grid

public class SqGrid {
	private int dir;
	private int[][] grid;
	
	public SqGrid(int n, int d) {
		grid = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				grid[i][j] = 0;
			}
		}
		dir = d;
	}
	
	// presses a coordinate and rotates that point and all adjacent points
	// @param x The x coordinate of the point
	// @param y The y coordinate of the point
	// @return If this results in a win
	public boolean press(int x, int y) {
		for (int i = 0; i < 9; i++) {
			if (x + (i % 3) - 1 >= 0 && x + (i % 3) - 1 < grid.length && y + (i / 3) - 1 >= 0 && y + (i / 3) - 1 < grid.length) {
				grid[x + (i % 3) - 1][y + (i / 3) - 1] = (grid[x + (i % 3) - 1][y + (i / 3) - 1] + 1) % dir;
			}
		}
		for (int i = 1; i < grid.length - 1; i++) {
			for (int j = 1; j < grid.length - 1; j++) {
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
		for (int i = 0; i < grid.length - 2; i += 2) {
			for (int j = 0; j < grid.length - i - 2; j++) {
				if (grid[i / 2 + j][i / 2] != 0) {
					hint[0] = i / 2 + j + 1;
					hint[1] = i / 2 + 1;
					hint[2] = dir - grid[i / 2 + j][i / 2];
					return hint;
				}
			}
			for (int j = 0; j < grid.length - i - 3; j++) {
				if (grid[grid.length - i / 2 - 1][i / 2 + j + 1] != 0) {
					hint[0] = grid.length - i / 2 - 2;
					hint[1] = i / 2 + j + 2;
					hint[2] = dir - grid[grid.length - i / 2 - 1][i / 2 + j + 1];
					return hint;
				}
			}
			for (int j = 0; j < grid.length - i - 3; j++) {
				if (grid[grid.length - i / 2 - j - 2][grid.length - i / 2 - 1] != 0) {
					hint[0] = grid.length - i / 2 - j - 3;
					hint[1] = grid.length - i / 2 - 2;
					hint[2] = dir - grid[grid.length - i / 2 - j - 2][grid.length - i / 2 - 1];
					return hint;
				}
			}
			for (int j = 0; j < grid.length - i - 4; j++) {
				if (grid[i / 2][grid.length - i / 2 - j - 2] != 0) {
					hint[0] = i / 2 + 1;
					hint[1] = grid.length - i / 2 - j - 3;
					hint[2] = dir - grid[i / 2][grid.length - i / 2 - j - 2];
					return hint;
				}
			}
		}
		return hint;
	}
}
