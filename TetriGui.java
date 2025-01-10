import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TetriGui extends Application {
    private Stage primaryStage;
    private Controls controls;

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
        primaryStage.setResizable(false); // Verhindert, dass das Fenster vergrößert oder verkleinert wird
        primaryStage.show();
    }

    public void startGame() {
        controls = new Controls(primaryStage, this);  // Pass TetriGui instance to Controls
        Scene gameScene = controls.getGameScene();
        primaryStage.setTitle("TetriGui - Game");
        primaryStage.setScene(gameScene);
        primaryStage.setResizable(false); // Verhindert, dass das Fenster vergrößert oder verkleinert wird
        primaryStage.show();
    }

    public void showStartScreenFromGame() {
        showStartScreen();  // Method to show start screen when returning from game
    }

    public static void main(String[] args) {
        launch(args);
    }
}
