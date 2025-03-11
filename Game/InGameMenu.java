import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;

public class InGameMenu {

    private TetriGui app = new TetriGui();
    private VBox modeRoot;
    public final static MusikPlayer musikPlayer = new MusikPlayer();

    public void loadMenu(Pane gameRoot, Stage primaryStage) {
        if (modeRoot == null) {
            modeRoot = new VBox(30);
            modeRoot.setAlignment(Pos.CENTER);
            modeRoot.setPrefSize(300, 400);
            modeRoot.setStyle(
                "-fx-background-color: rgba(20, 20, 20, 0.9);" + 
                "-fx-border-color: #00FFFF;" + // Cyanfarbener Rand
                "-fx-border-width: 2px;" + 
                "-fx-border-radius: 10px;" + 
                "-fx-background-radius: 10px;" + 
                "-fx-padding: 20px;"
            );

            // Titel mit Schatten
            Text menuTitle = new Text("In-Game Menu");
            menuTitle.setFont(Font.font("Arial", 36));
            menuTitle.setFill(Color.CYAN); // CYAN FARBE
            menuTitle.setEffect(new DropShadow(5, Color.WHITE));
            menuTitle.setStyle("-fx-font-weight: bold;");

            // Resume-Button
            Button resumeButton = new Button("Resume Game");
            styleButton(resumeButton);
            resumeButton.setOnAction(event -> {
                gameRoot.getChildren().remove(modeRoot);
                musikPlayer.startGameMusik();
            });

            // Main-Menu Button
            Button mainButton = new Button("Main Menu");
            styleButton(mainButton);
            mainButton.setOnAction(event -> {
                System.out.println("quit");
                app.showStartScreen();
                musikPlayer.stoppeAktuelleMusik();
            });

            // Exit-Button
            Button exitButton = new Button("Exit");
            styleButton(exitButton);
            exitButton.setOnAction(event -> System.exit(0));

            modeRoot.getChildren().addAll(menuTitle, resumeButton, mainButton, exitButton);
        }

        if (!gameRoot.getChildren().contains(modeRoot)) {
            modeRoot.setTranslateX(600);
            gameRoot.getChildren().add(modeRoot);

            musikPlayer.startMenuMusik();

            // Animation für das Menü
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), modeRoot);
            transition.setFromX(600);
            transition.setToX((primaryStage.getWidth() - modeRoot.getPrefWidth()) / 2); // Zentriert
            transition.setInterpolator(javafx.animation.Interpolator.EASE_OUT); // Sanfter Stopp
            transition.play();
        }
    }

    // Methode zum Stylen der Buttons
    private void styleButton(Button button) {
        button.setStyle(
            "-fx-font-size: 18px;" + 
            "-fx-font-family: 'Arial';" + 
            "-fx-text-fill: white;" + 
            "-fx-background-color: linear-gradient(to bottom, #00CED1, #008080);" + // Gradient
            "-fx-background-radius: 8px;" + 
            "-fx-padding: 10px 25px;" + 
            "-fx-border-color: #00FFFF;" + 
            "-fx-border-width: 1px;" + 
            "-fx-border-radius: 8px;" + 
            "-fx-effect: dropshadow(gaussian, rgba(0,255,255,0.5), 5, 0.5, 0, 1);"
        );

        // Hover-Effekt
        button.setOnMouseEntered(e -> button.setStyle(
            button.getStyle() + "-fx-background-color: linear-gradient(to bottom, #00FFFF, #00CED1);" + 
            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;" // Leichtes Vergrößern
        ));
        button.setOnMouseExited(e -> styleButton(button)); // Zurück zum ursprünglichen Stil
    }
}
