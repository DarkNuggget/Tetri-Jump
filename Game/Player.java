import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Player {
    private double playerX;
    private double playerY;
    private Skin skin;
    private final double playerSpeed = 30;
    private final int TILE_SIZE = 30;
    private final int WIDTH = 20;
    private final int HEIGHT = 22;
    private Color[][] grid;

    public Player(Color[][] grid, Skin skin) {
        this.grid = grid;
        this.skin = skin;
        playerX = 100;
        playerY = 100;
    }

    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                jump();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
            case DOWN:
                moveDown();
                break;
            default:
                System.out.println("Andere Taste gedrückt: " + event.getCode());
                break;
        }
    }

    private void moveLeft() {
        double newX = playerX - playerSpeed;
        if (newX < 0) {
            System.out.println("Spieler berührt den linken Rand des Spielfeldes!");
            return;
        }
        playerX = newX;
    }

    private void moveRight() {
        double newX = playerX + playerSpeed;
        if (newX + TILE_SIZE > WIDTH * TILE_SIZE) {
            System.out.println("Spieler berührt den rechten Rand des Spielfeldes!");
            return;
        }
        playerX = newX;
    }

    private void moveDown() {
        double newY = playerY + playerSpeed;
        if (newY + TILE_SIZE > HEIGHT * TILE_SIZE) {
            System.out.println("Spieler berührt den unteren Rand des Spielfeldes!");
            return;
        }
        playerY = newY;
    }

    private void jump() {
        double newY = playerY - 100;
        if (newY < 0) {
            System.out.println("Spieler berührt den oberen Rand des Spielfeldes!");
            playerY = 0;
            return;
        }
        playerY = newY;
    }

    // Methode zur Kollisionsprüfung, die wir später im Game-Loop nutzen
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

    // Getter/Setter ...
    public double getPlayerX() { return playerX; }
    public double getPlayerY() { return playerY; }
    public void setPlayerPosition(double x, double y) { playerX = x; playerY = y; }
    public Skin getSkin() { return skin; }
    public void setSkin(Skin skin) { this.skin = skin; }
}