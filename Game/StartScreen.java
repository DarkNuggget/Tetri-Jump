import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
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
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import java.util.prefs.Preferences;

public class StartScreen {
    private TetriGui app;
    private Scene scene;
    private Preferences prefs;

    public StartScreen(TetriGui app) {
        this.app = app;
        this.prefs = Preferences.userNodeForPackage(TetriGui.class);  // Zugriff auf Preferences für die App
        createUI();
    }

    private void createUI() {
        // VBox für vertikale Anordnung der Elemente
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);  // Zentriert die Elemente im Container

        // Hintergrundbild laden
        Image backgroundImage = new Image("file:Hintergrund.jpg");  // Der Pfad zum Bild
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, 
            BackgroundSize.DEFAULT);

        // Hintergrundbild auf die VBox anwenden
        root.setBackground(new Background(bgImage));

        // Titel-Text für das Spiel
        Text title = new Text("TetriJump");
        title.setFont(new Font(50));
        title.setFill(Color.WHITE);  // Textfarbe Weiß
        title.setStyle("-fx-font-weight: bold;");  // Fetter Text

        // Start-Button
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        startButton.setOnAction(event -> app.startGame());  // Spiel starten, wenn gedrückt

        // Exit-Button
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        exitButton.setOnAction(event -> System.exit(0));  // Beendet das Spiel, wenn gedrückt

        // Options-Button
        Button optionsButton = new Button("Options");
        optionsButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        optionsButton.setOnAction(event -> showOptionsWindow());  // Öffnet das Optionenfenster

        // Button in die VBox einfügen
        root.getChildren().addAll(title, startButton, optionsButton, exitButton);

        // Erstellen der Szene
        scene = new Scene(root, 596, 672);  // Größe der Szene
    }

    public Scene getScene() {
        return this.scene;
    }

    // Öffnet das Optionenfenster
    private void showOptionsWindow() {
        Stage optionsStage = new Stage();
        VBox optionsRoot = new VBox(20);
        optionsRoot.setAlignment(Pos.CENTER);

        // Lautstärkeregler
        Label volumeLabel = new Label("Volume");
        Slider volumeSlider = new Slider(0, 1, getSavedVolume());  // Lade die gespeicherte Lautstärke, Standardwert 0.5
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setMajorTickUnit(0.2);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        // Lautstärkeänderung speichern
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> saveVolume(newValue.doubleValue()));

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

    // Speichert die Lautstärke in den Preferences
    private void saveVolume(double volume) {
        prefs.putDouble("volume", volume);
    }

    // Lädt die gespeicherte Lautstärke aus den Preferences
    private double getSavedVolume() {
        return prefs.getDouble("volume", 0.5);  // Standardwert 0.5
    }
}
