import javafx.scene.input.KeyEvent;

public class Controller {

    private TetriJump game;

    // Konstruktor, der das Spielobjekt erhält
    public Controller(TetriJump game) {
        this.game = game;
    }

    // Methode, um die Tastenanschläge zu behandeln
    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
                game.getCurrentTetromino().rotate();
                break;
            case A:
                game.getCurrentTetromino().moveLeft();
                break;
            case D:
                game.getCurrentTetromino().moveRight();
                break;
            case S:
                game.getCurrentTetromino().moveDown();
                break;
            case M:
                game.InGameMenu(game.getRoot()); // Öffnet das In-Game Menü
                game.menuIsOpen = true;
                break;
            default:
                System.out.println("Falsche Taste");
        }
    }
}
