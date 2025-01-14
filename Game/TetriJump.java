import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;

public class TetriJump {

    private Scene gameScene;
    private Stage primaryStage;
    private TetriGui app;

    private static final int TILE_SIZE = 30;

    private int WIDTH = 20;
    private int HEIGHT = 20;
    private Color[][] grid = new Color[HEIGHT][WIDTH];  // Spielfeld mit Farben
    private Timeline gameLoop;
    private Tetromino currentTetromino;

    public TetriJump(Stage primaryStage, TetriGui app) {
        this.primaryStage = primaryStage;
        this.app = app;
        createGame(primaryStage, 20, 20);
    }

    private void createGame(Stage primaryStage, int width, int height) {
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        gameScene = new Scene(root, width * TILE_SIZE, height * TILE_SIZE);
        gameScene.setOnKeyPressed(event -> handleKeyPress(event));

        startGame(gc);
    }

    private void startGame(GraphicsContext gc) {
        currentTetromino = Tetromino.createRandomTetromino(WIDTH / 2, 0);

        gameLoop = new Timeline(new KeyFrame(Duration.millis(500), e -> {
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

    private boolean canMove(Tetromino tetromino, int dx, int dy) {
        return tetromino.canMove(grid, dx, dy);
    }

    private void fixTetromino(Tetromino tetromino) {
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
                // Alle anderen Reihen nach unten verschieben
                for (int row = y; row > 0; row--) {
                    grid[row] = grid[row - 1].clone();
                }
                grid[0] = new Color[WIDTH];  // Leere Reihe oben hinzufügen
            }
        }
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
                currentTetromino.rotate();
                break;
            case A:
                player.setX(playerX - 1);
                break;
            case D:
                player.setX(playerX + 1);
                break;
            case S:
                player.setY(playerY + 1);
                break;
            default:
                System.out.println("Falsche Taste");
        }
    }

    public Scene getGameScene() {
        return this.gameScene;
    }
   

}
