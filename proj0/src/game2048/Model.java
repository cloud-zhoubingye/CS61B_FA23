package game2048;

import com.google.common.truth.Fact;
import edu.princeton.cs.algs4.StdOut;
import net.sf.saxon.expr.Component;
import org.dom4j.Element;
import picocli.CommandLine;

import javax.swing.*;
import java.util.Formatter;


/** The state of a game of 2048.
 *  @author P. N. Hilfinger + Josh Hug
 */
public class Model {
    /** Current contents of the board. */
    private final Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore) {
        board = new Board(rawValues);
        this.score = score;
        this.maxScore = maxScore;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board. */
    public int size() {
        return board.size();
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        board.clear();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        return maxTileExists(board) || !atLeastOneMoveExists(board);
    }

    /** Checks if the game is over and sets the maxScore variable
     *  appropriately.
     */
    private void checkGameOver() {
        if (gameOver()) {
            maxScore = Math.max(score, maxScore);
        }
    }
    
    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i,j)==null)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by this.MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i,j)!=null&&b.tile(i,j).value()==MAX_PIECE)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        // at least one empty board
        if (emptySpaceExists(b))
            return true;
        // at least two adjacent tiles with the same value
        // for a certain row
        for (int row = 0; row < b.size(); row++) {
            for (int col = 0; col < b.size()-1; col++) {
                if (b.tile(col,row).value()==b.tile(col+1,row).value())
                    return true;
            }
        }
        for (int row = 0; row < b.size()-1; row++) {
            for (int col = 0; col < b.size(); col++) {
                if (b.tile(col,row).value()==b.tile(col,row+1).value())
                    return true;
            }
        }
        return false;
    }

    /** Tilt the board toward SIDE.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public void tilt(Side side) {
        // TODO: Modify this.board (and if applicable, this.score) to account
        // for the tilt to the Side SIDE.
        // MergedFlag to avoid multiple merge
        boolean[][] mergedFlag=new boolean[board.size()+1][board.size()+1];
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                mergedFlag[i][j]=false;     //initialization
            }
        }
        // rotate to side of NORTH
        board.setViewingPerspective(side);
        for (int row = this.board.size()-2; row >= 0; row--) {
            for (int col = 0; col < this.board.size(); col++) {
                // for every element[col][row],
                // search element[col][k] where from 'this.board.size()-1' to 'row'
                if (this.board.tile(col,row)==null)
                    continue;   // if null, do not need to move
                // record first null place
                int firstNullPlace=-1;
                for (int k = this.board.size()-1; k > row ; k--) {
                    if (this.board.tile(col,k)==null) {
                        firstNullPlace = k;
                        break;
                    }
                }
                // check if merge to an empty space is needed
                boolean isMergeWithEmptySpace = firstNullPlace != this.board.size() - 1 && board.tile(col, firstNullPlace +1)!=null &&
                        board.tile(col, firstNullPlace +1).value() == board.tile(col, row).value();
                if (firstNullPlace!=-1&& !mergedFlag[col][firstNullPlace + 1]) {
                    if (isMergeWithEmptySpace) {
                        // update score
                        this.score+=this.board.tile(col,row).value()*2;
                        // move with merge
                        board.move(col,firstNullPlace+1,tile(col,row));
                        mergedFlag[col][firstNullPlace+1]=true;
                    }
                    else
                        // move without merge
                        board.move(col,firstNullPlace,board.tile(col,row));
                }
                // check if merge without empty space space is needed
                boolean isMergeWithoutEmptySpace = row < board.size()-1 &&
                        board.tile(col,row)!=null && board.tile(col,row+1)!=null &&
                        board.tile(col,row).value()==board.tile(col,row+1).value()
                        && !mergedFlag[col][row + 1] ;
                if (isMergeWithoutEmptySpace){
                    board.move(col,row+1,board.tile(col,row));
                    this.score+=board.tile(col,row+1).value();
                }
            }
        }
        //move to void empty interval
        for (int row = 0; row < board.size(); row++) {
            for (int col = 0; col < board.size(); col++) {
                if (board.tile(col,row)==null)
                    continue;
                // record first null place
                int firstNullPlace=-1;
                for (int k = this.board.size()-1; k > row ; k--) {
                    if (this.board.tile(col,k)==null) {
                        firstNullPlace = k;
                        break;
                    }
                }
                if (firstNullPlace != -1){
                    board.move(col,firstNullPlace,board.tile(col,row));
                }
            }
        }
        // rotate to original side
        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
    }


    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}

