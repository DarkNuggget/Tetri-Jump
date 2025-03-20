import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow; // Für Schatten-Effekte
import javafx.scene.effect.Glow; // Für Glow-Effekte

public class TetriJump {
  
  private Scene gameScene;
  private Stage primaryStage;
  private TetriGui app;
  private StartScreen startScreen;
  private static final int TILE_SIZE = 30;
  
  public static int WIDTH = 20;
  public static int HEIGHT = 22;
  private Color[][] grid = new Color[HEIGHT][WIDTH];
  private Timeline gameLoop;
  private Tetromino currentTetromino;
  private InGameMenu menu = new InGameMenu();
  
  // Spielfigur mit visuellen Verbesserungen
  private Rectangle player;
  private double playerX = 100;
  private double playerY = 100;
  private final double playerSpeed = TILE_SIZE / 3; // Etwas langsamer für bessere Kontrolle
  private TranslateTransition playerTransition;
  private double velocityY = 0; // Für Sprungmechanik
  private final double gravity = 1; // Schwerkraft für realistische Sprünge
  private boolean isJumping = false;
  
  // Score und Combo-System
  private int score = 0;
  private int comboMultiplier = 1; // Für zusätzliche Belohnung bei Ketten
  private Text scoreText;
  private Text comboText; // Zeigt den Combo-Multiplikator
  private Timeline scoreIncrementer;
  
  public TetriJump(Stage primaryStage, TetriGui app) {
    this.primaryStage = primaryStage;
    this.app = app;
    createGame(primaryStage, 20, 22);
  }
  
  public void createGame(Stage primaryStage, int width, int height) {
    Pane root = new Pane();
    root.setStyle("-fx-background-color: #1a1a1a;"); // Dunkler, professioneller Hintergrund
    
    // Canvas für das Spielfeld
    Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
    root.getChildren().add(canvas);
    
    // Spielfigur mit Effekten
    player = new Rectangle(playerX, playerY, TILE_SIZE, TILE_SIZE);
    player.setFill(Color.CYAN); // Auffälligere Farbe
    player.setEffect(new DropShadow(10, Color.WHITE)); // Schatten für Tiefe
    root.getChildren().add(player);
    
    // Transition für flüssige Bewegung
    playerTransition = new TranslateTransition(Duration.millis(100), player);
    playerTransition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH); // Sanftere Bewegung
    
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
    
    gameLoop = new Timeline(new KeyFrame(Duration.millis(300), e -> { // Schnelleres Tempo
      updateGame();
      updatePlayer(); // Neue Methode für Spieler-Physik
      render(gc);
    }));
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.play();
  }
  
  private void updateGame() {
    if (canMove(currentTetromino, 0, 1)) {
      currentTetromino.moveDown();
    } else {
      fixTetromino(currentTetromino);
      int clearedRows = clearFullRows();
      if (clearedRows > 0) {
        comboMultiplier = Math.min(comboMultiplier + clearedRows, 5); // Max x5 Combo
      } else {
        comboMultiplier = 1; // Combo zurücksetzen
      }
      updateComboDisplay();
      currentTetromino = Tetromino.createRandomTetromino(WIDTH / 2, 0);
    }
  }
  
  // Neue Methode für Spieler-Physik (Sprung und Schwerkraft)
  private void updatePlayer() {
    if (isJumping) {
      velocityY += gravity;
      playerY += velocityY;
      
      // Überprüfen, ob der Spieler den Boden erreicht oder auf einem Block landet
      if (playerY >= HEIGHT * TILE_SIZE - TILE_SIZE || isCollidingWithBottom()) {
        playerY = HEIGHT * TILE_SIZE - TILE_SIZE;  // Spieler bleibt auf dem Boden stehen
        isJumping = false;
        velocityY = 0;
      }
    }
    
    // Kollision mit Blocken oben
    if (isCollidingWithTop()) {
      // Falls der Spieler oben mit einem Block kollidiert, verhindern wir das weitere Nach-oben-Bewegen
      velocityY = 0;  // Verhindert das Aufprallen auf den Block
    }
    
    movePlayerSmoothly();
  }
  
  
  private boolean isCollidingWithLeft() {
    int playerGridX = (int) (playerX / TILE_SIZE);
    int playerGridY = (int) (playerY / TILE_SIZE);
    return playerGridX > 0 && grid[playerGridY][playerGridX - 1] != null;
  }
  
  private boolean isCollidingWithRight() {
    int playerGridX = (int) ((playerX + TILE_SIZE) / TILE_SIZE);
    int playerGridY = (int) (playerY / TILE_SIZE);
    return playerGridX < WIDTH - 1 && grid[playerGridY][playerGridX + 1] != null;
  }
  private boolean isCollidingWithTop() {
    // Berechne die Position des Spielers im Gitter (für die obere Kante)
    int playerGridX = (int) (playerX / TILE_SIZE);
    int playerGridY = (int) (playerY / TILE_SIZE);  // Direkt über dem Spieler (obere Kante)
    
    // Überprüfen, ob der Spieler oben mit einem Block kollidiert
    if (playerGridY > 0) {
      if (grid[playerGridY - 1][playerGridX] != null) {
        return true;
      }
    }
    return false;
  }
  private boolean isCollidingWithBottom() {
    // Berechne die Position des Spielers im Gitter (für die untere Kante)
    int playerGridX = (int) (playerX / TILE_SIZE);
    int playerGridY = (int) ((playerY + TILE_SIZE) / TILE_SIZE);  // Direkt unter dem Spieler (untere Kante)
    
    // Überprüfen, ob der Spieler unten mit einem Block kollidiert
    if (playerGridY < HEIGHT) {
      if (grid[playerGridY][playerGridX] != null) {
        return true;
      }
    }
    return false;
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
            currentTetromino.rotate();
            break;
        case A:
            currentTetromino.moveLeft();
            break;
        case D:
            currentTetromino.moveRight();
            break;
        case S:
            currentTetromino.moveDown();
            break;
        case M: 
            menu.loadMenu((Pane) gameScene.getRoot(), primaryStage);
            break;  
        case UP:
            if (!isJumping) {
                isJumping = true;
                velocityY = -10;  // Sprungkraft
            }
            break;
        case LEFT:
            if (!isCollidingWithLeft()) {
                playerX -= playerSpeed;  // Links bewegen
            }
            movePlayerSmoothly();
            break;
        case RIGHT:
            if (!isCollidingWithRight()) {
                playerX += playerSpeed;  // Rechts bewegen
            }
            movePlayerSmoothly();
            break;
        default:
            System.out.println("Falsche Taste");
    }
}




  private void movePlayerSmoothly() {
    if (playerX < 0) {
      playerX = WIDTH * TILE_SIZE - TILE_SIZE;
    } else if (playerX > WIDTH * TILE_SIZE - TILE_SIZE) {
      playerX = 0;
    }
    
    if (playerY < 0) {
      playerY = HEIGHT * TILE_SIZE - TILE_SIZE;
    } else if (playerY > HEIGHT * TILE_SIZE - TILE_SIZE) {
      playerY = HEIGHT * TILE_SIZE - TILE_SIZE;
    }
    
    player.setX(playerX);
    player.setY(playerY);
  }
  
  public Scene getGameScene() {
    return gameScene;
  }



  private boolean isCollidingWithBlock() {
    int playerGridX = (int) (playerX / TILE_SIZE);
    int playerGridY = (int) ((playerY + TILE_SIZE) / TILE_SIZE); // Direkt unter dem Spieler
    
    // Kollision überprüfen, wenn der Spieler auf einem Block landet
    if (playerGridY < HEIGHT) {
      // Wenn unter dem Spieler ein Block existiert
      if (grid[playerGridY][playerGridX] != null) {
        return true;
      }
    }
    return false;
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
