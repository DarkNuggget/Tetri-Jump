import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class AIController {
    private TetriAutoGame game;
    private Tetromino currentTetromino;

    public AIController(TetriAutoGame game) {
        this.game = game;
    }

    public void startAI() {
        AnimationTimer aiLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentTetromino == null || !game.getCurrentTetromino().equals(currentTetromino)) {
                    currentTetromino = game.getCurrentTetromino();
                    makeBestMove();
                }
            }
        };
        aiLoop.start();
    }

    private void makeBestMove() {
        Color[][] grid = game.getGrid();
        int bestX = 0;
        int bestRotation = 0;
        double bestScore = Double.NEGATIVE_INFINITY;

        // alle möglichen rotationen
        for (int rotation = 0; rotation < 4; rotation++) {
            Tetromino testTetromino = copyTetromino(currentTetromino);
            for (int r = 0; r < rotation; r++) {
                testTetromino.rotate();
            }

            // alle mögliche positionen
            for (int x = -testTetromino.getWidth(); x < TetriJump.WIDTH; x++) {
                Tetromino temp = copyTetromino(testTetromino);
                temp.setPosition(x, 0);

                while (game.canMove(temp, 0, 1)) {
                    temp.moveDown();
                }

                double score = evaluatePosition(temp, grid);
                if (score > bestScore) {
                    bestScore = score;
                    bestX = x;
                    bestRotation = rotation;
                }
            }
        }

        // Execute the best move
        executeMove(bestX, bestRotation);
    }

    private void executeMove(int targetX, int targetRotation) {
        Tetromino tetromino = game.getCurrentTetromino();

        // Rotate to the target rotation
        for (int i = 0; i < targetRotation; i++) {
            tetromino.rotate();
        }

        // Move to the target X position
        int deltaX = targetX - tetromino.getX();
        if (deltaX > 0) {
            for (int i = 0; i < deltaX; i++) {
                tetromino.moveRight();
            }
        } else if (deltaX < 0) {
            for (int i = 0; i < -deltaX; i++) {
                tetromino.moveLeft();
            }
        }

        // Drop the tetromino
        while (game.canMove(tetromino, 0, 1)) {
            tetromino.moveDown();
        }
        game.fixTetromino(tetromino);
    }

    private double evaluatePosition(Tetromino tetromino, Color[][] grid) {
        int rowsCleared = calculateRowsCleared(tetromino, grid);
        int holes = calculateHoles(tetromino, grid);
        int height = calculateAggregateHeight(tetromino, grid);

        // Weight parameters
        double weightRowsCleared = 10.0;
        double weightHoles = -5.0;
        double weightHeight = -1.0;

        return rowsCleared * weightRowsCleared + holes * weightHoles + height * weightHeight;
    }

    private int calculateRowsCleared(Tetromino tetromino, Color[][] grid) {
        // Simulate adding the tetromino to the grid and count full rows
        Color[][] tempGrid = copyGrid(grid);
        tetromino.fixToGrid(tempGrid);
        int rowsCleared = 0;

        for (int y = 0; y < tempGrid.length; y++) {
            boolean fullRow = true;
            for (int x = 0; x < tempGrid[0].length; x++) {
                if (tempGrid[y][x] == null) {
                    fullRow = false;
                    break;
                }
            }
            if (fullRow) {
                rowsCleared++;
            }
        }
        return rowsCleared;
    }

    private int calculateHoles(Tetromino tetromino, Color[][] grid) {
        // Count empty cells below filled cells in each column
        Color[][] tempGrid = copyGrid(grid);
        tetromino.fixToGrid(tempGrid);
        int holes = 0;

        for (int x = 0; x < tempGrid[0].length; x++) {
            boolean blockFound = false;
            for (int y = 0; y < tempGrid.length; y++) {
                if (tempGrid[y][x] != null) {
                    blockFound = true;
                } else if (blockFound) {
                    holes++;
                }
            }
        }
        return holes;
    }

    private int calculateAggregateHeight(Tetromino tetromino, Color[][] grid) {
        // Calculate the total column heights
        Color[][] tempGrid = copyGrid(grid);
        tetromino.fixToGrid(tempGrid);
        int height = 0;

        for (int x = 0; x < tempGrid[0].length; x++) {
            for (int y = 0; y < tempGrid.length; y++) {
                if (tempGrid[y][x] != null) {
                    height += tempGrid.length - y;
                    break;
                }
            }
        }
        return height;
    }

    private Tetromino copyTetromino(Tetromino tetromino) {
        return new Tetromino(tetromino.getShape(), tetromino.getX(), tetromino.getY(), tetromino.getColor());
    }

    private Color[][] copyGrid(Color[][] grid) {
        Color[][] copy = new Color[grid.length][grid[0].length];
        for (int y = 0; y < grid.length; y++) {
            System.arraycopy(grid[y], 0, copy[y], 0, grid[0].length);
        }
        return copy;
    }
}
