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
    private TetrominoWithTimer currentTetromino;
    private InGameMenu menu = new InGameMenu();
    private Random random = new Random();
    private Player player;
  
  private double tetrominoFallSpeed = 2.5;
  private double tetrominoYPosition = 0;
  
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
        gameScene.setOnKeyReleased(event -> player.handleKeyRelease(event));
        
        // Spieler initialisieren
        Skin defaultSkin = new Skin("BernDasBrot", "Skins/BerndDasBrot.png", true);
        player = new Player(grid, defaultSkin);
        
        // Spawn-Plattform erstellen (I-Tetromino am Boden)
        Tetromino spawnPlatform = new Tetromino(new int[][]{{1, 1, 1, 1}}, WIDTH / 2 - 2, HEIGHT - 1, Color.GRAY);
        fixTetromino(spawnPlatform);
        
        // Timer für Plattform-Despawn
        Timeline platformTimer = new Timeline(new javafx.animation.KeyFrame(Duration.seconds(10), e -> {
            removeSpawnPlatform(spawnPlatform);
        }));
        platformTimer.setCycleCount(1);
        platformTimer.play();
        
        // Spieler auf Plattform spawnen
        player.setPlayerPosition((WIDTH / 2 - 1) * TILE_SIZE, (HEIGHT - 2) * TILE_SIZE); // Über der Plattform
        
        spawnRandomTetromino();
        
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
        if (currentTetromino == null || currentTetromino.isDespawned()) {
            return false; // Kein Tetromino vorhanden oder despawnt
        }
        Tetromino tetromino = currentTetromino.getTetromino();
        int[] hitbox = tetromino.getHitbox(); // Zugriff über getTetromino()
        int tetroLeft = hitbox[0];
        int tetroTop = hitbox[1];
        int tetroRight = hitbox[2];
        int tetroBottom = hitbox[3];
    
        return gridX >= tetroLeft && gridX <= tetroRight && gridY >= tetroTop && gridY <= tetroBottom;
    }

    private void startGame(GraphicsContext gc) {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            try {
                updateGame();
                player.update();
                render(gc);
            } catch (RuntimeException ex) {
                gameLoop.stop();
                System.out.println(ex.getMessage());
            }
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
    
        if (currentTetromino != null && !currentTetromino.isDespawned()) {
            currentTetromino.render(gc, tetrominoYPosition, TILE_SIZE);
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
        Tetromino tetromino = (currentTetromino != null && !currentTetromino.isDespawned()) ? currentTetromino.getTetromino() : null;
        switch (event.getCode()) {
            case ESCAPE:
                menu.loadMenu((Pane) gameScene.getRoot(), primaryStage);
                break;
            default:
                player.handleKeyPress(event);
                break;
        }
    }

    // Getter für den Spieler (falls benötigt)
    public Player getPlayer() {
        return player;
    }
  
    private void spawnRandomTetromino() {
        int maxAttempts = 10;
        int attempt = 0;
        boolean validPosition = false;
        int spawnX;
    
        while (!validPosition && attempt < maxAttempts) {
            spawnX = random.nextInt(WIDTH - 4);
            Tetromino tetromino = Tetromino.createRandomTetromino(spawnX, 0);
            validPosition = isSpawnAreaFree(tetromino);
            attempt++;
    
            if (validPosition) {
                currentTetromino = new TetrominoWithTimer(tetromino);
                tetrominoYPosition = currentTetromino.getTetromino().getY() * TILE_SIZE;
            }
    
            if (!validPosition && attempt >= maxAttempts) {
                gameLoop.stop();
                System.out.println("Game Over: Kein Platz zum Spawnen!");
                return;
            }
        }
    }
  
    private void updateGame() {
        if (currentTetromino == null || currentTetromino.isDespawned()) {
            spawnRandomTetromino();
            return;
        }
    
        Tetromino tetromino = currentTetromino.getTetromino();
        tetrominoYPosition += tetrominoFallSpeed;
        int newGridY = (int) (tetrominoYPosition / TILE_SIZE);
        tetromino.setPosition(tetromino.getX(), newGridY);
    
        if (isTetrominoCollidingWithPlayer(tetromino)) {
            fixTetromino(tetromino);
            currentTetromino.stopTimer();
            gameLoop.stop();
            System.out.println("Game Over: Tetromino hat den Spieler getroffen!");
            return;
        }
    
        if (canMove(tetromino, 0, 1)) {
            // Nichts tun, Position wird bereits aktualisiert
        } else {
            fixTetromino(tetromino);
            currentTetromino.stopTimer();
            clearFullRows();
            spawnRandomTetromino();
            tetrominoYPosition = currentTetromino.getTetromino().getY() * TILE_SIZE;
        }
    }
  
    private boolean isTetrominoCollidingWithPlayer(Tetromino tetromino) {
        if (tetromino == null) return false;
    
        int[][] shape = tetromino.getShape();
        int tetroX = tetromino.getX();
        int tetroY = tetromino.getY();
    
        int playerGridX = (int) (player.getPlayerX() / TILE_SIZE);
        int playerGridY = (int) (player.getPlayerY() / TILE_SIZE);
    
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridX = tetroX + col;
                    int gridY = tetroY + row;
    
                    // Prüfe, ob dieser Block des Tetrominos den Spieler trifft
                    if (gridX == playerGridX && gridY == playerGridY) {
                        return true;
                    }
                }
            }
        }
        return false;
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
  
    private boolean isSpawnAreaFree(Tetromino tetromino) {
    int[][] shape = tetromino.getShape();
    int tetroX = tetromino.getX();
    int tetroY = tetromino.getY();

    // Spielerposition im Grid
    int playerGridX = (int) (player.getPlayerX() / TILE_SIZE);
    int playerGridY = (int) (player.getPlayerY() / TILE_SIZE);

    // Prüfe jeden Block des Tetrominos
    for (int row = 0; row < shape.length; row++) {
        for (int col = 0; col < shape[row].length; col++) {
            if (shape[row][col] != 0) {
                int gridX = tetroX + col;
                int gridY = tetroY + row;

                // Prüfe, ob die Position außerhalb des Spielfelds liegt
                if (gridX < 0 || gridX >= WIDTH || gridY >= HEIGHT) {
                    return false;
                }

                // Prüfe Kollision mit fixierten Blöcken im Grid
                if (gridY >= 0 && grid[gridY][gridX] != null) {
                    return false;
                }

                // Prüfe Kollision mit dem Spieler
                if (gridX == playerGridX && gridY == playerGridY) {
                    return false;
                }
            }
        }
    }
    return true;
}
 
     private void removeSpawnPlatform(Tetromino platform) {
        int[][] shape = platform.getShape();
        int platX = platform.getX();
        int platY = platform.getY();
    
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridX = platX + col;
                    int gridY = platY + row;
                    if (gridX >= 0 && gridX < WIDTH && gridY >= 0 && gridY < HEIGHT) {
                        grid[gridY][gridX] = null; // Entferne Plattform aus dem Grid
                    }
                }
            }
        }
    }
  
    public Scene getGameScene() {
           return this.gameScene;
    } 
}
