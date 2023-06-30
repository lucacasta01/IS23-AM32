package it.polimi.myShelfie.utilities;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * row-column tuple reference
 */
public class Position implements Serializable {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * check if positions are adjacent, i.e. if they are in a row or in a column
     * @param positions list of positions to be checked
     * @return true if adjacent, false otherwise
     */
    public static boolean areAdjacent(List<Position> positions) {
        boolean rAdj=true, cAdj=true;
        int r = positions.get(0).getRow();
        int c = positions.get(0).getColumn();

        int[] rows = new int[positions.size()];
        int[] cols = new int[positions.size()];

        cols[0] = c;
        for(int i=1;i<positions.size();i++){
            if (positions.get(i).getRow() != r) {
                rAdj = false;
            }
            cols[i] = positions.get(i).getColumn();
        }
        if(rAdj){
            Arrays.sort(cols);
            for(int i=0;i<cols.length-1;i++){
                if (cols[i+1] != cols[i] + 1) {
                    rAdj = false;
                    break;
                }
            }
        }

        rows[0] = r;
        for(int i=1;i<positions.size();i++){
            if (positions.get(i).getColumn() != c) {
                cAdj = false;
            }
            rows[i] = positions.get(i).getRow();
        }
        if(cAdj){
            Arrays.sort(rows);
            for(int i=0;i<rows.length-1;i++){
                if (rows[i+1] != rows[i] + 1) {
                    cAdj = false;
                    break;
                }
            }
        }
        return rAdj || cAdj;
    }

}
