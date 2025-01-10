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
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class StartScreen {
    private TetriGui app;
    private Scene scene;

    public StartScreen(TetriGui app) {
        this.app = app;
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

        // Button in die VBox einfügen
        root.getChildren().addAll(title, startButton, exitButton);

        // Erstellen der Szene
        scene = new Scene(root, 596, 672);  // Größe der Szene
    }

    public Scene getScene() {
        return this.scene;
    }
}
