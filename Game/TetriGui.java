import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;   

public class TetriGui extends Application {
    private Stage primaryStage;
    private TetriJump tetriJump;
    private MusikPlayer musikPlayer;
    @Override
  
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.musikPlayer = new MusikPlayer();
        showStartScreen();
    }

    public void showStartScreen() {
       
        StartScreen startScreen = new StartScreen(this);
        Scene startScene = startScreen.getScene();
        primaryStage.setTitle("TetriGui - Start");
        primaryStage.setScene(startScene);   
        primaryStage.setResizable(false);
        primaryStage.show();  
           if (musikPlayer != null) {
            System.out.println("MusikPlayer ist vorhanden!");
            musikPlayer.stopMusik();
      } else {
            System.out.println("MusikPlayer ist null!");
        
      }

        
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
            case "JumpTetris":
                startJumpTetris();
                break;
            default:
                System.out.println("Unknown game mode: " + mode);
        }
    }
  
    public void startJumpTetris() {
      // Hallo tobi hir bin ich die metho wo du bitte das spiel mit dem bot machst top ;)
    }
  
    public void showStartScreenFromGame() {  
        showStartScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
