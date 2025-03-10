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
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import javafx.animation.TranslateTransition;

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
    private double playerX = 100; // Startposition der Spielfigur, nicht in der Mitte
    private double playerY = 100; // Startposition der Spielfigur, nicht in der Mitte
    private final double playerSpeed = TILE_SIZE / 2; // Verlangsamung: Spielfigur bewegt sich langsamer
    private TranslateTransition playerTransition; // Für flüssige Bewegung

    // Score-Anzeige
    private int score = 0; // Startwert für den Punktestand
    private Text scoreText; // Textobjekt, das den Punktestand anzeigt
    private Timeline scoreIncrementer; // Timeline für den Score-Incrementer

    public TetriJump(Stage primaryStage, TetriGui app) {
        this.primaryStage = primaryStage;
        this.app = app;
        createGame(primaryStage, 20, 22);
    }

    public void createGame(Stage primaryStage, int width, int height) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: black;"); // Hintergrundfarbe

        // Canvas für das Spielfeld
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

        // Score-Text hinzufügen
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE); // Textfarbe für den Score
        scoreText.setFont(Font.font(20)); // Schriftgröße
        scoreText.setX(WIDTH * TILE_SIZE - 100); // Position für den Score (oben rechts)
        scoreText.setY(30); // Position für den Score
        root.getChildren().add(scoreText);

        gameScene = new Scene(root, width * TILE_SIZE, height * TILE_SIZE);
        gameScene.setOnKeyPressed(event -> handleKeyPress(event));

        // Score-Inkrementer (jeden Punkt pro Sekunde hinzufügen)
        scoreIncrementer = new Timeline(new KeyFrame(Duration.seconds(1), e -> incrementScore()));
        scoreIncrementer.setCycleCount(Timeline.INDEFINITE);
        scoreIncrementer.play();

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
                // Wenn eine Reihe voll ist, erhöhen wir den Punktestand
                score += 100; // Beispielweise 100 Punkte pro voll gelöschter Reihe
                updateScoreDisplay(); // Punktestand aktualisieren

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

    // Methode für flüssige Bewegung der Spielfigur mit Teleportation an die gegenüberliegende Seite
    private void movePlayerSmoothly() {
        // Horizontaler Wrap-Around: Wenn der Spieler den rechten Rand erreicht, erscheint er links
        if (playerX < 0) {
            playerX = WIDTH * TILE_SIZE - TILE_SIZE; // Gehe auf die rechte Seite
        } else if (playerX > WIDTH * TILE_SIZE - TILE_SIZE) {
            playerX = 0; // Gehe auf die linke Seite
        }

        // Vertikaler Wrap-Around: Wenn der Spieler den oberen Rand erreicht, erscheint er unten
        if (playerY < 0) {
            playerY = HEIGHT * TILE_SIZE - TILE_SIZE; // Gehe an den unteren Rand
        } else if (playerY > HEIGHT * TILE_SIZE - TILE_SIZE) {
            playerY = 0; // Gehe an den oberen Rand
        }

        // Setze die Position des Spielers sofort, ohne Animation
        player.setX(playerX);
        player.setY(playerY);
    }

    // Methode, um den Punktestand anzuzeigen und zu aktualisieren
    private void updateScoreDisplay() {
        scoreText.setText("Score: " + score); // Aktualisiert den Text im Score-Anzeige
    }

    // Methode, die jede Sekunde den Score um 1 erhöht
    private void incrementScore() {
        score += 1;
        updateScoreDisplay(); // Aktualisiert den Punktestand im UI
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
