import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.File;

public class DeathScreen {
    private TetriGui app = new TetriGui();
    private int score;
    private Stage primaryStage;
    private Scene previousScene;

    public final static MusikPlayer musikPlayer = new MusikPlayer();

    public DeathScreen(int score, Stage primaryStage, Scene previousScene) {
        this.score = score;
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
    }

    public void show() {
        // Erstelle ein Bild für den "Game Over"-Text
        ImageView deathScreenText = new ImageView(new Image(new File("Bilder/deathscreentext.png").toURI().toString()));
        deathScreenText.setFitWidth(300);
        deathScreenText.setFitHeight(150);
     
        ImageView BerndBild = new ImageView(new Image(new File("Bilder/BerndTot.png").toURI().toString()));
        BerndBild.setFitWidth(600);
        BerndBild.setFitHeight(500);
//        BerndBild.setX(300);

        // Button erstellen mit dem gleichen Design wie auf dem Startscreen
        Button restartButton = createStyledButton("Bilder/MainMenu.png", event -> {
            System.out.println("Returning to Main Menu...");
            app.showStartScreen();
            musikPlayer.stoppeAktuelleMusik();
            musikPlayer.startMenuMusik();
        });                                                                                         

        // Layout für den Death Screen
        StackPane overlay = new StackPane();

        // Füge das Bild und die Buttons zu diesem Overlay hinzu
        overlay.getChildren().addAll(deathScreenText,BerndBild,restartButton);

        // Bild und Button positionieren
        StackPane.setAlignment(deathScreenText, Pos.TOP_CENTER);
        StackPane.setAlignment(restartButton, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(BerndBild, Pos.BOTTOM_CENTER);

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
