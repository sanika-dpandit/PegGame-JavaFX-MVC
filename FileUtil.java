package PegGamee;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.*;

public class FileUtil {
    /**
 * Loads the game board configuration from a specified file. This method presents a file chooser to the user to select a file.
 * The method reads a square grid configuration where 'o' represents an occupied cell.
 *
 * @param ownerWindow The window that owns the file chooser dialog, typically the main application window.
 * @return A 2D boolean array representing the game board, where true indicates an occupied cell.
 * @throws IOException If an I/O error occurs reading from the file or the file cannot be opened.
 */

    
    public static boolean[][] loadGameBoard(Window ownerWindow) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Board Configuration File");

        File file = fileChooser.showOpenDialog(ownerWindow); 

        if (file != null ) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                int size = Integer.parseInt(reader.readLine().trim());
                boolean[][] gameBoard = new boolean[size][size];

                for (int i = 0; i < size; i++) {
                    String line = reader.readLine();
                    for (int j = 0; j < line.length(); j++) {
                        gameBoard[i][j] = (line.charAt(j) == 'o');
                    }
                }
                return gameBoard;
            }
        }
        return null; // Return null if file is not selected or reading fails
    }

    
    public static void saveGameBoard(Window ownerWindow, boolean[][] gameBoard) throws IOException {
        /**
 * Saves the current state of the game board to a file. This method presents a file chooser to the user for specifying the file to save to.
 * The game board is saved with each cell represented as 'o' for occupied and '.' for unoccupied.
 *
 * @param ownerWindow The window that owns the file chooser dialog, typically the main application window.
 * @param gameBoard The 2D boolean array representing the game board to be saved.
 * @throws IOException If an I/O error occurs writing to the file or the file cannot be saved.
 */

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save your current Gameboard");
        File file = fileChooser.showSaveDialog(ownerWindow);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gameBoard.length + "\n");
                for (boolean[] row : gameBoard) {
                    for (boolean cell : row) {
                        writer.write(cell ? "o" : ".");
                    }
                    writer.newLine();
                }
            }
        }
    }
}
