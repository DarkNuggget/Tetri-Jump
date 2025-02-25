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

public class InGameMenu {

    private TetriGui app = new TetriGui();
    private VBox modeRoot;
    public final static MusikPlayer musikPlayer = new MusikPlayer();
    

    public void loadMenu(Pane gameRoot, Stage primaryStage) {
        if (modeRoot == null) {
            modeRoot = new VBox(30);
            modeRoot.setAlignment(Pos.CENTER);
            modeRoot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

            Text menuTitle = new Text("In-Game Menu");
            menuTitle.setFont(new Font(30));
            menuTitle.setFill(Color.WHITE);

            // Resume-Button
            Button resumeButton = new Button("Resume Game");
            resumeButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
            resumeButton.setOnAction(event -> {
                gameRoot.getChildren().remove(modeRoot);
                
            });
      
            Button optionsButton = new Button("Options");
            optionsButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
            optionsButton.setOnAction(event -> musikPlayer.showOptionsWindow());
      
            // Main-Menu Button
            Button mainButton = new Button("Main Menu");
            mainButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
            mainButton.setOnAction(event -> {
                System.out.println("quit");
                app.showStartScreen(primaryStage);
                musikPlayer.stoppeAktuelleMusik();
            });

            Button exitButton = new Button("Exit");
            exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
            exitButton.setOnAction(event -> System.exit(0));

            modeRoot.getChildren().addAll(menuTitle, resumeButton, mainButton, optionsButton, exitButton);
        }

        if (!gameRoot.getChildren().contains(modeRoot)) {
            gameRoot.getChildren().add(modeRoot);
           
           // musikPlayer.startMenuMusik();

            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), modeRoot);
            transition.setFromX(600);
            transition.setToX(0);
            transition.play();
        }
    }
}
