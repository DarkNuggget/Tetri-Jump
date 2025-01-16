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

    public void rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotatedShape = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotatedShape[j][rows - 1 - i] = shape[i][j];
            }
        }

        shape = rotatedShape;
    }

    public boolean canMove(Color[][] grid, int dx, int dy) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col + dx;
                    int newY = y + row + dy;

                    if (newX < 0 || newX >= grid[0].length || newY >= grid.length) {
                        return false;
                    }

                    if (newY >= 0 && grid[newY][newX] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

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

    public void render(GraphicsContext gc, int tileSize) {
        gc.setFill(color);

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int drawX = (x + col) * tileSize;
                    int drawY = (y + row) * tileSize;

                    gc.fillRect(drawX, drawY, tileSize, tileSize);
                }
            }
        }
    }
}
