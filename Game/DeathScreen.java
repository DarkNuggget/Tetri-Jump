import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import java.io.File;

public class DeathScreen {
    private TetriGui app = new TetriGui();
    private int score;
    private Stage primaryStage;
    private Scene previousScene;

    

    public DeathScreen(int score, Stage primaryStage, Scene previousScene) {
        this.score = score;
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
    }

    public void show() {
        InGameMenu.musikPlayer.stoppeAktuelleMusik();
        InGameMenu.musikPlayer.startDeathScreenMusik();
        // Erstelle ein Bild für den "Game Over"-Text
        ImageView deathScreenText = new ImageView(new Image(new File("Bilder/deathscreentext.png").toURI().toString()));
        deathScreenText.setFitWidth(300);
        deathScreenText.setFitHeight(150);
     
        ImageView BerndBild = new ImageView(new Image(new File("Bilder/BerndTot.png").toURI().toString()));
        BerndBild.setFitWidth(600);
        BerndBild.setFitHeight(500);

        // Erstelle das Score-Symbol
        ImageView scoreIcon = new ImageView(new Image(new File("Bilder/scoreicon.png").toURI().toString())); // Beispiel: Bild für das Score-Symbol
        scoreIcon.setFitWidth(50);
        scoreIcon.setFitHeight(50);

        // Erstelle den Text für den Score (mit weißer Farbe)
        Text scoreText = new Text("Score: " + score);
        scoreText.setFont(new Font(30)); // Schriftgröße anpassen
        scoreText.setFill(Color.WHITE); // Textfarbe auf Weiß setzen

        // Button erstellen mit dem gleichen Design wie auf dem Startscreen
        Button restartButton = createStyledButton("Bilder/MainMenu.png", event -> {
            System.out.println("Returning to Main Menu...");
            app.showStartScreen();
        });                                                                                         

        // Layout für den Death Screen
        StackPane overlay = new StackPane();

        // Füge das Bild, das Score-Symbol, den Score-Text und die Buttons zu diesem Overlay hinzu
        overlay.getChildren().addAll(deathScreenText, BerndBild, scoreIcon, scoreText, restartButton);

        // Bild und Button positionieren
        StackPane.setAlignment(deathScreenText, Pos.TOP_CENTER);
        StackPane.setAlignment(restartButton, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(BerndBild, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(scoreIcon, Pos.TOP_CENTER);
        StackPane.setAlignment(scoreText, Pos.CENTER);

        // Das Score-Symbol und der Text werden unterhalb des Bernd-Bildes angezeigt
        StackPane.setMargin(scoreIcon, new javafx.geometry.Insets(20, 0, 50, 0)); // Abstand zum Bernd-Bild
        StackPane.setMargin(scoreText, new javafx.geometry.Insets(20, 0, 100, 0)); // Abstand zum Score-Symbol

        // Spielfeld bleibt im Hintergrund sichtbar
        StackPane layout = new StackPane();
        layout.getChildren().addAll(previousScene.getRoot(), overlay);

        // Erstelle die Szene
        Scene deathScene = new Scene(layout, 600, 700);

        // Stage konfigurieren
        primaryStage.setTitle("Death Screen");
        primaryStage.setScene(deathScene);
        primaryStage.show();
    }

    private Button createStyledButton(String imagePath, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button();
        ImageView icon = new ImageView(new Image(new File(imagePath).toURI().toString()));
        icon.setFitWidth(140);
        icon.setFitHeight(40);
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"); 
        button.setMinWidth(110);
        button.setMinHeight(60);
        button.setOnAction(action);

        // Hover-Effekt hinzufügen
        button.setOnMouseEntered(event -> {
            icon.setFitWidth(160);
            icon.setFitHeight(45);
        });

        button.setOnMouseExited(event -> {
            icon.setFitWidth(150);
            icon.setFitHeight(40);
        });

        return button;
    }
}