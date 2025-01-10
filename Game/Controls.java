import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class Controls {
    private Rectangle player;
    private Rectangle obstacle;
    private Scene gameScene;
    private Stage primaryStage;
    private TetriGui app;  // Reference to TetriGui
    private boolean isGameActive = true;

    public static final int FPS = 30;
    public static final int FRAME_TIME = 1000 / FPS;

    public Controls(Stage primaryStage, TetriGui app) {
        this.primaryStage = primaryStage;
        this.app = app;
        createGame(primaryStage);
    }

    private void createGame(Stage primaryStage) {
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        player = new Rectangle(50, 50, Color.BLUE);
        player.setX(100);
        player.setY(100);

        obstacle = new Rectangle(1000, 20, Color.WHITE);
        obstacle.setX(0);
        obstacle.setY(600);

        root.getChildren().addAll(player, obstacle);

        gameScene = new Scene(root, 596, 672);
        gameScene.setOnKeyPressed(event -> handleKeyPress(event));

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(FRAME_TIME), e -> checkCollision()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleKeyPress(KeyEvent event) {
        double playerX = player.getX();
        double playerY = player.getY();

        switch (event.getCode()) {
            case SPACE:
                player.setY(playerY - 2);
                break;
            case A:
                player.setX(playerX - 2);
                break;
            case D:
                player.setX(playerX + 2);
                break;
            case S:
                player.setY(playerY + 2);
                break;
            case M:
                if (isGameActive) {
                    pauseGame();
                    app.showStartScreenFromGame();  // Show the start screen when ESC is pressed
                }
                break;
            default:
                System.out.println("Falsche Taste");
        }
    }

    private void checkCollision() {
        if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
            player.setY(obstacle.getY() - obstacle.getHeight() * 3 + 12);
        }
    }

    private void pauseGame() {
        isGameActive = false;
        // Here you could stop animations or game loops if necessary
    }

    public Scene getGameScene() {
        return this.gameScene;
    }
}
