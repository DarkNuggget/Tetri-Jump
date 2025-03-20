import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Player {
  private double playerX;
  private double playerY;
  private final double playerSpeed = 30;  // Geschwindigkeit des Spielers
  private final int TILE_SIZE = 30;  // Beispiel für Tile-Größe
  private final int WIDTH = 20;      // Breite des Spielfeldes (in Tiles)
  private final int HEIGHT = 22;     // Höhe des Spielfeldes (in Tiles)
  
  private Color[][] grid; // Spielfeld-Grid

  // Konstruktor
  public Player(Color[][] grid) {
    this.grid = grid;
    playerX = 100;
    playerY = 100;
  }

  // Die Methode zur Handhabung der Tastenanschläge
  public void handleKeyPress(KeyEvent event, Tetromino[] tetrominos) {
    switch (event.getCode()) {
      case UP:
        jump();
        break;
      case LEFT:
        moveLeft(tetrominos);
        break;
      case RIGHT:
        moveRight(tetrominos);
        break;
      case DOWN:
        moveDown(tetrominos);
        break;
      default:
        // Andere Tastenaktionen
        System.out.println("Andere Taste gedrückt: " + event.getCode());
        break;
    }
  }

  // Bewege den Spieler nach links
  private void moveLeft(Tetromino[] tetrominos) {
    double newX = playerX - playerSpeed;

    // Überprüfe, ob der Spieler sich innerhalb der Spielfeldgrenzen befindet
    if (newX < 0) {
        return;  // Der Spieler ist zu weit links, keine Bewegung
    }

    // Prüfen, ob der neue X-Wert eine Kollision mit einem Tetromino verursacht
    for (Tetromino tetromino : tetrominos) {
        if (!tetromino.canMove(grid, (int)newX, (int)playerY)) {
            return;  // Kollision, keine Bewegung
        }
    }

    playerX = newX;
  }

  // Bewege den Spieler nach rechts
  private void moveRight(Tetromino[] tetrominos) {
    double newX = playerX + playerSpeed;

    // Überprüfe, ob der Spieler sich innerhalb der Spielfeldgrenzen befindet
    if (newX + TILE_SIZE > WIDTH * TILE_SIZE) {
        return;  // Der Spieler ist zu weit rechts, keine Bewegung
    }

    // Prüfen, ob der neue X-Wert eine Kollision mit einem Tetromino verursacht
    for (Tetromino tetromino : tetrominos) {
        if (!tetromino.canMove(grid, (int)newX, (int)playerY)) {
            return;  // Kollision, keine Bewegung
        }
    }

    playerX = newX;
  }

  // Bewege den Spieler nach unten
  private void moveDown(Tetromino[] tetrominos) {
    double newY = playerY + playerSpeed;

    // Überprüfe, ob der Spieler sich innerhalb der Spielfeldgrenzen befindet
    if (newY + TILE_SIZE > HEIGHT * TILE_SIZE) {
        return;  // Der Spieler ist zu weit unten, keine Bewegung
    }

    // Prüfen, ob der neue Y-Wert eine Kollision mit einem Tetromino verursacht
    for (Tetromino tetromino : tetrominos) {
        if (!tetromino.canMove(grid, (int)playerX, (int)newY)) {
            return;  // Kollision, keine Bewegung
        }
    }

    playerY = newY;
  }

  // Sprung-Mechanik
  private void jump() {
    playerY -= 100;  // Beispielhöhe des Sprungs
  }

  // Überprüfe, ob der Spieler an einem grauen Block ist
  private boolean isTouchingGray(double x, double y) {
    int playerGridX = (int) (x / TILE_SIZE);
    int playerGridY = (int) (y / TILE_SIZE);
    
    // Überprüfen, ob das Feld eine graue Farbe hat
    return grid[playerGridY][playerGridX] != null && grid[playerGridY][playerGridX].equals(Color.GRAY);
  }

  // Getter-Methoden für die Spielerposition
  public double getPlayerX() {
    return playerX;
  }

  public double getPlayerY() {
    return playerY;
  }

  public void setPlayerPosition(double x, double y) {
    playerX = x;
    playerY = y;
  }
}
