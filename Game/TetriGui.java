import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TetriGui extends Application {
    private Stage primaryStage;
    private Controls controls;
    private TetriJump jump;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showStartScreen();
    }

    private void showStartScreen() {
        StartScreen startScreen = new StartScreen(this);
        Scene startScene = startScreen.getScene();
        primaryStage.setTitle("TetriGui - Start");
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void startGame() {
        jump = new TetriJump(primaryStage, this);
        
        Scene gameScene = jump.getGameScene();
        primaryStage.setTitle("TetriGui - Game");
        primaryStage.setScene(gameScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void showStartScreenFromGame() {
        showStartScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
