import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import java.util.Random;
import javafx.scene.image.Image;

public class TetriAutoGame {

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
    private Random random = new Random();
    private Player player;
  
    private boolean lastCollisionState = false;

    public TetriAutoGame(Stage primaryStage, TetriGui app) {
        this.primaryStage = primaryStage;
        this.app = app;
        createGame(primaryStage, WIDTH, HEIGHT);
    }

    public void createGame(Stage primaryStage, int width, int height) {
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
    
        root.getChildren().add(canvas);
    
        gameScene = new Scene(root, width * TILE_SIZE, height * TILE_SIZE);
        gameScene.setOnKeyPressed(event -> handleKeyPress(event));
    
        spawnRandomTetromino();
        Skin defaultSkin = new Skin("BernDasBrot", "Skins/BerndDasBrot.png", true);
        player = new Player(grid, defaultSkin);
        spawnPlayerRandomly();
    
        startGame(gc);  
    }

    private void spawnPlayerRandomly() {
        int maxX = WIDTH - 1;
        int maxY = HEIGHT - 1;
        int spawnX = random.nextInt(maxX) * TILE_SIZE;
        int spawnY = random.nextInt(maxY) * TILE_SIZE;

        while (!isPositionFree(spawnX, spawnY)) {
            spawnX = random.nextInt(maxX) * TILE_SIZE;
            spawnY = random.nextInt(maxY) * TILE_SIZE;
        }

        player.setPlayerPosition(spawnX, spawnY);
    }

    private boolean isPositionFree(double x, double y) {
        int gridX = (int) (x / TILE_SIZE);
        int gridY = (int) (y / TILE_SIZE);
        return grid[gridY][gridX] == null && !isCollidingWithTetromino(gridX, gridY);
    }

    private boolean isCollidingWithTetromino(int gridX, int gridY) {
        if (currentTetromino == null) {
            return false; // Kein Tetromino vorhanden, keine Kollision möglich
        }
        int[] hitbox = currentTetromino.getHitbox();
        int tetroLeft = hitbox[0];
        int tetroTop = hitbox[1];
        int tetroRight = hitbox[2];
        int tetroBottom = hitbox[3];

        return gridX >= tetroLeft && gridX <= tetroRight && gridY >= tetroTop && gridY <= tetroBottom;
    }

    private void startGame(GraphicsContext gc) {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            updateGame();
            render(gc);
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] != null) {
                    gc.setFill(grid[y][x]);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        if (currentTetromino != null) {
            currentTetromino.render(gc, TILE_SIZE);
        }

        renderPlayer(gc);
    }

    private void renderPlayer(GraphicsContext gc) {
      String imagePath = player.getSkin().getImagePath();
        try {
            Image skinImage = new Image(imagePath);
            gc.drawImage(skinImage, player.getPlayerX(), player.getPlayerY(), TILE_SIZE, TILE_SIZE);
        } catch (Exception e) {
            gc.setFill(Color.WHITE);
            gc.fillRect(player.getPlayerX(), player.getPlayerY(), TILE_SIZE, TILE_SIZE);
            System.err.println("Fehler beim Laden des Skins: " + imagePath);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        Tetromino[] tetrominos = (currentTetromino != null) ? new Tetromino[]{currentTetromino} : new Tetromino[0];
        switch (event.getCode()) {

            case M:
                menu.loadMenu((Pane) gameScene.getRoot(), primaryStage);
                break;
            default:
                player.handleKeyPress(event, tetrominos);
                break;
        }
    }

    // Getter für den Spieler (falls benötigt)
    public Player getPlayer() {
        return player;
    }
  
    private void spawnRandomTetromino() {
        int randomX = random.nextInt(WIDTH - 4); // Zufällige X-Position
        currentTetromino = Tetromino.createRandomTetromino(randomX, 0);
    }
  
    private void updateGame() {
        if (canMove(currentTetromino, 0, 1)) {
            currentTetromino.moveDown();
        } else {
            fixTetromino(currentTetromino);
            clearFullRows();
            spawnRandomTetromino();
        }

        // Kontinuierliche Kollisionsprüfung
        boolean isColliding = player.isCollidingWithTetromino(currentTetromino);
        if (isColliding && !lastCollisionState) {
            System.out.println("Spieler berührt ein Tetromino!");
        }
        lastCollisionState = isColliding; // Speichere den letzten Zustand
    }
  
    public boolean canMove(Tetromino tetromino, int dx, int dy) {
        return tetromino.canMove(grid, dx, dy);
    }
  
    public void fixTetromino(Tetromino tetromino) {
        tetromino.fixToGrid(grid);
    }
  
    private void clearFullRows() {
        for (int y = 0; y < HEIGHT; y++) {
            boolean isFull = true;
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == null) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                for (int row = y; row > 0; row--) {
                    grid[row] = grid[row - 1].clone();
                }
                grid[0] = new Color[WIDTH];
            }
        }
    }
  
    public Scene getGameScene() {
           return this.gameScene;
    }
}
