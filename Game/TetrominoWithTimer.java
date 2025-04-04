import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TetrominoWithTimer {
    private Tetromino tetromino;
    private Timeline despawnTimer;
    private boolean isBlinking = false;
    private long spawnTime;

    public TetrominoWithTimer(Tetromino tetromino) {
        this.tetromino = tetromino;
        this.spawnTime = System.currentTimeMillis();
        
        despawnTimer = new Timeline();
        despawnTimer.getKeyFrames().add(new javafx.animation.KeyFrame(Duration.seconds(3), e -> isBlinking = true));
        despawnTimer.getKeyFrames().add(new javafx.animation.KeyFrame(Duration.seconds(5), e -> despawn()));
        despawnTimer.setCycleCount(1);
        despawnTimer.play();
    }

    private void despawn() {
        tetromino = null;
    }

    public void render(GraphicsContext gc, double yPosition, int tileSize) {
        if (tetromino == null) return;

        int[][] shape = tetromino.getShape();
        int tetroX = tetromino.getX();
        Color color = tetromino.getColor();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    if (isBlinking && (System.currentTimeMillis() % 500 < 250)) {
                        gc.setFill(color.deriveColor(0, 1, 1, 0.5));
                    } else {
                        gc.setFill(color);
                    }
                    gc.fillRect((tetroX + col) * tileSize, yPosition + (row * tileSize), tileSize, tileSize);
                }
            }
        }
    }

    public Tetromino getTetromino() { return tetromino; }
    public boolean isDespawned() { return tetromino == null; } // Sicherstellen, dass diese Methode da ist
    public void stopTimer() { despawnTimer.stop(); }
}