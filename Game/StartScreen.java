import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class StartScreen {
    private TetriGui app;
    private Scene scene;
    private MediaPlayer mediaPlayer;

    public StartScreen(TetriGui app) {
        this.app = app;
        createUI();
    }

    private void createUI() {
        // VBox für vertikale Anordnung der Elemente
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // Hintergrundbild laden
        File backgroundFile = new File("Hintergrund.jpg");
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
        title.setFill(javafx.scene.paint.Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");

        // Start-Button
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        startButton.setOnAction(event -> app.startGame());

        // Exit-Button
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        exitButton.setOnAction(event -> System.exit(0));

        // Options-Button
        Button optionsButton = new Button("Options");
        optionsButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        optionsButton.setOnAction(event -> showOptionsWindow());

        // Button in die VBox einfügen
        root.getChildren().addAll(title, startButton, optionsButton, exitButton);

        // Erstellen der Szene
        scene = new Scene(root, 596, 672);
        startMusic();
    }

    public Scene getScene() {
        return this.scene;
    }

    // Musik abspielen
    private void startMusic() {
        // Den Pfad zur Musikdatei korrekt angeben
        File file = new File("Musik1.mp3");
        String uriString = file.toURI().toString(); // Den URI aus dem Dateipfad generieren
        Media media = new Media(uriString); // Media-Objekt erstellen
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Die Musik in einer Schleife abspielen
        mediaPlayer.play(); // Musik abspielen
    }

    // Optionen anzeigen (z.B. Lautstärkeregelung)
    private void showOptionsWindow() {
        Stage optionsStage = new Stage();
        VBox optionsRoot = new VBox(20);
        optionsRoot.setAlignment(Pos.CENTER);

        // Lautstärkeregler
        javafx.scene.control.Label volumeLabel = new javafx.scene.control.Label("Volume");
        javafx.scene.control.Slider volumeSlider = new javafx.scene.control.Slider(0, 1, 0.5);  // Standardwert 0.5
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setMajorTickUnit(0.2);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        // Lautstärkeänderung speichern
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue());  // Musiklautstärke ändern
        });

        // Schließen-Button
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        closeButton.setOnAction(event -> optionsStage.close());  // Schließt das Fenster

        optionsRoot.getChildren().addAll(volumeLabel, volumeSlider, closeButton);

        Scene optionsScene = new Scene(optionsRoot, 400, 300);
        optionsStage.setTitle("Options");
        optionsStage.setScene(optionsScene);
        optionsStage.show();
    }
}
