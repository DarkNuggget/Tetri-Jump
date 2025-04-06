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
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

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
    private double cameraYPosition = 0;
    private double tetrominoFallSpeed = 2.5;
    private double tetrominoYPosition = 0;
    private Pane root;  // Instanzvariable für das Pane
    private boolean hasFallen = false; // Gibt an, ob der Spieler durch den Boden gefallen ist
    private boolean lastCollisionState = false;
    public int score = 0;
    private Timeline scoreTimer;
    public static boolean gameOver = false; // NEU ganz oben in der Klasse
    public boolean tot = false;
    private int tetriCoins;
  
    public TetriAutoGame(Stage primaryStage, TetriGui app) {
        this.primaryStage = primaryStage;
        this.app = app;
        createGame(primaryStage, WIDTH, HEIGHT);
        startScoreCounter();
        gameOver = false;
    }

    public void createGame(Stage primaryStage, int width, int height) {
        Pane root = new Pane();  // Dein Pane als Container für die gesamte Szene
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        gameScene = new Scene(root, width * TILE_SIZE, height * TILE_SIZE);
        gameScene.setOnKeyPressed(event -> handleKeyPress(event));
        gameScene.setOnKeyReleased(event -> player.handleKeyRelease(event));

        // Spieler initialisieren
        String skinPath = loadSkinPathFromFile();
        Skin loadedSkin = new Skin("GeladenerSkin", skinPath, true);
        player = new Player(grid, loadedSkin, primaryStage);

        // Spieler ImageView für das Bild (falls noch benötigt)
        ImageView playerImageView = loadPlayerImage(skinPath);
        root.getChildren().add(playerImageView);

        // Spawn-Plattform erstellen (I-Tetromino am Boden)
        Tetromino spawnPlatform = new Tetromino(new int[][]{{1, 1, 1, 1}}, WIDTH / 2 - 2, HEIGHT - 1, Color.GRAY);
        fixTetromino(spawnPlatform);

        // Timer für Plattform-Despawn
        Timeline platformTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            removeSpawnPlatform(spawnPlatform);
        }));
        platformTimer.setCycleCount(1);
        platformTimer.play();

        // Spieler auf Plattform spawnen
        player.setPlayerPosition((WIDTH / 2 - 1) * TILE_SIZE, (HEIGHT - 2) * TILE_SIZE);

        spawnRandomTetromino();

        startGame(gc);
    }

    // Neue Methode zum Laden des Spielerbildes
    private ImageView loadPlayerImage(String skinPath) {
        Image playerImage = new Image("file:" + skinPath); // Spielerbild laden
        ImageView playerImageView = new ImageView(playerImage);
        return playerImageView; // Rückgabe des ImageView
    }

    private String loadSkinPathFromFile() {
        File file = new File("Config/Skin.txt");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    return scanner.nextLine();
                }
            } catch (IOException e) {
                System.err.println("Fehler beim Laden des Skin-Pfads: " + e.getMessage());
            }
        }
        return "Skins/Standard.png"; // Fallback, falls Datei fehlt oder Fehler
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

        // Wenn die Y-Position den Boden erreicht, wird diese Position immer als frei angesehen
        if (gridY == HEIGHT - 1) {
            return true; // Boden ist immer frei, keine Kollisionsprüfung
        }

        // Überprüfe, ob der Punkt im Grid leer ist und keine Kollision mit Tetrominos besteht
        return grid[gridY][gridX] == null && !isCollidingWithTetromino(gridX, gridY);
    }

    private boolean isCollidingWithTetromino(int gridX, int gridY) {
        if (currentTetromino == null || currentTetromino.isDespawned()) {
            return false; // Kein Tetromino vorhanden oder despawnt
        }

        Tetromino tetromino = currentTetromino.getTetromino();

        // Kollision nur mit Tetrominos, der Boden wird ignoriert
        if (tetromino.getY() == HEIGHT - 1) {
            return false; // Boden wird NICHT als Tetromino betrachtet
        }

        int[] hitbox = tetromino.getHitbox();
        int tetroLeft = hitbox[0];
        int tetroTop = hitbox[1];
        int tetroRight = hitbox[2];
        int tetroBottom = hitbox[3];

        // Überprüfe, ob der Punkt im Bereich des Tetrominos liegt
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
        gameOver = false;
        score = 0;
        saveScoreToFile();
        loadTetriCoinsFromFile();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        // Bestehende Render-Logik
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
        renderScore(gc);
    }

    private void renderPlayer(GraphicsContext gc) {
        String imagePath = player.getSkin().getImagePath();
        try {
            Image skinImage = new Image(imagePath);

            // Speichere den aktuellen Status
            gc.save();

            // Verschiebe den Ursprung zum Zentrum des Spielers, um korrekt zu spiegeln
            double playerCenterX = player.getPlayerX() + TILE_SIZE / 2;
            gc.translate(playerCenterX, 0); // Verschiebe zum Zentrum

            if (player.isMirrored()) {
                gc.scale(-1, 1); // Spiegeln horizontal (X-Achse)
                gc.drawImage(skinImage, -TILE_SIZE / 2, player.getPlayerY(), TILE_SIZE, TILE_SIZE);
            } else {
                gc.drawImage(skinImage, -TILE_SIZE / 2, player.getPlayerY(), TILE_SIZE, TILE_SIZE);
            }

            // Stelle den ursprünglichen Status wieder her
            gc.restore();

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
            case A:
                player.setMirrored(true); // Setze Spiegelsatus auf true (spiegeln)
                player.handleKeyPress(event); // Rufe die Bewegung des Players auf
                break;
            case D:
                player.setMirrored(false); // Setze Spiegelsatus auf false (normal)
                player.handleKeyPress(event); // Rufe die Bewegung des Players auf
                break;
            default:
                player.handleKeyPress(event); // Standardmäßig die Bewegung des Players aufrufen
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

        // Überprüfen, ob der Spieler den Boden überschreitet und weiter nach unten fällt
        if (player.getPlayerY() < HEIGHT * TILE_SIZE) {
            player.setPlayerPosition(player.getPlayerX(), player.getPlayerY() + tetrominoFallSpeed);
        } else {
            player.setPlayerPosition(player.getPlayerX(), HEIGHT * TILE_SIZE + (player.getPlayerY() - HEIGHT * TILE_SIZE));
            hasFallen = true;  // Der Spieler ist jetzt gefallen
        }

        // Die Kamera folgt dem Spieler, wenn er gefallen ist
        if (hasFallen) {
            updateCameraPosition();  // Kamera folgt dem Spieler
        }

        if (player.newY >= 630) {
            fixTetromino(tetromino);
            currentTetromino.stopTimer();
            gameLoop.stop();
            scoreTimer.stop();
            System.out.println("Game Over: Tetromino hat den Spieler getroffen!");
            ScoreCoinsRechner();
            saveTetriCoinsToFile();
            // Schließe das Fenster (primaryStage)
            //        primaryStage.close();  // Das Schließt das Fenster, ohne das Programm zu beenden
        }

        if (isTetrominoCollidingWithPlayer(tetromino)) {
            fixTetromino(tetromino);
            currentTetromino.stopTimer();
            gameLoop.stop();
            scoreTimer.stop();
            System.out.println("Game Over: Tetromino hat den Spieler getroffen!");
            ScoreCoinsRechner();
            saveTetriCoinsToFile();
            player.goToHeaven(primaryStage);
        } // end of if

        if (canMove(tetromino, 0, 1)) {
            // Position wird automatisch aktualisiert
        } else {
            fixTetromino(tetromino);
            currentTetromino.stopTimer();
            clearFullRows();
            spawnRandomTetromino();
            tetrominoYPosition = currentTetromino.getTetromino().getY() * TILE_SIZE;
        }
    }

    private void updateCameraPosition() {
        // Die Kamera folgt dem Spieler nur, wenn er gefallen ist
        if (hasFallen) {
            cameraYPosition = player.getPlayerY() - (HEIGHT * TILE_SIZE) / 2;

            // Sicherstellen, dass die Kamera nicht unter den Boden geht
            if (cameraYPosition > 0) {
                cameraYPosition = 0;  // Die Kamera soll nicht über den Spielfeld-Boden hinausgehen
            }

            root.setTranslateY(cameraYPosition);  // Verschiebt das "root"-Pane basierend auf der Kamera-Position
        }
    }

    private void updatePlayerPosition() {
        double playerY = player.getPlayerY();

        // Falls der Spieler den Boden überschreitet, setze ihn auf die unterste Position
        if (playerY >= (HEIGHT - 1) * TILE_SIZE) {
            // Spieler durch den Boden fallen lassen
            fallThroughGround();
        }
    }

    private void fallThroughGround() {
        System.out.println("Der Spieler ist durch den Boden gefallen!");

        // Optional: Du kannst den Spieler zurücksetzen oder andere Logik anwenden
        //    spawnPlayerRandomly();

        // Kamera soll nach unten folgen
        updateCameraPosition();  // Kamera wird nach unten bewegt, wenn der Spieler durch den Boden fällt
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

    private void startScoreCounter() {
        score = 0;
        if (gameOver) return;

        scoreTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            score++;  // Score erhöhen
            saveScoreToFile(); // Speichere den Score
        }));
        scoreTimer.setCycleCount(Timeline.INDEFINITE);
        scoreTimer.play();
    }

    private void saveScoreToFile() {
        if (gameOver) return; // Kein Schreiben mehr, wenn Spiel vorbei

        // Hier sollte auch immer geschrieben werden, wenn der Score sich ändert
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Config/score.txt"))) {
            writer.write(" " + score);  // Score wird in die Datei gespeichert
//            System.out.println("Score gespeichert: " + score);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern des Scores: " + e.getMessage());
        }
    }

    private void renderScore(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(20));

        // Score immer oben rechts anzeigen
        double scoreX = WIDTH * TILE_SIZE - 100; // Abstand von rechts
        gc.fillText("Score: " + score, scoreX, 20);
    }
  
    public void loadTetriCoinsFromFile() {
    File file = new File("Config/TetriCoins.txt");
    if (file.exists()) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                tetriCoins = Integer.parseInt(scanner.nextLine().trim()); // TetriCoins aus der Datei lesen
                System.out.println("Geladene TetriCoins: " + tetriCoins);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der TetriCoins: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Fehler beim Konvertieren der TetriCoins: " + e.getMessage());
        }
    } else {
        System.out.println("Keine TetriCoins-Konfiguration gefunden. Standardwert wird verwendet.");
    }
} 
  
   public void saveTetriCoinsToFile() {
    try {
        File configDir = new File("Config");
        if (!configDir.exists()) {
            configDir.mkdir(); // Ordner erstellen, falls nicht vorhanden
        }

        FileWriter writer = new FileWriter("Config/TetriCoins.txt");
        writer.write(String.valueOf(tetriCoins)); // Speichern der TetriCoins
        writer.close();
        System.out.println("TetriCoins wurden in Config/TetriCoins.txt gespeichert.");
    } catch (IOException e) {
        System.err.println("Fehler beim Speichern der TetriCoins: " + e.getMessage());
    }
}
  
  public void ScoreCoinsRechner(){
    int plusCoins = 0;
    plusCoins = score / 3;
    
    tetriCoins = tetriCoins + plusCoins;
    System.out.println("plusCoins: " + plusCoins);
    System.out.println("neu TetriCoins: " + tetriCoins);
  }



    public int getScore() {
        return score;
    }

    public boolean getTot() {
        return tot;
    }

    public Scene getGameScene() {
        return this.gameScene;
    }

    public void setGameOver(boolean igameOver) {
        gameOver = igameOver;
    }
}