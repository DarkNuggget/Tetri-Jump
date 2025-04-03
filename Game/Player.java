import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Player {
  private double playerX;
  private double playerY;
  private Skin skin;
  private final double playerSpeed = 5;
  private final int TILE_SIZE = 30;
  private final int WIDTH = 20;
  private final int HEIGHT = 22;
  private Color[][] grid;
  private double velocityY = 0; // Vertikale Geschwindigkeit für Sprung und Schwerkraft
  private final double gravity = 1; // Schwerkraft
  private final double jumpStrength = -15; // Anfangsgeschwindigkeit beim Springen
  private boolean isJumping = false;
  public final static MusikPlayer musikPlayer = new MusikPlayer();
  private boolean leftPressed = false;
  private boolean rightPressed = false;
  private boolean downPressed = false;
  private boolean jumpRequested = false;
  private Stage primaryStage;
  
  private boolean isDeathScreenShown = false; // Flag, ob der Deathscreen bereits angezeigt wurde

  public Player(Color[][] grid, Skin skin, Stage primaryStage) {
    this.grid = grid;
    this.skin = skin;
    this.playerX = 100;
    this.playerY = 100;
    this.primaryStage = primaryStage;
  }
  
  public void handleKeyPress(KeyEvent event) {
    switch (event.getCode()) {
      case A:
        leftPressed = true;
        break;
      case D:
        rightPressed = true;
        break;
      case SPACE:
        jumpRequested = true;
        InGameMenu.musikPlayer.playJumpSound();
        break;
      default:
        System.out.println("False Taste:  " + event.getCode());
        break;
    }
  }
  
  public void handleKeyRelease(KeyEvent event) {
    switch (event.getCode()) {
      case A:
        leftPressed = false;
        break;
      case D:
        rightPressed = false;
        break;
      case SPACE:
        jumpRequested = false;
        break;
    }
  }
  
  public void update() {
         
      // Horizontale Bewegung
      if (leftPressed) {
        moveLeft();
      }
      if (rightPressed) {
        moveRight();
      }
      if (downPressed) {
        moveDown();
      }
      if (jumpRequested && !isJumping) {
        jump();
        jumpRequested = false; // Sprung nur einmal auslösen
      }
      
      // Schwerkraft anwenden
      velocityY += gravity;
      double newY = playerY + velocityY;
      
      // Wenn der Spieler Y >= 630 erreicht, gehe zur HellDeathScreen Klasse
      if (newY >= 630) {
        // Hier den Übergang zur DeathScreen Klasse initiieren
        goToDeathScreen(primaryStage);
        return;  // Stoppe die weitere Aktualisierung der Position
    }
      
      // Prüfe Kollision mit fixierten Blöcken oder Boden
      if (velocityY > 0) { // Fallend
        if (isStandingOnBlock(newY)) {
          newY = Math.floor(newY / TILE_SIZE) * TILE_SIZE;
          velocityY = 0;
          isJumping = false;
        } else if (newY + TILE_SIZE >= HEIGHT * TILE_SIZE) {
          newY = (HEIGHT - 1) * TILE_SIZE;
          velocityY = 0;
          isJumping = false;
        }
      } else if (velocityY < 0) { // Springend
        if (isCollidingWithCeiling(newY)) {
          newY = Math.ceil(newY / TILE_SIZE) * TILE_SIZE;
          velocityY = 0;
        }
      }
      
      playerY = newY;    
  }
  
 private void goToDeathScreen(Stage primaryStage) {
    if (isDeathScreenShown) {
        return; // Wenn der Deathscreen bereits angezeigt wurde, nichts tun
    }
    
    isDeathScreenShown = true; // Markiere den Deathscreen als angezeigt
    
    // Erstelle die DeathScreen-Instanz
    Scene previousScene = primaryStage.getScene(); // Hol dir die vorherige Szene
    
    // Erstelle und zeige den HellDeathScreen
    HellDeathScreen hellDeathScreen = new HellDeathScreen(primaryStage, previousScene);
    hellDeathScreen.show();
    
    // Du kannst auch sicherstellen, dass die Hintergrundmusik oder andere Übergänge im `HellDeathScreen` korrekt abgespielt werden.
}

  
  private void moveLeft() {
    double newX = playerX - playerSpeed;
    if (newX < 0) {
      System.out.println("Spieler berührt den linken Rand des Spielfeldes!");
      return;
    }
    if (isPositionValid(newX, playerY)) {
      playerX = newX;
    }
  }
  
  private void moveRight() {
    double newX = playerX + playerSpeed;
    if (newX + TILE_SIZE > WIDTH * TILE_SIZE) {
      System.out.println("Spieler berührt den rechten Rand des Spielfeldes!");
      return;
    }
    if (isPositionValid(newX, playerY)) {
      playerX = newX;
    }
  }
  
  private void moveDown() {
    double newY = playerY + playerSpeed;
    if (newY + TILE_SIZE > HEIGHT * TILE_SIZE) {
      System.out.println("Spieler berührt den unteren Rand des Spielfeldes!");
      return;
    }
    if (isPositionValid(playerX, newY)) {
      playerY = newY;
    }
  }
  
  private void jump() {
    if (!isJumping) {
      velocityY = jumpStrength;
      isJumping = true;
    }
  }
  
  private boolean isPositionValid(double x, double y) {
    int gridX = (int) (x / TILE_SIZE);
    int gridY = (int) (y / TILE_SIZE);
    
    // Außerhalb des Spielfelds?
    if (gridX < 0 || gridX >= WIDTH || gridY < 0 || gridY >= HEIGHT) {
      return false;
    }
    
    // Kollision mit fixiertem Block?
    return grid[gridY][gridX] == null;
  }
  
  private boolean isStandingOnBlock(double y) {
    int gridX = (int) (playerX / TILE_SIZE);
    int gridY = (int) ((y + TILE_SIZE) / TILE_SIZE);
    
    if (gridY >= HEIGHT) return true; // Boden erreicht
    return grid[gridY][gridX] != null; // Block darunter
  }
  
  private boolean isCollidingWithCeiling(double y) {
    int gridX = (int) (playerX / TILE_SIZE);
    int gridY = (int) (y / TILE_SIZE);
    
    if (gridY < 0) return true; // Obere Grenze
    return grid[gridY][gridX] != null; // Block darüber
  }
  
  public boolean isCollidingWithTetromino(Tetromino tetromino) {
    if (tetromino == null) return false;
    
    int playerGridX = (int) (playerX / TILE_SIZE);
    int playerGridY = (int) (playerY / TILE_SIZE);
    
    int[] hitbox = tetromino.getHitbox();
    int tetroLeft = hitbox[0];
    int tetroTop = hitbox[1];
    int tetroRight = hitbox[2];
    int tetroBottom = hitbox[3];
    
    if (playerGridX >= tetroLeft && playerGridX <= tetroRight &&
    playerGridY >= tetroTop && playerGridY <= tetroBottom) {
      int[][] shape = tetromino.getShape();
      int relX = playerGridX - tetromino.getX();
      int relY = playerGridY - tetromino.getY();
      
      if (relX >= 0 && relX < shape[0].length && relY >= 0 && relY < shape.length) {
        return shape[relY][relX] != 0;
      }
    }
    return false;
  }
  
  // Getter/Setter
  public double getPlayerX() { return playerX; }
  public double getPlayerY() { return playerY; }
  public void setPlayerPosition(double x, double y) { playerX = x; playerY = y; }
  public Skin getSkin() { return skin; }
  public void setSkin(Skin skin) { this.skin = skin; }
}
