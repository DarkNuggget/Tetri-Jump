import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TetriGui extends Application {
    private Stage primaryStage; // Das Hauptfenster
    private TetriJump tetriJump;
    private TetriAutoGame tetriAutoGame;
    private StartScreen startScreen;
    private TetriJumpShop tetriJumpShop; // Das Shop-Objekt

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showStartScreen();  // Zeigt den Startbildschirm an
    }

    // Methode um den Startbildschirm anzuzeigen
    public void showStartScreen() {
    if (primaryStage == null) {
        System.err.println("primaryStage ist null. Kann den Startbildschirm nicht anzeigen.");
        return;
    }

    if (startScreen == null) {
        startScreen = new StartScreen(this);  // Falls StartScreen noch nicht erstellt wurde
    }

    Scene startScene = startScreen.getScene();
    primaryStage.setTitle("TetriGui - Start");
    primaryStage.setScene(startScene);
    primaryStage.setResizable(false);
    primaryStage.show();
    System.out.println("Startbildschirm angezeigt.");
}


    // Methode um das Spiel zu starten
    public void startGame() {
        TetriJump jump = new TetriJump(primaryStage, this);
        Scene gameScene = jump.getGameScene();
        primaryStage.setTitle("TetriGui - Game");
        primaryStage.setScene(gameScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Methode um den Shop zu öffnen
   public void openShop() {
    tetriJumpShop = new TetriJumpShop();
    ShopScreen shopScreen = new ShopScreen(tetriJumpShop, this);  // Pass 'this' to ShopScreen constructor
    Scene shopScene = shopScreen.getScene();
    primaryStage.setScene(shopScene);
    primaryStage.show();
}


    // Methode, um das Spiel im Classic Tetris-Modus zu starten
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

    // Methode um den Jump Tetris-Modus zu starten
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
        launch(args);  // Starte die Anwendung
    }
}
