package PegGamee;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PegGameGUI extends Application {

    // Instance variables for game components and state
    private SqrBoard gameBoard; 
    private Location selectedPegLocation = null; // Tracks the currently selected peg location in the game
    private GridPane gridPane = new GridPane(); // Layout for placing game pegs
    public Label gameStateLabel; // Label to display current game state
    public BorderPane bPane = new BorderPane(); // Main container for UI elements
    public Label TopLabel; // Top label to display the game's title
    private Button[][] pegButtons; // 2D array to hold references to buttons representing pegs
    private boolean GameOver = false; // Flag to check if the game is over
    private boolean isPaused = false; // Flag to check if the game is paused

    @Override
    public void start(Stage primaryStage) {
        /**
         * *Initializes and displays the primary stage with all UI components.
         *  @param primaryStage The primary stage for this application, provided by JavaFX.
         * */

        TopLabel = new Label("Let's play the PEG GAME");
        setLabel(TopLabel, 45 , Color.ALICEBLUE); 
        gameStateLabel = new Label();
        VBox Info= new VBox(5,TopLabel,gameStateLabel); 
        Info.setAlignment(Pos.TOP_CENTER);
        setLabel(gameStateLabel, 30, Color.LAVENDER); 
        Button loadButton = new Button("Load Game"); // Button to load a saved game
        Button saveButton = new Button("Save Game"); // Button to save the current game
        Button pauseButton = new Button("Pause/Resume"); // Button to toggle pause/resume
        Button quitButton = new Button("Quit Game"); // Button to quit the game

        // Event handlers for buttons
        loadButton.setOnAction(e -> loadGame(primaryStage));
        saveButton.setOnAction(e -> saveGame(primaryStage)); 
        pauseButton.setOnAction(e -> PauseOrResume());
        quitButton.setOnAction(e -> quitGame(primaryStage));

        HBox buttonBox = new HBox(20, loadButton, saveButton, pauseButton, quitButton); 
        buttonBox.setAlignment(Pos.CENTER);

        // Setting positions of components in the BorderPane
        bPane.setTop(Info);
        bPane.setCenter(gridPane);
        bPane.setBottom(buttonBox);
        bPane.setCenter(gridPane);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        GridPane.setHgrow(gridPane, Priority.ALWAYS);
        GridPane.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setAlignment(Pos.CENTER);

        // Background styling for the main panel
        BackgroundFill backgroundFill = new BackgroundFill(Color.INDIGO, CornerRadii.EMPTY, Insets.EMPTY);
        bPane.setBackground(new Background(backgroundFill));

        Scene scene = new Scene(bPane, 300, 300);
        primaryStage.setMaximized(true); // Maximizing the primary stage
        primaryStage.setTitle("Peg Game"); // Setting title of the window
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Displaying the stage
    }

    private void loadGame(Stage primaryStage) {
        /**
 * Loads a game from a file and updates the game board and UI.
 * @param primaryStage The primary stage of the application, used here for file dialogs.
 */

        try {
            boolean[][] boardArray = FileUtil.loadGameBoard(primaryStage);
            if (boardArray != null) {
                gameBoard = new SqrBoard(boardArray);
                pegButtons = new Button[boardArray.length][boardArray[0].length];
                displayGameBoard(); // Display the loaded game board
            }
        } catch (Exception ex) {
            System.err.println("Error loading the game board.");
            showError("Invalid file format selected");

        }
    }

    private void saveGame(Stage primaryStage) {
        /**
* Saves the current game state to a file using the file utility.
 * @param primaryStage The primary stage of the application, used to anchor file dialogs.
 */

        try {
            FileUtil.saveGameBoard(primaryStage, gameBoard.getGameBoard());
        } catch (Exception ex) {
            System.err.println("Error saving the game board.");
            showError("Invalid File");
        }
    }

    private void PauseOrResume() {
        /**
 *  Pauses  and resumes state of the game, disabling or enabling interaction with the game board.
 */
        isPaused = !isPaused; 
        for (Button[] row : pegButtons) {
            for (Button btn : row) {
                btn.setDisable(isPaused); 
            }
        }
        gameStateLabel.setText(isPaused ? "Game Paused" : "Game In Progress"); // Update label based on state
    }

    private void displayGameBoard() {
        /**
 * Creates and displays the game board using buttons to represent pegs.
 */

        gridPane.getChildren().clear();
        gridPane.setHgap(3);  
        gridPane.setVgap(3);  
        for (int i = 0; i < gameBoard.getGameBoard().length; i++) {
            for (int j = 0; j < gameBoard.getGameBoard()[i].length; j++) {
                Button pegButton = createPegButton(i, j);
                gridPane.add(pegButton, j, i);
                pegButtons[i][j] = pegButton;
            }
        }
    }

    private Button createPegButton(int row, int col) {
        /**
 * Creates a button for a peg at a specified position on the game board.
 * @param row The row index of the peg in the game board.
 * @param col The column index of the peg in the game board.
 * @return The created button with appropriate styling and event handling.
 */

        Button button = new Button();
        boolean isPeg = gameBoard.getGameBoard()[row][col];
        Circle circle = new Circle(25, isPeg ? Color.BLACK : Color.WHITE); // Set color based on peg presence
        button.setGraphic(circle);
        button.setPrefSize(40, 40);
        button.setOnAction(e -> handlePegButtonClick(row, col));
        if (isPaused) button.setDisable(true); // Disable if game is paused
        return button;
    }

    private void handlePegButtonClick(int row, int col) {
       /**
 * Handles button clicks on the game board, either selecting a peg or making a move.
 * @param row The row index of the peg that was clicked.
 * @param col The column index of the peg that was clicked.
 */

        if (isPaused || GameOver) return; // Ignore clicks if paused or game is over
        if (selectedPegLocation == null && gameBoard.getGameBoard()[row][col]) {
            highlightPeg(row, col);
            selectedPegLocation = new Location(row, col);
        } else if (selectedPegLocation != null) {
            try {
                gameBoard.makeMove(new Move(selectedPegLocation, new Location(row, col)));
                displayGameBoard();
                updateGameStateLabel();
            } catch (PegGameException e) {
                showError(e.getMessage());
            }
            deselectPeg();
        }
    }

    private void highlightPeg(int row, int col) {
        /**
 * Highlights a peg on the board to indicate it is selected.
 * @param row The row index of the peg to highlight.
 * @param col The column index of the peg to highlight.
 */

        
        Circle circle = new Circle(25, Color.YELLOW);
        pegButtons[row][col].setGraphic(circle);
    }

    private void deselectPeg() {
        /**
 * Deselects any currently selected peg on the board.
 */

        if (selectedPegLocation != null) {
            int row = selectedPegLocation.getRow();
            int col = selectedPegLocation.getCol();
            boolean isPeg = gameBoard.getGameBoard()[row][col];
            Circle circle = new Circle(25, isPeg ? Color.BLACK : Color.WHITE);
            pegButtons[row][col].setGraphic(circle);
            selectedPegLocation = null;
        }
    }

    private void updateGameStateLabel() {
        /**
 * Updates the label that displays the current state of the game (e.g., in progress, paused, won, etc.).
 */

        GameState gameState = gameBoard.getGameState();
        if (gameState == GameState.NOT_STARTED) {
            gameStateLabel.setText("Game Not Started");
        } else if (gameState == GameState.IN_PROGRESS) {
            gameStateLabel.setText("Game In Progress");
        } else if (gameState == GameState.STALEMATE || gameState == GameState.WON) {
            gameStateLabel.setText(gameState == GameState.WON ? "You Won!" : "Stalemate");
            GameOver = true;
            disableBoard();
            showFinalGameState(gameState);
        }
    }

    private void disableBoard() {
       /**
 * Disables all interactive elements on the game board, typically called when the game ends.
 */

        for (Button[] row : pegButtons) {
            for (Button button : row) {
                button.setDisable(true);
            }
        }
    }

    private void quitGame(Stage primaryStage) {
        /**
 * Handles the operation to quit the game, offering a save option before closing.
 * @param primaryStage The primary stage of the application, used for anchoring dialog windows.
 */

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Confirm Quit");

        // Message Label
        Label messageLabel = new Label("Do you want to save the game before quitting?");

        // Save Button
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            saveGame(primaryStage);
            dialogStage.close();
            primaryStage.close();
        });

        // Don't Save Button
        Button dontSaveButton = new Button("Don't Save");
        dontSaveButton.setOnAction(e -> {
            dialogStage.close();
            primaryStage.close();
        });

        // Layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(messageLabel, saveButton, dontSaveButton);
        layout.setAlignment(Pos.CENTER);

        // Scene and Stage
        Scene scene = new Scene(layout, 300, 150);
        dialogStage.setScene(scene);
        dialogStage.initOwner(primaryStage);
        dialogStage.show();
    }

private void showError(String errorMessage) {
      /**
 * Displays an error message in a new window.
 * @param errorMessage The error message to display.
 */
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null); 
    alert.setContentText(errorMessage);
    alert.initOwner(bPane.getScene().getWindow()); 
    alert.showAndWait(); // Show the alert and wait for it to be dismissed
}

private void showFinalGameState(GameState state) {
    /**
    * Displays the final state of the game (won or stalemate) in a new window, then offers an option to close the popup.
    * @param state The final state of the game, indicating whether the player won or the game ended in a stalemate.
    */

    Stage resultStage = new Stage();
    resultStage.setTitle("Game Over");
    String message = state == GameState.WON ? "Congratulations, You Won!" : "Stalemate";
    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> resultStage.close()); // Adds an event handler to close the popup

    VBox layout = new VBox(10, new Label(message), closeButton);
    layout.setAlignment(Pos.CENTER);
    Scene scene = new Scene(layout, 300, 100);
    resultStage.setScene(scene);
    resultStage.showAndWait();
}


    public static void main(String[] args) {
        launch(args);
    }

    private static Label setLabel(Label l, int size, Color clr){
        /**
 * Styles and configures a label with specified font size and color.
 * @param l The label to style.
 * @param size The font size for the label's text.
 * @param clr The color for the label's text.
 * @return The styled label.
 */
        Font f = new Font("Cooper Black", size);
        l.setFont(f);
        Insets i = new Insets(20);
        l.setPadding(i);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setAlignment(Pos.CENTER);
        l.setTextFill(clr);
        l.setMaxHeight(Double.POSITIVE_INFINITY);
        l.setMaxWidth(Double.POSITIVE_INFINITY);
        return l;
    }
}
