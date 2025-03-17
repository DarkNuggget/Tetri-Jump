import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.stage.Stage;

public class InGameMenu {

    private TetriGui app = new TetriGui();
    private VBox modeRoot;
    public final static MusikPlayer musikPlayer = new MusikPlayer();

    public void loadMenu(Pane gameRoot, Stage primaryStage) {
        if (modeRoot == null) {
            modeRoot = new VBox(30);
            modeRoot.setAlignment(Pos.CENTER);
            modeRoot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); -fx-padding: 20px; -fx-border-color: #00FFFF; -fx-border-width: 3px; -fx-border-radius: 10px;");

            Text menuTitle = new Text("TetriJump");
            menuTitle.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Tetris.ttf"), 40));
            menuTitle.setFill(Color.CYAN);

            // Resume-Button
            Button resumeButton = createStyledButton("Resume Game");
            resumeButton.setOnAction(event -> {
                gameRoot.getChildren().remove(modeRoot);
                musikPlayer.startGameMusik();
            });

            // Main-Menu Button
            Button mainButton = createStyledButton("Main Menu");
            mainButton.setOnAction(event -> {
                System.out.println("Returning to Main Menu...");
                app.showStartScreen();
                musikPlayer.stoppeAktuelleMusik();
            });

            // Exit-Button
            Button exitButton = createStyledButton("Exit");
            exitButton.setOnAction(event -> System.exit(0));

            modeRoot.getChildren().addAll(menuTitle, resumeButton, mainButton, exitButton);
        }

        if (!gameRoot.getChildren().contains(modeRoot)) {
            gameRoot.getChildren().add(modeRoot);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), modeRoot);
            transition.setFromX(600);
            transition.setToX(0);
            transition.play();
        }
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: #222222; -fx-border-color: #00FFFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 10px 20px; -fx-font-family: 'Press Start 2P';");
        
        // Hover Effekt
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: #00FFFF; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 10px 20px; -fx-font-family: 'Press Start 2P';"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: #222222; -fx-border-color: #00FFFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 10px 20px; -fx-font-family: 'Press Start 2P';"));

        return button;
    }
}
