import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BackgroundRepeat;

public class DeathScreen {
    private TetriGui app = new TetriGui();
    private int score;
    public final static MusikPlayer musikPlayer = new MusikPlayer();

    public DeathScreen(int score) {
        this.score = score;
    }

    public void show(Stage primaryStage) {
        // Erstelle den Text für das "Game Over"
        Text deathMessage = new Text("Du bist gestorben! Score: " + score);
        deathMessage.setStyle(
                "-fx-font-size: 30px; " +
                "-fx-fill: red; " +
                "-fx-font-family: Arial; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 0, 0, 0.7), 10, 0.5, 0, 0);");

        // Erstelle den Button, um ein neues Spiel zu starten
        Button restartButton = new Button("Main Menu");
        restartButton.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-background-color: #ff4747; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-border-radius: 10px; " +
                "-fx-cursor: hand;");

        restartButton.setOnAction(event -> {
                System.out.println("Returning to Main Menu...");
                app.showStartScreen();
                musikPlayer.stoppeAktuelleMusik();
            });

        // Füge das Bild als Hintergrund hinzu
        Image image = new Image("Hintergrund/DeathScreen.jpg");  // Pfad zum Bild
        BackgroundImage backgroundImage = new BackgroundImage(image, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundPosition.CENTER, 
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, false));
        StackPane layout = new StackPane();
        layout.setBackground(new Background(backgroundImage));

        // Layout für den Death Screen
        layout.getChildren().addAll(deathMessage, restartButton);

        // Text und Button positionieren
        StackPane.setAlignment(deathMessage, javafx.geometry.Pos.TOP_CENTER);
        StackPane.setAlignment(restartButton, javafx.geometry.Pos.BOTTOM_CENTER);

        // Szene und Stage
        Scene scene = new Scene(layout, 600, 700);
        

        // Stage konfigurieren
        primaryStage.setTitle("Death Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Funktion um das Spiel neu zu starten
}
