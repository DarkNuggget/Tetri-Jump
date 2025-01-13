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
    private int[][] grid = new int[HEIGHT][WIDTH];  // Spielfeld
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
    //TODO NOTE: currentTetromino = player!!
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
        // Tetromino nach unten bewegen
        if (canMove(currentTetromino, 0, 1)) {
            currentTetromino.moveDown();
        } else {
            // Tetromino hat Boden erreicht
            fixTetromino(currentTetromino);
            // volle reihen entfernen
            clearFullRows();
            // neuen Block erstellen
            currentTetromino = Tetromino.createRandomTetromino(WIDTH / 2, 0);
        }
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] != 0) {
                    gc.setFill(Color.GRAY);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
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
                if (grid[y][x] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                // alle anderen Reihen nach unten
                for (int row = y; row > 0; row--) {
                    grid[row] = grid[row - 1].clone();
                }
                grid[0] = new int[WIDTH];
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
                /*if (isGameActive) {
                    pauseGame();
                    app.showStartScreenFromGame();  // Show the start screen when ESC is pressed
                }*/
                break;
            default:
                System.out.println("Falsche Taste");
        }
    }
  
    public Scene getGameScene() {
        return this.gameScene;
    }
}