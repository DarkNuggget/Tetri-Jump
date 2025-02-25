import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.util.Duration;
import java.io.File;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class StartScreen {
    private TetriGui app;
    private Scene scene;

    public StartScreen(TetriGui app) {
        this.app = app;
        createUI();
    }

    public void createUI() {
        InGameMenu.musikPlayer.startMenuMusik(); // Menü-Musik starten

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // Hintergrundbild laden
        File backgroundFile = new File("Hintergrund/MainMenuHintergrund.jpg");
        String bgUri = backgroundFile.toURI().toString();
        BackgroundImage bgImage = new BackgroundImage(new Image(bgUri),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(bgImage));

        // Titel-Text für das Spiel
        Text title = new Text("TetriJump");
        title.setFont(new Font(50));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");

        // Start-Button
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        startButton.setOnAction(event -> showModeSelection());

        // Exit-Button
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        exitButton.setOnAction(event -> System.exit(0));

        // Buttons zur VBox hinzufügen
        root.getChildren().addAll(title, startButton, exitButton);

        // Szene erstellen
        scene = new Scene(root, 596, 672);
    }

    private void showModeSelection() {
         // Neues Fenster für die Spielmodi-Auswahl
        Stage modeStage = new Stage();
        modeStage.setTitle("Choose Game Mode");

        VBox modeRoot = new VBox(20);
        modeRoot.setAlignment(Pos.CENTER);

        Text modeTitle = new Text("Select a Game Mode");
        modeTitle.setFont(new Font(30));
        modeTitle.setFill(Color.BLACK);

        // RadioButtons für die Modi
        ToggleGroup modeGroup = new ToggleGroup();

        RadioButton classicTetris = new RadioButton("Classic Tetris");
        classicTetris.setToggleGroup(modeGroup);
        classicTetris.setSelected(true);

        RadioButton jumpTetris = new RadioButton("Jump Tetris");
        jumpTetris.setToggleGroup(modeGroup);

        // Start-Button für den ausgewählten Modus
        Button startModeButton = new Button("Start");
        startModeButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        startModeButton.setOnAction(event -> {
            RadioButton selectedMode = (RadioButton) modeGroup.getSelectedToggle();
            if (selectedMode != null) {
                String mode = selectedMode.getText();
                System.out.println("Starting game in mode: " + mode);
                modeStage.close();
                InGameMenu.musikPlayer.startGameMusik();
                app.startGameWithMode(mode); // Spiel im ausgewählten Modus starten
            }
        });

        modeRoot.getChildren().addAll(modeTitle, classicTetris, jumpTetris, startModeButton);

        Scene modeScene = new Scene(modeRoot, 400, 300);
        modeStage.setScene(modeScene);
        modeStage.show();
    }

    public Scene getScene() {
        return this.scene;
    }
}
