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

    // Neue Spielfigur
    private Rectangle player;
    private double playerX = 150; // Startposition der Spielfigur
    private double playerY = 150; // Startposition der Spielfigur
    private final double playerSpeed = TILE_SIZE; // Geschwindigkeit der Spielfigur

    public TetriJump(Stage primaryStage, TetriGui app) {
        this.primaryStage = primaryStage;
        this.app = app;
        createGame(primaryStage, 20, 22);
    }

    public void createGame(Stage primaryStage, int width, int height) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: black;"); // Hintergrundfarbe

        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().add(canvas);

        // Spielfigur hinzufügen
        player = new Rectangle(playerX, playerY, TILE_SIZE, TILE_SIZE);
        player.setFill(Color.RED); // Spielfigur in Rot
        root.getChildren().add(player);

        gameScene = new Scene(root, width * TILE_SIZE, height * TILE_SIZE);
        gameScene.setOnKeyPressed(event -> handleKeyPress(event));

        startGame(canvas.getGraphicsContext2D());
    }

    private void startGame(GraphicsContext gc) {
        currentTetromino = Tetromino.createRandomTetromino(WIDTH / 2, 0);

        gameLoop = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            updateGame();
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
            clearFullRows();
            currentTetromino = Tetromino.createRandomTetromino(WIDTH / 2, 0);
        }
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        // Render the fixed grid blocks (with their colors)
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] != null) {
                    gc.setFill(grid[y][x]);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Render the current tetromino
        currentTetromino.render(gc, TILE_SIZE);
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
            // Steuerung der Spielfigur mit den Pfeiltasten
            case UP:
                playerY -= playerSpeed;
                break;
            case DOWN:
                playerY += playerSpeed;
                break;
            case LEFT:
                playerX -= playerSpeed;
                break;
            case RIGHT:
                playerX += playerSpeed;
                break;
            default:
                System.out.println("Falsche Taste");
        }
        // Position der Spielfigur aktualisieren
        player.setX(playerX);
        player.setY(playerY);
    }

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public Color[][] getGrid() {
        return grid;
    }

    public Scene getGameScene() {
        return this.gameScene;
    }
}
