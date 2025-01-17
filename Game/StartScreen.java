import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.File;
import javafx.util.Duration;

public class StartScreen {
    private TetriGui app;
    private Scene scene;
    private MusikPlayer musikPlayer;

    public StartScreen(TetriGui app) {
        this.app = app;
        this.musikPlayer = new MusikPlayer(); // MusikPlayer instanziieren
        createUI();
    }

    public void createUI() {    
        musikPlayer.startMenuMusik();
   
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // Hintergrundbild laden
        File backgroundFile = new File("Hintergrund/MainMenuHintergrund.jpg");
        String bgUri = backgroundFile.toURI().toString();
        BackgroundImage bgImage = new BackgroundImage(new javafx.scene.image.Image(bgUri),
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

        // Exit-Button        z
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        exitButton.setOnAction(event -> System.exit(0));

        // Options-Button
        Button optionsButton = new Button("Options");
        optionsButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        optionsButton.setOnAction(event -> musikPlayer.showOptionsWindow());

        // Buttons zur VBox hinzufügen
        root.getChildren().addAll(title, startButton, optionsButton, exitButton);

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
        
                app.startGameWithMode(mode); // Spiel im ausgewählten Modus starten
            }
            musikPlayer.stopMusik();
            musikPlayer.startGameMusik();
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
