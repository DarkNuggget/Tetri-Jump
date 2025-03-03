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

import javafx.animation.TranslateTransition;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.animation.TranslateTransition;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TetriJump {

    private Scene gameScene;
    private Stage primaryStage;
    private TetriGui app;
    private StartScreen startScreen;
    private static final int TILE_SIZE = 30;

    public static int WIDTH = 20; // Breite des Spielfelds
    public static int HEIGHT = 22; // Höhe des Spielfelds
    private Color[][] grid = new Color[HEIGHT][WIDTH];
    private Timeline gameLoop;
    private Tetromino currentTetromino;
    private InGameMenu menu = new InGameMenu();

    // Neue Spielfigur
    private Rectangle player;
    private double playerX = 150; // Startposition der Spielfigur
    private double playerY = 150; // Startposition der Spielfigur
    private final double playerSpeed = TILE_SIZE; // Geschwindigkeit der Spielfigur
    private TranslateTransition playerTransition; // Für flüssige Bewegung

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

        // Transition für flüssige Bewegung
        playerTransition = new TranslateTransition();
        playerTransition.setNode(player);
        playerTransition.setInterpolator(javafx.animation.Interpolator.LINEAR); // Für gleichmäßige Bewegung

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
                movePlayerSmoothly();
                break;
            case DOWN:
                playerY += playerSpeed;
                movePlayerSmoothly();
                break;
            case LEFT:
                playerX -= playerSpeed;
                movePlayerSmoothly();
                break;
            case RIGHT:
                playerX += playerSpeed;
                movePlayerSmoothly();
                break;
            default:
                System.out.println("Falsche Taste");
        }
    }

    // Methode für flüssige Bewegung der Spielfigur
    private void movePlayerSmoothly() {
        // Überprüfen, ob die Spielfigur innerhalb des Spielfelds bleibt
        if (playerX < 0) playerX = 0;
        if (playerX > WIDTH * TILE_SIZE - TILE_SIZE) playerX = WIDTH * TILE_SIZE - TILE_SIZE;
        if (playerY < 0) playerY = 0;
        if (playerY > HEIGHT * TILE_SIZE - TILE_SIZE) playerY = HEIGHT * TILE_SIZE - TILE_SIZE;

        // Stoppe die Animation, falls sie bereits läuft
        if (playerTransition.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            playerTransition.stop();
        }

        // Setze die Zielposition und die Dauer der Animation
        playerTransition.setToX(playerX);
        playerTransition.setToY(playerY);
        playerTransition.setDuration(Duration.millis(100)); // Dauer der Animation anpassen
        playerTransition.play(); // Animation abspielen
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


