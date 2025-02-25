import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TetriGui extends Application {
    private Stage primaryStage;
    private TetriJump tetriJump;
    private TetriAutoGame tetriAutoGame;
    private StartScreen startScreen;
                            
    @Override
    public void start(Stage primaryStage) {     
        showStartScreen(primaryStage);
    }

    public void showStartScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        startScreen = new StartScreen(this);
        Scene startScene = startScreen.getScene();
        primaryStage.setTitle("TetriGui - Start");
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void startGame() {
        TetriJump jump = new TetriJump(primaryStage, this);
        Scene gameScene = jump.getGameScene();
        primaryStage.setTitle("TetriGui - Game");
        primaryStage.setScene(gameScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
    }
   
    public void startGameWithMode(String mode) {
        System.out.println("Starting game in mode: " + mode);
        // Implementiere die Logik für jeden Modus
        switch (mode) {
            case "Classic Tetris":
                startGame();
                break;
            case "Jump Tetris":
                startJumpTetris();
                break;
            default:
                System.out.println("Unknown game mode: " + mode);
        }
    }
  
    public void startJumpTetris() {
       System.out.println("AUTOGAME");
       TetriAutoGame tetriAutoGame = new TetriAutoGame(primaryStage, this);
       Scene gameScene = tetriAutoGame.getGameScene();
       primaryStage.setTitle("TetriAutoGame - ALPHA");
       primaryStage.setScene(gameScene);
       primaryStage.setResizable(false);
       primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);            
    }
}
