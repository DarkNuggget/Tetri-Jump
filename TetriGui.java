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
    
    obstacle = new Rectangle(20, 20, Color.GREEN);
    
    root.getChildren().addAll(player, obstacle);
    
    Scene scene = new Scene(root, 596, 679);
    // Anfang Komponenten
    scene.setOnKeyPressed(event -> handleKeyPress(event));
    
   
    
    // Ende Komponenten
    
    primaryStage.setOnCloseRequest(e -> System.exit(0));
    primaryStage.setTitle("TetriGui");
    primaryStage.setScene(scene);
    primaryStage.show();
  } // end of public TetriGui
  
  // Anfang Methoden
  
  public static void main(String[] args) {
    launch(args);
    
  } // end of main
  
  public void handleKeyPress(KeyEvent event) {
     double playerX = player.getLayoutX();
     double playerY = player.getLayoutY();
    switch (event.getCode()) {
      case SPACE:
        player.setLayoutY(playerY - 2); 
        break;
      case A: 
        player.setLayoutX(playerX - 2);
        break;
      case D:
        player.setLayoutX(playerX + 2);
        break;
      default: 
         System.out.println("Falsche Taste");
    } // end of switch
  }
  private void checkCollision() {
        if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
            System.out.println("Kollision erkannt!");
        }
    }

  // Ende Methoden
} // end of class TetriGui
