import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HimmelDeathScreen {
  private Stage primaryStage;
  private Scene previousScene;

  public HimmelDeathScreen(Stage primaryStage, Scene previousScene) {
    this.primaryStage = primaryStage;
    this.previousScene = previousScene;
  }

  public void show() {
    StackPane root = new StackPane();
    Text deathMessage = new Text("Game Over! Welcome to Himmer!");
    root.getChildren().add(deathMessage);

    Scene himmerDeathScene = new Scene(root, 600, 700);  // Dimensionen können angepasst werden
    primaryStage.setTitle("HimmerDeathScreen");
    primaryStage.setScene(himmerDeathScene);
    primaryStage.show();

    System.out.println("HimmerDeathScreen angezeigt.");
  }

  // Optional: Methode, um das vorherige Fenster wiederherzustellen
  public void showPreviousScreen() {
    primaryStage.setScene(previousScene);
    primaryStage.show();
  }
}
