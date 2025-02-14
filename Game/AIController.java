import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

public class AIController {
    private TetriAutoGame game;
    private Tetromino currentTetromino;
    private Color[][] grid;

    public AIController(TetriAutoGame game) {
        this.game = game;
    }

    public void startAI() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);    //2 sek bis zum neu spawnen
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        
                currentTetromino = game.getCurrentTetromino();
                grid = game.getGrid();

                AIMove bestMove = calculateBestMove();
                executeMove(bestMove);
            }
        }).start();
    }

    private AIMove calculateBestMove() {
        List<AIMove> possibleMoves = new ArrayList<>();

        //0°, 90°, 180°, 270°
        for (int rotation = 0; rotation < 4; rotation++) {
            Tetromino simulatedTetromino = cloneTetromino(currentTetromino);

            // rptate
            for (int r = 0; r < rotation; r++) {
                if (simulatedTetromino.canRotate(grid)) {
                    simulatedTetromino.rotate();
                }
            }

            // horizontal (funktioniert nicht vollständig)
            for (int x = -simulatedTetromino.getWidth(); x < game.WIDTH; x++) {
                Tetromino testTetromino = cloneTetromino(simulatedTetromino);
                testTetromino.setPosition(x, testTetromino.getY());

                // fall animation
                while (game.canMove(testTetromino, 0, 1)) {
                    testTetromino.moveDown();
                }

                int score = evaluatePosition(testTetromino);
                possibleMoves.add(new AIMove(x, rotation, score));
            }
        }

        AIMove bestMove = possibleMoves.get(0);
        for (AIMove move : possibleMoves) {
            if (move.score > bestMove.score) {
                bestMove = move;
            }
        }

        return bestMove;
    }
  
  //Simulation
    private int evaluatePosition(Tetromino tetromino) {
        Color[][] simulatedGrid = cloneGrid(grid);
        tetromino.fixToGrid(simulatedGrid);
    
        int score = 0;
    
        // 1. anzahl voller reihen (fehler)
        score += countFullRows(simulatedGrid) * 1000;
        // 2. höhe der säule (fehler)
        score -= getMaxHeight(simulatedGrid) * 10;
        // 3. holes anzahl
        score -= countHoles(simulatedGrid) * 50;
        // 4. roughness (fehler)
        score -= calculateRoughness(simulatedGrid) * 20;
        // 5. Potenzielle Löcher
        score -= countPotentialHoles(simulatedGrid) * 30;
    
        return score;
    }
  
    //unnötig
    private int countFullRows(Color[][] grid) {
        int fullRows = 0;
        for (int y = 0; y < grid.length; y++) {
            boolean isFull = true;
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == null) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullRows++;
            }
        }
        return fullRows;
    }

    private int getMaxHeight(Color[][] grid) {
        int maxHeight = 0;
        for (int x = 0; x < grid[0].length; x++) {
            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x] != null) {
                    maxHeight = Math.max(maxHeight, grid.length - y);
                    break;
                }
            }
        }
        return maxHeight;
    }

    private int countHoles(Color[][] grid) {
        int holes = 0;
        for (int x = 0; x < grid[0].length; x++) {
            boolean foundBlock = false;
            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x] != null) {
                    foundBlock = true;
                } else if (foundBlock) {
                    holes++;
                }
            }
        }
        return holes;
    }

    private void executeMove(AIMove move) {
        for (int r = 0; r < move.rotation; r++) {
            if (currentTetromino.canRotate(grid)) {
                currentTetromino.rotate();
            }
        }

        //zu bester positon bewegen
        int targetX = move.x;
        while (currentTetromino.getX() != targetX) {
            if (currentTetromino.getX() < targetX && game.canMove(currentTetromino, 1, 0)) {
                currentTetromino.moveRight();
            } else if (currentTetromino.getX() > targetX && game.canMove(currentTetromino, -1, 0)) {
                currentTetromino.moveLeft();
            } else {
                break;
            }
        }

        //fall animation
        while (game.canMove(currentTetromino, 0, 1)) {
            currentTetromino.moveDown();
        }
    }

    private Tetromino cloneTetromino(Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        int x = tetromino.getX();
        int y = tetromino.getY();
        Color color = tetromino.getColor();
        return new Tetromino(shape, x, y, color);
    }

    private Color[][] cloneGrid(Color[][] grid) {
        Color[][] newGrid = new Color[grid.length][grid[0].length];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                newGrid[y][x] = grid[y][x];
            }
        }
        return newGrid;
    }

    private static class AIMove {  //muss  noch ausgelagert werden in einzelnes dokument
        int x;          // Ziel-X-Position
        int rotation;   // Anzahl der Rotationen
        int score;      // Bewertung des Zuges

        AIMove(int x, int rotation, int score) {
            this.x = x;
            this.rotation = rotation;
            this.score = score;
        }
    }
  
    private int calculateRoughness(Color[][] grid) {
        int roughness = 0;
        for (int x = 1; x < grid[0].length; x++) {
            int heightLeft = getColumnHeight(grid, x - 1);
            int heightRight = getColumnHeight(grid, x);
            roughness += Math.abs(heightLeft - heightRight);
        }
        return roughness;
    }

    private int getColumnHeight(Color[][] grid, int x) {
        for (int y = 0; y < grid.length; y++) {
            if (grid[y][x] != null) {
                return grid.length - y;
            }
        }
        return 0;
    }
  
    private int countPotentialHoles(Color[][] grid) {
      int potentialHoles = 0;
      for (int x = 0; x < grid[0].length; x++) {
          boolean foundBlock = false;
          for (int y = 0; y < grid.length; y++) {
              if (grid[y][x] != null) {
                  foundBlock = true;
              } else if (foundBlock) {
                  // Überprüfe, ob das Loch vermeidbar ist
                  if (y < grid.length - 1 && grid[y + 1][x] == null) {
                      potentialHoles++;
                  }
              }
          }
      }
      return potentialHoles;
  }
}
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

public class AIController {
    private TetriAutoGame game;
    private Tetromino currentTetromino;
    private Color[][] grid;

    public AIController(TetriAutoGame game) {
        this.game = game;
    }

    public void startAI() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);    //2 sek bis zum neu spawnen
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        
                currentTetromino = game.getCurrentTetromino();
                grid = game.getGrid();

                AIMove bestMove = calculateBestMove();
                executeMove(bestMove);
            }
        }).start();
    }

    private AIMove calculateBestMove() {
        List<AIMove> possibleMoves = new ArrayList<>();

        //0°, 90°, 180°, 270°
        for (int rotation = 0; rotation < 4; rotation++) {
            Tetromino simulatedTetromino = cloneTetromino(currentTetromino);

            // rptate
            for (int r = 0; r < rotation; r++) {
                if (simulatedTetromino.canRotate(grid)) {
                    simulatedTetromino.rotate();
                }
            }

            // horizontal (funktioniert nicht vollständig)
            for (int x = -simulatedTetromino.getWidth(); x < game.WIDTH; x++) {
                Tetromino testTetromino = cloneTetromino(simulatedTetromino);
                testTetromino.setPosition(x, testTetromino.getY());

                // fall animation
                while (game.canMove(testTetromino, 0, 1)) {
                    testTetromino.moveDown();
                }

                int score = evaluatePosition(testTetromino);
                possibleMoves.add(new AIMove(x, rotation, score));
            }
        }

        AIMove bestMove = possibleMoves.get(0);
        for (AIMove move : possibleMoves) {
            if (move.score > bestMove.score) {
                bestMove = move;
            }
        }

        return bestMove;
    }
  
  //Simulation
    private int evaluatePosition(Tetromino tetromino) {
        Color[][] simulatedGrid = cloneGrid(grid);
        tetromino.fixToGrid(simulatedGrid);
    
        int score = 0;
    
        // 1. anzahl voller reihen (fehler)
        score += countFullRows(simulatedGrid) * 1000;
        // 2. höhe der säule (fehler)
        score -= getMaxHeight(simulatedGrid) * 10;
        // 3. holes anzahl
        score -= countHoles(simulatedGrid) * 50;
        // 4. roughness (fehler)
        score -= calculateRoughness(simulatedGrid) * 20;
        // 5. Potenzielle Löcher
        score -= countPotentialHoles(simulatedGrid) * 30;
    
        return score;
    }
  
    //unnötig
    private int countFullRows(Color[][] grid) {
        int fullRows = 0;
        for (int y = 0; y < grid.length; y++) {
            boolean isFull = true;
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == null) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullRows++;
            }
        }
        return fullRows;
    }

    private int getMaxHeight(Color[][] grid) {
        int maxHeight = 0;
        for (int x = 0; x < grid[0].length; x++) {
            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x] != null) {
                    maxHeight = Math.max(maxHeight, grid.length - y);
                    break;
                }
            }
        }
        return maxHeight;
    }

    private int countHoles(Color[][] grid) {
        int holes = 0;
        for (int x = 0; x < grid[0].length; x++) {
            boolean foundBlock = false;
            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x] != null) {
                    foundBlock = true;
                } else if (foundBlock) {
                    holes++;
                }
            }
        }
        return holes;
    }

    private void executeMove(AIMove move) {
        for (int r = 0; r < move.rotation; r++) {
            if (currentTetromino.canRotate(grid)) {
                currentTetromino.rotate();
            }
        }

        //zu bester positon bewegen
        int targetX = move.x;
        while (currentTetromino.getX() != targetX) {
            if (currentTetromino.getX() < targetX && game.canMove(currentTetromino, 1, 0)) {
                currentTetromino.moveRight();
            } else if (currentTetromino.getX() > targetX && game.canMove(currentTetromino, -1, 0)) {
                currentTetromino.moveLeft();
            } else {
                break;
            }
        }

        //fall animation
        while (game.canMove(currentTetromino, 0, 1)) {
            currentTetromino.moveDown();
        }
    }

    private Tetromino cloneTetromino(Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        int x = tetromino.getX();
        int y = tetromino.getY();
        Color color = tetromino.getColor();
        return new Tetromino(shape, x, y, color);
    }

    private Color[][] cloneGrid(Color[][] grid) {
        Color[][] newGrid = new Color[grid.length][grid[0].length];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                newGrid[y][x] = grid[y][x];
            }
        }
        return newGrid;
    }

    private static class AIMove {  //muss  noch ausgelagert werden in einzelnes dokument
        int x;          // Ziel-X-Position
        int rotation;   // Anzahl der Rotationen
        int score;      // Bewertung des Zuges

        AIMove(int x, int rotation, int score) {
            this.x = x;
            this.rotation = rotation;
            this.score = score;
        }
    }
  
    private int calculateRoughness(Color[][] grid) {
        int roughness = 0;
        for (int x = 1; x < grid[0].length; x++) {
            int heightLeft = getColumnHeight(grid, x - 1);
            int heightRight = getColumnHeight(grid, x);
            roughness += Math.abs(heightLeft - heightRight);
        }
        return roughness;
    }

    private int getColumnHeight(Color[][] grid, int x) {
        for (int y = 0; y < grid.length; y++) {
            if (grid[y][x] != null) {
                return grid.length - y;
            }
        }
        return 0;
    }
  
    private int countPotentialHoles(Color[][] grid) {
      int potentialHoles = 0;
      for (int x = 0; x < grid[0].length; x++) {
          boolean foundBlock = false;
          for (int y = 0; y < grid.length; y++) {
              if (grid[y][x] != null) {
                  foundBlock = true;
              } else if (foundBlock) {
                  // Überprüfe, ob das Loch vermeidbar ist
                  if (y < grid.length - 1 && grid[y + 1][x] == null) {
                      potentialHoles++;
                  }
              }
          }
      }
      return potentialHoles;
  }
}
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

public class AIController {
    private TetriAutoGame game;
    private Tetromino currentTetromino;
    private Color[][] grid;

    public AIController(TetriAutoGame game) {
        this.game = game;
    }

    public void startAI() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);    //2 sek bis zum neu spawnen
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        
                currentTetromino = game.getCurrentTetromino();
                grid = game.getGrid();

                AIMove bestMove = calculateBestMove();
                executeMove(bestMove);
            }
        }).start();
    }

    private AIMove calculateBestMove() {
        List<AIMove> possibleMoves = new ArrayList<>();

        //0°, 90°, 180°, 270°
        for (int rotation = 0; rotation < 4; rotation++) {
            Tetromino simulatedTetromino = cloneTetromino(currentTetromino);

            // rptate
            for (int r = 0; r < rotation; r++) {
                if (simulatedTetromino.canRotate(grid)) {
                    simulatedTetromino.rotate();
                }
            }

            // horizontal (funktioniert nicht vollständig)
            for (int x = -simulatedTetromino.getWidth(); x < game.WIDTH; x++) {
                Tetromino testTetromino = cloneTetromino(simulatedTetromino);
                testTetromino.setPosition(x, testTetromino.getY());

                // fall animation
                while (game.canMove(testTetromino, 0, 1)) {
                    testTetromino.moveDown();
                }

                int score = evaluatePosition(testTetromino);
                possibleMoves.add(new AIMove(x, rotation, score));
            }
        }

        AIMove bestMove = possibleMoves.get(0);
        for (AIMove move : possibleMoves) {
            if (move.score > bestMove.score) {
                bestMove = move;
            }
        }

        return bestMove;
    }
  
  //Simulation
    private int evaluatePosition(Tetromino tetromino) {
        Color[][] simulatedGrid = cloneGrid(grid);
        tetromino.fixToGrid(simulatedGrid);
    
        int score = 0;
    
        // 1. anzahl voller reihen (fehler)
        score += countFullRows(simulatedGrid) * 1000;
        // 2. höhe der säule (fehler)
        score -= getMaxHeight(simulatedGrid) * 10;
        // 3. holes anzahl
        score -= countHoles(simulatedGrid) * 50;
        // 4. roughness (fehler)
        score -= calculateRoughness(simulatedGrid) * 20;
        // 5. Potenzielle Löcher
        score -= countPotentialHoles(simulatedGrid) * 30;
    
        return score;
    }
  
    //unnötig
    private int countFullRows(Color[][] grid) {
        int fullRows = 0;
        for (int y = 0; y < grid.length; y++) {
            boolean isFull = true;
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == null) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullRows++;
            }
        }
        return fullRows;
    }

    private int getMaxHeight(Color[][] grid) {
        int maxHeight = 0;
        for (int x = 0; x < grid[0].length; x++) {
            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x] != null) {
                    maxHeight = Math.max(maxHeight, grid.length - y);
                    break;
                }
            }
        }
        return maxHeight;
    }

    private int countHoles(Color[][] grid) {
        int holes = 0;
        for (int x = 0; x < grid[0].length; x++) {
            boolean foundBlock = false;
            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x] != null) {
                    foundBlock = true;
                } else if (foundBlock) {
                    holes++;
                }
            }
        }
        return holes;
    }

    private void executeMove(AIMove move) {
        for (int r = 0; r < move.rotation; r++) {
            if (currentTetromino.canRotate(grid)) {
                currentTetromino.rotate();
            }
        }

        //zu bester positon bewegen
        int targetX = move.x;
        while (currentTetromino.getX() != targetX) {
            if (currentTetromino.getX() < targetX && game.canMove(currentTetromino, 1, 0)) {
                currentTetromino.moveRight();
            } else if (currentTetromino.getX() > targetX && game.canMove(currentTetromino, -1, 0)) {
                currentTetromino.moveLeft();
            } else {
                break;
            }
        }

        //fall animation
        while (game.canMove(currentTetromino, 0, 1)) {
            currentTetromino.moveDown();
        }
    }

    private Tetromino cloneTetromino(Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        int x = tetromino.getX();
        int y = tetromino.getY();
        Color color = tetromino.getColor();
        return new Tetromino(shape, x, y, color);
    }

    private Color[][] cloneGrid(Color[][] grid) {
        Color[][] newGrid = new Color[grid.length][grid[0].length];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                newGrid[y][x] = grid[y][x];
            }
        }
        return newGrid;
    }

    private static class AIMove {  //muss  noch ausgelagert werden in einzelnes dokument
        int x;          // Ziel-X-Position
        int rotation;   // Anzahl der Rotationen
        int score;      // Bewertung des Zuges

        AIMove(int x, int rotation, int score) {
            this.x = x;
            this.rotation = rotation;
            this.score = score;
        }
    }
  
    private int calculateRoughness(Color[][] grid) {
        int roughness = 0;
        for (int x = 1; x < grid[0].length; x++) {
            int heightLeft = getColumnHeight(grid, x - 1);
            int heightRight = getColumnHeight(grid, x);
            roughness += Math.abs(heightLeft - heightRight);
        }
        return roughness;
    }

    private int getColumnHeight(Color[][] grid, int x) {
        for (int y = 0; y < grid.length; y++) {
            if (grid[y][x] != null) {
                return grid.length - y;
            }
        }
        return 0;
    }
  
    private int countPotentialHoles(Color[][] grid) {
      int potentialHoles = 0;
      for (int x = 0; x < grid[0].length; x++) {
          boolean foundBlock = false;
          for (int y = 0; y < grid.length; y++) {
              if (grid[y][x] != null) {
                  foundBlock = true;
              } else if (foundBlock) {
                  // Überprüfe, ob das Loch vermeidbar ist
                  if (y < grid.length - 1 && grid[y + 1][x] == null) {
                      potentialHoles++;
                  }
              }
          }
      }
      return potentialHoles;
  }
}
