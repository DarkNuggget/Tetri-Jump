import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;  // Füge dies hinzu
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
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;


public class TetriJump {

    private Scene gameScene;
    private Stage primaryStage;
    private TetriGui app;
    private MusikPlayer musikPlayer;
    private StartScreen startScreen;
    public boolean menuIsOpen = false;
    private static final int TILE_SIZE = 30;
    private Controller controller; // Controller-Instanz

    private int WIDTH = 20;
    private int HEIGHT = 22;
    private Color[][] grid = new Color[HEIGHT][WIDTH];
    private Timeline gameLoop;
    private Tetromino currentTetromino;

    public TetriJump(Stage primaryStage, TetriGui app) {
        this.primaryStage = primaryStage;
        this.app = app;
        this.musikPlayer = new MusikPlayer(); // MusikPlayer instanziieren
        this.controller = new Controller(this); // Controller initialisieren
        createGame(primaryStage, 20, 22);
    }

    public void createGame(Stage primaryStage, int width, int height) {
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        gameScene = new Scene(root, width * TILE_SIZE, height * TILE_SIZE);
        gameScene.setOnKeyPressed(event -> controller.handleKeyPress(event)); // Tasteneingabe an den Controller weiterleiten

        startGame(gc);
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

    // In-Game Menü als Overlay
  

    // Andere Attribute und Methoden ...

     public void InGameMenu(Pane gameRoot) {
        if (menuIsOpen == false) { 
          
         // end of while
        // VBox für das Menü
        VBox modeRoot = new VBox(30);
        modeRoot.setAlignment(Pos.CENTER);

        // Setze den Hintergrund des Menüs transparent und mit einem halbtransparenten schwarzen Overlay
        modeRoot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // 70% Transparenz

        // Titeltext für das Menü
        Text menuTitle = new Text("In-Game Menu");
        menuTitle.setFont(new Font(30));
        menuTitle.setFill(Color.WHITE);

        // Resume-Button
        Button resumeButton = new Button("Resume Game");
        resumeButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        resumeButton.setOnAction(event -> {
         gameRoot.getChildren().remove(modeRoot);
         menuIsOpen = false;  
        }); // Entferne das Menü

        // Options-Button
        Button optionsButton = new Button("Options");
        optionsButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        optionsButton.setOnAction(event -> musikPlayer.showOptionsWindow());

        // Main-Menu Button
        Button mainButton = new Button("Main Menu");
        mainButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        mainButton.setOnAction(event -> {;
            menuIsOpen = false; 
            musikPlayer.stopMusik(); // Falls Musik gestoppt werden muss
            app.showStartScreen();  
        });

        // Füge alle Buttons zum Menü hinzu
        modeRoot.getChildren().addAll(menuTitle, resumeButton, optionsButton, mainButton);

        // Füge das Menü als Overlay zum Spielfeld hinzu
        gameRoot.getChildren().add(modeRoot);

        // Animation, um das Menü von der Seite hereinzuschieben
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), modeRoot);
        transition.setFromX(600);  // Startet außerhalb des Bildschirms
        transition.setToX(0);      // Bewegt es auf die ursprüngliche Position
        transition.play();
        
    } else {
        System.out.println("Menu ist schon offen");
      } // end of if-else
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
                for (int row = y; row > 0; row--) {
                    grid[row] = grid[row - 1].clone();
                }
                grid[0] = new Color[WIDTH];
            }
        }
    }
    public void setMenuIsOpen(boolean menuIsOpen) {
        this.menuIsOpen = menuIsOpen;
  }
   

    public Scene getGameScene() {
        return this.gameScene;
    }
  
    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public Pane getRoot() {
        return (Pane) gameScene.getRoot();
    }
}

