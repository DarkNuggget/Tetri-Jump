import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.effect.Glow;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.scene.effect.GaussianBlur;

public class TetriJump {
  
  private Scene gameScene;
  private Stage primaryStage;
  private TetriGui app;
  private static final int TILE_SIZE = 30;
  
  public static int WIDTH = 20;
  public static int HEIGHT = 22;
  private Color[][] grid = new Color[HEIGHT][WIDTH];
  private Timeline gameLoop;
  private Tetromino currentTetromino;
  private InGameMenu menu = new InGameMenu();
  
  private StartScreen startScreen;
  private int score = 0;
  private int comboMultiplier = 1; // Für zusätzliche Belohnung bei Ketten
  private Text scoreText;
  private Text comboText; // Zeigt den Combo-Multiplikator
  private Timeline scoreIncrementer;
  private boolean showGrid;  
  
  public TetriJump(Stage primaryStage, TetriGui app) {
    this.primaryStage = primaryStage;
    this.app = app;
    createGame(primaryStage, 20, 22);
  }
  
  public void createGame(Stage primaryStage, int width, int height) {
    Pane root = new Pane();
    root.setStyle("-fx-background-color: #1a1a1a;"); // Dunkler, professioneller Hintergrund
    
    File backgroundFile = new File("Hintergrund/MainMenuHintergrund.jpg");  // Pfad zu deinem Bild
    String bgUri = backgroundFile.toURI().toString();
    ImageView backgroundImageView = new ImageView(new Image(bgUri, 600, 700, false, true));
    root.getChildren().add(backgroundImageView);  // Füge das Bild als Hintergrund hinzu
    
    // Canvas für das Spielfeld
    Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
    root.getChildren().add(canvas);
    
    // Score-Text
    scoreText = new Text("Score: 0");
    scoreText.setFill(Color.YELLOW); // Auffällige Farbe
    scoreText.setFont(Font.font("Arial", 24)); // Größere, professionellere Schrift
    scoreText.setX(WIDTH * TILE_SIZE - 120);
    scoreText.setY(30);
    scoreText.setEffect(new Glow(0.8)); // Glow für Professionalität
    root.getChildren().add(scoreText);
    
    // Combo-Text
    comboText = new Text("Combo: x1");
    comboText.setFill(Color.ORANGE);
    comboText.setFont(Font.font("Arial", 20));
    comboText.setX(WIDTH * TILE_SIZE - 120);
    comboText.setY(60);
    root.getChildren().add(comboText);
    
    gameScene = new Scene(root, width * TILE_SIZE, height * TILE_SIZE);
    gameScene.setOnKeyPressed(event -> handleKeyPress(event));
    
    // Score-Inkrementer mit Variation
    scoreIncrementer = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> incrementScore()));
    scoreIncrementer.setCycleCount(Timeline.INDEFINITE);
    scoreIncrementer.play();
    
    startGame(canvas.getGraphicsContext2D());
  }
  
  private void startGame(GraphicsContext gc) {
    currentTetromino = Tetromino.createRandomTetromino(WIDTH / 2, 0);
    
    gameLoop = new Timeline(new KeyFrame(Duration.millis(300), e -> {
      updateGame();
      render(gc);
    }));
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.play();
  }
  
  private void updateGame() {
    if (canMove(currentTetromino, 0, 1)) {
      currentTetromino.moveDown(grid); // Grid übergeben
    } else {
      fixTetromino(currentTetromino);
      int clearedRows = clearFullRows();
      if (clearedRows > 0) {
        comboMultiplier = Math.min(comboMultiplier + clearedRows, 5);
      } else {
        comboMultiplier = 1;
      }
      updateComboDisplay();
      
      // Neues Tetromino erstellen und prüfen, ob es spawnen kann
      currentTetromino = Tetromino.createRandomTetromino(WIDTH / 2, 0);
      if (!canMove(currentTetromino, 0, 0)) { // Prüfe Spawn-Position
        endGame(primaryStage); // Spiel stoppen und Bildschirm zeigen
      }
    }
  }
  
  private void endGame(Stage primaryStage) {
    gameLoop.stop(); // Stoppe den Game Loop
    scoreIncrementer.stop(); // Stoppe den Score-Inkrementer, damit der Score nicht weiter geht
    
    // Zeige den Death Screen an
    Scene previousScene = primaryStage.getScene(); // Annahme, dass die aktuelle Szene des Spiels geladen ist
    
    // Wende den Unschärfe-Effekt auf das Root-Element der aktuellen Szene an
    if (previousScene != null) {
      GaussianBlur blur = new GaussianBlur();
      blur.setRadius(10); // Stärke der Unschärfe anpassen
      previousScene.getRoot().setEffect(blur); // Wende den Effekt auf das Root-Element der Szene an
    }
    
    // Erstelle und zeige den DeathScreen
    DeathScreen deathScreen = new DeathScreen(score, primaryStage, previousScene);
    deathScreen.show();
  }
  
  
  
  private void render(GraphicsContext gc) {
    gc.clearRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
    
    // Render Grid mit leichter Transparenz für Stil
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        if (grid[y][x] != null) {
          gc.setFill(grid[y][x].deriveColor(0, 1, 1, 0.9)); // Leichte Transparenz
          gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
          gc.setStroke(Color.GRAY); // Gitterlinien für besseren Look
          gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
      }
    }
    
    gc.setStroke(Color.rgb(169, 169, 169, 0.5)); // Leicht graue Farbe für das Gitter
    gc.setLineWidth(0.5); // Dünne Linien für das Gitter
    for (int x = 0; x < WIDTH; x++) {
      gc.strokeLine(x * TILE_SIZE, 0, x * TILE_SIZE, HEIGHT * TILE_SIZE); // Vertikale Linien
    }
    for (int y = 0; y < HEIGHT; y++) {
      gc.strokeLine(0, y * TILE_SIZE, WIDTH * TILE_SIZE, y * TILE_SIZE); // Horizontale Linien
    }
    
    // Render Tetromino
    currentTetromino.render(gc, TILE_SIZE);
  }
  
  
  public boolean canMove(Tetromino tetromino, int dx, int dy) {
    return tetromino.canMove(grid, dx, dy);
  }
  
  public void fixTetromino(Tetromino tetromino) {
    tetromino.fixToGrid(grid);
  }
  
  private int clearFullRows() {
    int clearedRows = 0;
    for (int y = 0; y < HEIGHT; y++) {
      boolean isFull = true;
      for (int x = 0; x < WIDTH; x++) {
        if (grid[y][x] == null) {
          isFull = false;
          break;
        }
      }
      if (isFull) {
        clearedRows++;
        score += 100 * comboMultiplier; // Mehr Punkte mit Combo
        updateScoreDisplay();
        
        for (int row = y; row > 0; row--) {
          grid[row] = grid[row - 1].clone();
        }
        grid[0] = new Color[WIDTH];
      }
    }
    return clearedRows;
  }
  
  private void handleKeyPress(KeyEvent event) {
    switch (event.getCode()) {
      case SPACE:
        currentTetromino.rotate(grid);
        break;
      case A:
        currentTetromino.moveLeft(grid);
        break;
      case D:
        currentTetromino.moveRight(grid);
        break;
      case S:
        currentTetromino.moveDown(grid);
        break;
      case ESCAPE: 
        menu.loadMenu((Pane) gameScene.getRoot(), primaryStage);
        break;  
      default:                                                                                  
        System.out.println("Falsche Taste");
    }
  }
  
  public Scene getGameScene() {                                                    
    return gameScene;
  }
  
  private void updateScoreDisplay() {
    scoreText.setText("Score: " + score);
  }
  
  private void updateComboDisplay() {
    comboText.setText("Combo: x" + comboMultiplier);
  }
  
  private void incrementScore() {
    // Einfaches Inkrement für die kontinuierliche Punktzahl (optional)
    score += 1;
    updateScoreDisplay();
  }
}