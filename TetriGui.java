import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TetriGui extends Application {
    // Anfang Attribute
    private Rectangle player;
    private Rectangle obstacle;
    // Ende Attribute

    public static final int FPS = 30;
    public static final int FRAME_TIME = 1000 / FPS;

    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        player = new Rectangle(50, 50, Color.BLUE);
        player.setX(100);
        player.setY(100);

        obstacle = new Rectangle(1000, 20, Color.WHITE);
        obstacle.setX(0); // Position des Hindernissesssssssss
        obstacle.setY(600); // Position des Hindernisses

        // Anfang Komponenten
        root.getChildren().addAll(player, obstacle);

        Scene scene = new Scene(root, 596, 679);
        scene.setOnKeyPressed(event -> handleKeyPress(event));

        // Animation oder Timer zur regelmäßigen Kollisionsüberprüfung
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(FRAME_TIME), e -> checkCollision()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    
        primaryStage.setResizable(false); 
        primaryStage.setTitle("Kollision Beispiel");
        primaryStage.setScene(scene);
        primaryStage.show();
    } // end of public TetriGui

    // Anfang Methoden
    public void handleKeyPress(KeyEvent event) {
        double playerX = player.getX(); // Verwenden von getX() und getY()
        double playerY = player.getY();

        switch (event.getCode()) {
            case SPACE:
                player.setY(playerY - 2); // Verwende setY statt setLayoutY
                break;
            case A:
                player.setX(playerX - 2); // Verwende setX statt setLayoutX
                break;
            case D:
                player.setX(playerX + 2);
                break;
            case S:
                player.setY(playerY + 2);
                break;
            default:
                System.out.println("Falsche Taste");
        } // end of switch
    }

    private void checkCollision() {
      double playerX = player.getX(); // Verwenden von getX() und getY()
      double playerY = player.getY();
        if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
           player.setY(playerY = obstacle.getY() - obstacle.getHeight() * 3 + 12);   
        }
    }

    public static void main(String[] args) {
        launch(args);
    } // end of main
} // end of class TetriGui
