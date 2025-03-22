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
    
        startGame(gc);  
    }

    private void startGame(GraphicsContext gc) {
        spawnRandomTetromino();

        gameLoop = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            updateGame();
            render(gc);
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
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
            default:
                System.out.println("Falsche Taste");
        }
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
