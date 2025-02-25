import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.Node;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartScreen {
    private TetriGui app;
    private Scene scene;
    private VBox mainMenuRoot; // Der Container für das Hauptmenü
    private boolean buttonsAdded = false; // Flag, um zu prüfen, ob die Buttons schon hinzugefügt wurden
    MusikPlayer musikPlayer;
    public StartScreen(TetriGui app) {
        this.app = app;
        createUI();
    }

    public void createUI() {
        // Verhindere mehrfaches Hinzufügen der Buttons
        if (buttonsAdded) {
            return;
        }

        InGameMenu.musikPlayer.startMenuMusik(); // Menü-Musik starten
        InGameMenu.musikPlayer.MenuOffen = true;
        mainMenuRoot = new VBox(20);
        mainMenuRoot.setAlignment(Pos.CENTER);

        // Hintergrundbild laden
        File backgroundFile = new File("Hintergrund/MainMenuHintergrund.jpg");
        String bgUri = backgroundFile.toURI().toString();
        BackgroundImage bgImage = new BackgroundImage(new Image(bgUri),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        mainMenuRoot.setBackground(new Background(bgImage));

        // Titel-Text für das Spiel
        Text title = new Text("TetriJump");
        title.setFont(new Font(50));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");

        // Start-Button
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        startButton.setOnAction(event -> showModeSelection());

        // Optionen-Button
        Button optionsButton = new Button("Options");
        optionsButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        optionsButton.setOnAction(event -> showVolumeSettings());

        // Exit-Button
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        exitButton.setOnAction(event -> System.exit(0));
    

        // Buttons zur VBox hinzufügen
        mainMenuRoot.getChildren().addAll(title, startButton, optionsButton, exitButton);

        // Szene erstellen
        scene = new Scene(mainMenuRoot, 596, 672);

        // Setze das Flag, dass die Buttons jetzt hinzugefügt wurden
        buttonsAdded = true;
    }

    private void showModeSelection() {
        // Panel für die Spielmodi-Auswahl im bestehenden Menü-Fenster
        VBox modeRoot = new VBox(20);
        modeRoot.setAlignment(Pos.CENTER);
        modeRoot.setStyle("-fx-background-color: white; -fx-padding: 10px;");

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
                InGameMenu.musikPlayer.MenuOffen = false;
                String mode = selectedMode.getText();
                System.out.println("Starting game in mode: " + mode);
                InGameMenu.musikPlayer.startGameMusik();
                app.startGameWithMode(mode); // Spiel im ausgewählten Modus starten
            }
        });
    
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> {
            // Panel ausblenden
            VBox currentPanel = (VBox) closeButton.getParent();
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), currentPanel);
            transition.setFromX(0);
            transition.setToX(600);
            transition.play();

            // Wenn die Animation abgeschlossen ist, zurück zum Hauptmenü
            transition.setOnFinished(e -> {
                // Zeige die ursprünglichen Buttons wieder an, sobald das Panel geschlossen wurde
                mainMenuRoot.getChildren().remove(currentPanel);
                addMainMenuButtons(); // Füge die ursprünglichen Buttons wieder hinzu
            });
        });
    
        modeRoot.getChildren().addAll(modeTitle, classicTetris, jumpTetris, startModeButton, closeButton);

        // Das Panel in das Hauptmenü einfügen
        mainMenuRoot.getChildren().add(modeRoot);

        // Panel-Einblendung animieren
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), modeRoot);
        transition.setFromX(600);
        transition.setToX(0);
        transition.play();
    }

    public void showVolumeSettings() {
        // Panel für Lautstärke-Einstellungen im bestehenden Menü-Fenster
        VBox volumeRoot = new VBox(20);
        volumeRoot.setAlignment(Pos.CENTER);
        volumeRoot.setStyle("-fx-background-color: white; -fx-padding: 10px;");

        // Lautstärke Label und Slider
        Label volumeLabel = new Label("Adjust Volume:");
        Slider volumeSlider = new Slider(0, 100, LautstaerkeEinstellungen.loadVolumeSetting() * 100);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double volume = newValue.doubleValue() / 100;
            LautstaerkeEinstellungen.saveaVolumeSetting(volume);
            InGameMenu.musikPlayer.setAktuelleLautstaerke(volume);
        });

        // Button zum Schließen des Panels
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> {
            // Panel ausblenden
            VBox currentPanel = (VBox) closeButton.getParent();
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), currentPanel);
            transition.setFromX(0);
            transition.setToX(600);
            transition.play();

            // Wenn die Animation abgeschlossen ist, zurück zum Hauptmenü
            transition.setOnFinished(e -> {
                // Zeige die ursprünglichen Buttons wieder an, sobald das Panel geschlossen wurde
                mainMenuRoot.getChildren().remove(currentPanel);
                addMainMenuButtons(); // Füge die ursprünglichen Buttons wieder hinzu
            });
        });

        volumeRoot.getChildren().addAll(volumeLabel, volumeSlider, closeButton);

        // Das Panel in das Hauptmenü einfügen
        mainMenuRoot.getChildren().add(volumeRoot);

        // Panel-Einblendung animieren
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), volumeRoot);
        transition.setFromX(600);
        transition.setToX(0);
        transition.play();
    }

    // Hilfsmethode, um die Buttons wieder hinzuzufügen
    private void addMainMenuButtons() {
        // Füge die Buttons nur hinzu, wenn sie noch nicht vorhanden sind
        if (!buttonsAdded) {
            createUI(); // Stelle die ursprünglichen Buttons wieder her
        }
    }

    public Scene getScene() {
        return this.scene;
    }
}
