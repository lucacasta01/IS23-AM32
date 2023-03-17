package it.polimi.myShelfie.model;
public class Board {
    private Tile[][] grid;

    public Board(Tile[][] grid) {
        this.grid = new Tile[9][9];
    }

    /**
     * Returns the current state of a board's grid
     *
     * @return current state of the grid
     */
    public Tile[][] getGrid() {
        return this.grid;
    }

    /**
     * Checks whether the board's grid needs to be refilled
     * @return true if needed, false otherwise
     */
    public boolean needToRefill() {
        boolean check = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.grid[i][j].getColor() != Tile.Color.NULLTILE) {
                    if (this.grid[Math.min(i + 1, 8)][j].getColor() != Tile.Color.NULLTILE ||
                            this.grid[i][Math.min(j + 1, 8)].getColor() != Tile.Color.NULLTILE ||
                            this.grid[Math.max(i - 1, 0)][j].getColor() != Tile.Color.NULLTILE ||
                            this.grid[i][Math.max(j - 1, 0)].getColor() != Tile.Color.NULLTILE) {
                        check = false;
                    }
                }
            }
        }
        return check;
    }

    /**
     * Checks if a Tile in a given position is catchable by a player
     *
     * @param row    of the tile we want to catch
     * @param column of the tile we want to catch
     * @return true if the Tile is catchable, else otherwise
     */
    public boolean isCatchable(int row, int column) {
        if (this.grid[row][column].getColor() == Tile.Color.NULLTILE) {
            return false;
        }
        return this.grid[Math.min(row + 1, 8)][column].getColor() == Tile.Color.NULLTILE ||
                this.grid[row][Math.min(column + 1, 8)].getColor() == Tile.Color.NULLTILE ||
                this.grid[Math.max(row - 1, 0)][column].getColor() == Tile.Color.NULLTILE ||
                this.grid[row][Math.max(column - 1, 0)].getColor() == Tile.Color.NULLTILE;
    }
}
