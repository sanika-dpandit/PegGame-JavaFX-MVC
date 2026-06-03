package PegGamee;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;



/**
 * Represents a square board in a Peg Game.
 */

public class SqrBoard implements PegGame {

    private boolean[][] gameboard; // True if a peg is present, false if it's empty 
    private GameState gameState;

    /**
     * Constructs a square board for a Peg Game.
     * 
     * @param board The initial configuration of the board where 'true' represents a peg, and 'false' represents an empty space.
     */
    @Test
    public SqrBoard(boolean[][] board) {
        this.gameboard = board;
        this.gameState = GameState.NOT_STARTED;
    }
    
    /**
     * Gets the current game state based on the number of pegs and possible moves.
     * 
     * @return The current game state.
     */
    @Override
    @Test
    public GameState getGameState() {
        int NumofPegs = 0;
        for (boolean[] row : gameboard) {
            for (boolean peg : row) {
                if (peg) 
                    NumofPegs++;
            }
        }

        Collection<Move> possibleMoves = getPossibleMoves();
        if (NumofPegs == 1) {
            gameState = GameState.WON;
        } else if (possibleMoves.isEmpty()) {
            gameState = GameState.STALEMATE;
        } else {
            gameState = GameState.IN_PROGRESS;
        }
        return gameState;
    }

    /**
     * Checks if a move from one location to another is a valid move.
     * 
     * @param from    The starting location of the move.
     * @param to      The destination location of the move.
     * @param midRow  The row coordinate of the peg being jumped over.
     * @param midCol  The column coordinate of the peg being jumped over.
     * @return True if the move is valid, false otherwise.
     */
    @Test

    public boolean isValidMove(Location from, Location to) {
        int midRow = (from.getRow() + to.getRow()) / 2;
        int midCol = (from.getCol() + to.getCol()) / 2;
    
        // Check bounds
        if (from.getRow() < 0 || from.getRow() >= gameboard.length || from.getCol() < 0 || from.getCol() >= gameboard[0].length ||
            to.getRow() < 0 || to.getRow() >= gameboard.length || to.getCol() < 0 || to.getCol() >= gameboard[0].length ||
            midRow < 0 || midRow >= gameboard.length || midCol < 0 || midCol >= gameboard[0].length) {
            return false;
        }
    
        // Distance must be exactly two cells in a straight line
        boolean isHorizontalMove = from.getRow() == to.getRow() && Math.abs(from.getCol() - to.getCol()) == 2;
        boolean isVerticalMove = from.getCol() == to.getCol() && Math.abs(from.getRow() - to.getRow()) == 2;
    
        // Check that 'from' has a peg, 'to' is empty, and 'mid' has a peg
        boolean isFromPeg = gameboard[from.getRow()][from.getCol()];
        boolean isToEmpty = !gameboard[to.getRow()][to.getCol()];
        boolean isMidPeg = gameboard[midRow][midCol];
    
        return (isHorizontalMove || isVerticalMove) && isFromPeg && isToEmpty && isMidPeg;
    }
    
    

    /**
     * Adds a valid move to the provided list of moves.
     * 
     * @param fromRow   The row coordinate of the starting location.
     * @param fromCol   The column coordinate of the starting location.
     * @param toRow     The row coordinate of the destination location.
     * @param toCol     The column coordinate of the destination location.
     * @param movesList The list to which the valid move is added.
     */
    @Test
    public void AddValidMove(int fromRow, int fromCol, int toRow, int toCol, List<Move> movesList) {
        Location from = new Location(fromRow, fromCol);
        Location to = new Location(toRow, toCol);
    
        if (isValidMove(from, to)) {
            movesList.add(new Move(from, to));
        }
    }

    /**
     * Gets a collection of possible moves on the current board.
     * 
     * @return A collection of possible moves.
     */
    @Override
    @Test
    public Collection<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        for (int row = 0; row < gameboard.length; row++) {
            for (int col = 0; col < gameboard[row].length; col++) {
                if (gameboard[row][col]) { // If there's a peg at this position
                    // Check and add valid moves in all directions directly here

                    AddValidMove(row, col, row, col + 2, possibleMoves); // Right
                    AddValidMove(row, col, row, col - 2, possibleMoves); // Left
                    AddValidMove(row, col, row + 2, col, possibleMoves); // Down
                    AddValidMove(row, col, row - 2, col, possibleMoves); // Up
                    
                    // Diagonal Moves
                    AddValidMove(row, col, row - 2, col - 2, possibleMoves); // Diagonal Up-Left
                    AddValidMove(row, col, row - 2, col + 2, possibleMoves); // Diagonal Up-Right
                    AddValidMove(row, col, row + 2, col - 2, possibleMoves); // Diagonal Down-Left
                    AddValidMove(row, col, row + 2, col + 2, possibleMoves); // Diagonal Down-Right
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Makes a move on the board based on the provided move object.
     * 
     * @param move The move to be made.
     * @throws PegGameException If the move is invalid.
     */
    @Override
    @Test
    public void makeMove(Move move) throws PegGameException {
        Location fromLocation = move.getFrom();
        Location toLocation = move.getTo();
        int midRow = (fromLocation.getRow() + toLocation.getRow()) / 2;
        int midCol = (fromLocation.getCol() + toLocation.getCol()) / 2;

        if (!isValidMove(fromLocation, toLocation)) { 
            throw new PegGameException("Invalid move");
        }

        gameboard[fromLocation.getRow()][fromLocation.getCol()] = false; // Remove the jumping peg
        gameboard[midRow][midCol] = false; // Remove the jumped over peg
        gameboard[toLocation.getRow()][toLocation.getCol()] = true; // Place the peg in the new location
        
        // Update the game state after the move
        getGameState();
    }

    /**
     * Converts the current board state to a string representation.
     * 
     * @return A string representation of the board.
     */
    @Override
    @Test
    public String toString() {
    String printboard = "";
    for (boolean[] row : gameboard) {
        for (boolean isPeg : row) {
            printboard += isPeg ? "o " : ". ";
        }
        printboard += "\n";
    }
    return printboard;}

    public boolean[][] getGameBoard(){
        return gameboard;
    }

            @Override
            @Test
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + Arrays.deepHashCode(gameboard);
                result = prime * result + ((gameState == null) ? 0 : gameState.hashCode());
                return result;
            }
    
            @Override
            @Test
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                SqrBoard other = (SqrBoard) obj;
                if (!Arrays.deepEquals(gameboard, other.gameboard))
                    return false;
                if (gameState != other.gameState)
                    return false;
                return true;
        }

           
        }    
