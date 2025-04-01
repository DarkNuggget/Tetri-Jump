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
import javafx.scene.image.Image;       // Import für Image
import javafx.scene.image.ImageView;  // Import für ImageView
import java.io.File;
public class InGameMenu {
  
  private TetriGui app = new TetriGui();
  private VBox modeRoot;
  public final static MusikPlayer musikPlayer = new MusikPlayer();
  
 public void loadMenu(Pane gameRoot, Stage primaryStage) {
    if (modeRoot == null) {
        modeRoot = new VBox(40);  // Erhöhe den Abstand zwischen den Buttons (z.B. 40 statt 30)
        modeRoot.setAlignment(Pos.CENTER);
        
        // Setze eine größere Mindestgröße für das Menü
        modeRoot.setMinWidth(600);  // Mindestbreite des Menüs
        modeRoot.setMinHeight(300); // Mindesthöhe des Menüs
        
       modeRoot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-padding: 40px; -fx-background-radius: 10px;");
       
        // Resume-Button
        Button resumeButton = createStyledButton("Bilder/Resume.png", event -> {
            gameRoot.getChildren().remove(modeRoot);

        });

        // Main-Menu Button
        Button mainButton = createStyledButton("Bilder/MainMenu.png", event -> {
            System.out.println("Returning to Main Menu...");
            app.showStartScreen();
            musikPlayer.stoppeAktuelleMusik();
        });

        // Exit-Button
        Button exitButton = createStyledButton("Bilder/Exit.png", event -> System.exit(0));

        modeRoot.getChildren().addAll(resumeButton, mainButton, exitButton);
    }

    // Verhindert, dass TranslateTransition jedes Mal ausgeführt wird, wenn das Menü wieder geladen wird
    if (!gameRoot.getChildren().contains(modeRoot)) {
        gameRoot.getChildren().add(modeRoot);

        // Position des Menüs festlegen
        modeRoot.setLayoutX(0);
        modeRoot.setLayoutY(150);

        // TranslateTransition für das Menü (nur einmal beim Laden)
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), modeRoot);
        transition.setFromX(600);
        transition.setToX(0);
        transition.play();
    }
}

private Button createStyledButton(String imagePath, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
    Button button = new Button();
    // Füge das Bild dem Button hinzu
    ImageView icon = new ImageView(new Image(new File(imagePath).toURI().toString()));
    icon.setFitWidth(140); // Standardbreite der Icons
    icon.setFitHeight(40); // Standardhöhe der Icons
    button.setGraphic(icon);
    button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"); // Transparenten Hintergrund
    button.setMinWidth(110); // Minimale Breite der Buttons
    button.setMinHeight(60); // Minimale Höhe der Buttons
    button.setOnAction(action); // Action für den Button hinzufügen

    // Hover-Effekt hinzufügen, der nur den Button betrifft
    button.setOnMouseEntered(event -> {
        icon.setFitWidth(160); // Vergrößern beim Hover
        icon.setFitHeight(45); // Vergrößern beim Hover
    });

    button.setOnMouseExited(event -> {
        icon.setFitWidth(140); // Zurück zur ursprünglichen Größe
        icon.setFitHeight(40); // Zurück zur ursprünglichen Größe
    });

    return button;
}


  
}
