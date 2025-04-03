import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class Tetromino {
  private static final int[][][] SHAPES = {
        {{1, 1, 1, 1}},                        // I-Form
        {{1, 1}, {1, 1}},                      // O-Form
        {{0, 1, 0}, {1, 1, 1}},               // T-Form
        {{1, 1, 0}, {0, 1, 1}},               // S-Form
        {{0, 1, 1}, {1, 1, 0}},               // Z-Form
        {{1, 1, 1}, {1, 0, 0}},               // L-Form
        {{1, 1, 1}, {0, 0, 1}}                // J-Form 
    };

  private int[][] shape;
  private int x, y;
  private Color color;

  public Tetromino(int[][] shape, int x, int y, Color color) {
    this.shape = shape;
    this.x = x;
    this.y = y;
    this.color = color;
  }

  public static Tetromino createRandomTetromino(int startX, int startY) {
    Random random = new Random();
    int index = random.nextInt(SHAPES.length);
    int[][] shape = SHAPES[index];
    
    Color[] colors = {Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN, Color.RED, Color.ORANGE, Color.BLUE};
    Color color = colors[index];
    
    return new Tetromino(shape, startX, startY, color);
  }

  public void moveDown() {
    y++;
  }

  public void moveLeft() {
    x--;
  }

  public void moveRight() {
    x++;
  }

  // In der Tetromino-Klasse
public void rotate(Color[][] grid) {
    int rows = shape.length;
    int cols = shape[0].length;
    int[][] rotatedShape = new int[cols][rows];
    
    // Rotierte Form berechnen
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            rotatedShape[j][rows - 1 - i] = shape[i][j];
        }
    }
    
    // Prüfen, ob die Rotation möglich ist
    int originalX = x;
    shape = rotatedShape; // Temporär rotieren
    if (!canMove(grid, 0, 0)) { // Prüfe aktuelle Position nach Rotation
        // Wall-Kick: Versuche, das Tetromino nach links oder rechts zu verschieben
        if (canMove(grid, -1, 0)) {
            x--; // Nach links verschieben
        } else if (canMove(grid, 1, 0)) {
            x++; // Nach rechts verschieben
        } else if (canMove(grid, -2, 0)) {
            x -= 2; // Weiter nach links
        } else if (canMove(grid, 2, 0)) {
            x += 2; // Weiter nach rechts
        } else {
            // Wenn keine Korrektur möglich, Rotation rückgängig machen
            shape = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    shape[i][j] = rotatedShape[rows - 1 - j][i];
                }
            }
            x = originalX; // Position zurücksetzen
        }
    }
}

  // Überprüft, ob das Tetromino an der neuen Position (mit dx, dy Verschiebung) bewegt werden kann
  public boolean canMove(Color[][] grid, int dx, int dy) {
    for (int row = 0; row < shape.length; row++) {
      for (int col = 0; col < shape[row].length; col++) {
        if (shape[row][col] != 0) {
          int newX = x + col + dx;
          int newY = y + row + dy;
          
          // Prüfen, ob das neue Koordinaten innerhalb der Grenzen des Spielfeldes liegen
          if (newX < 0 || newX >= grid[0].length || newY >= grid.length) {
            return false;  // Außerhalb des Spielfeldes
          }
          
          // Überprüfen, ob die neue Position mit einem bestehenden Block kollidiert
          if (newY >= 0 && grid[newY][newX] != null) {
            return false;  // Kollisionsblock gefunden
          }
        }
      }
    }
    return true;  // Keine Kollision, Bewegung ist erlaubt
  }

  // Überprüft, ob das Tetromino gedreht werden kann (ohne Kollision mit bestehenden Blöcken)
  public boolean canRotate(Color[][] grid) {
    int rows = shape.length;
    int cols = shape[0].length;
    int[][] rotatedShape = new int[cols][rows];
    
    // Rotierte Form berechnen
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        rotatedShape[j][rows - 1 - i] = shape[i][j];
      }
    }
    
    // Überprüfen, ob die rotierte Form passt
    for (int row = 0; row < rotatedShape.length; row++) {
      for (int col = 0; col < rotatedShape[row].length; col++) {
        if (rotatedShape[row][col] != 0) {
          int newX = x + col;
          int newY = y + row;
          
          // Prüfen, ob die rotierte Form außerhalb des Spielfelds liegt
          if (newX < 0 || newX >= grid[0].length || newY >= grid.length) {
            return false;
          }
          
          // Prüfen, ob die rotierte Form mit vorhandenen Blöcken kollidiert
          if (newY >= 0 && grid[newY][newX] != null) {
            return false;
          }
        }
      }
    }
    
    return true;
  }

  // Fixiert das Tetromino auf dem Spielfeld
  public void fixToGrid(Color[][] grid) {
    for (int row = 0; row < shape.length; row++) {
      for (int col = 0; col < shape[row].length; col++) {
        if (shape[row][col] != 0) {
          int gridX = x + col;
          int gridY = y + row;
          if (gridY >= 0 && gridY < grid.length && gridX >= 0 && gridX < grid[0].length) {
            grid[gridY][gridX] = color;
          }
        }
      }
    }
  }

  // Zeichnet das Tetromino auf dem Canvas
  public void render(GraphicsContext gc, int tileSize) {
    // Zeichnen des Tetrominos
    gc.setFill(color);
    
    for (int row = 0; row < shape.length; row++) {
      for (int col = 0; col < shape[row].length; col++) {
        if (shape[row][col] != 0) {
          int drawX = (x + col) * tileSize;
          int drawY = (y + row) * tileSize;
          
          // Zeichne das Tetromino als gefülltes Rechteck
          gc.fillRect(drawX, drawY, tileSize, tileSize);
        }
      }
    }
    
    // Zeichnen der Hitbox (Umrandung)
    gc.setStroke(Color.BLACK);  // Farbe für die Hitbox (z.B. schwarz)
    gc.setLineWidth(0);         // Dicke der Linie
    
    // Umrandung des Tetrominos
    for (int row = 0; row < shape.length; row++) {
      for (int col = 0; col < shape[row].length; col++) {
        if (shape[row][col] != 0) {
          int drawX = (x + col) * tileSize;
          int drawY = (y + row) * tileSize;
          
          // Zeichne ein Rechteck um die Hitbox
          gc.strokeRect(drawX, drawY, tileSize, tileSize);
        }
      }
    }
  }

  // Gibt die Hitbox des Tetrominos als ein Array von 4 Werten zurück (links, oben, rechts, unten)
  public int[] getHitbox() {
    int left = x;
    int top = y;
    int right = x + getWidth() - 1;
    int bottom = y + shape.length - 1;
    
    return new int[] { left, top, right, bottom };
  }

  // Zusätzliche Methoden
  public int getWidth() {
    return shape[0].length;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int[][] getShape() {
    return shape;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Color getColor() {
    return color;
  }
   
   public void moveDown(Color[][] grid) {
        if (canMove(grid, 0, 1)) {
            y++;
        }
    }
    
    public void moveLeft(Color[][] grid) {
        if (canMove(grid, -1, 0)) {
            x--;
        }
    }
    
    public void moveRight(Color[][] grid) {
        if (canMove(grid, 1, 0)) {
            x++;
        }
    }
}