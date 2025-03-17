import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.util.Duration;
import java.io.File;

public class StartScreen {
    private TetriGui app;
    private Scene scene;
    private VBox mainMenuRoot;
    private boolean buttonsAdded = false;
    private VBox activePanel = null;

    public StartScreen(TetriGui app) {
        this.app = app;
        createUI();
    }

    private void createUI() {
        if (buttonsAdded) return;

        InGameMenu.musikPlayer.startMenuMusik();
        InGameMenu.musikPlayer.MenuOffen = true;
        
        mainMenuRoot = new VBox(15);
        mainMenuRoot.setAlignment(Pos.CENTER);
        mainMenuRoot.setPrefSize(600, 700);
        
        File backgroundFile = new File("Hintergrund/MainMenuHintergrund.jpg");
        String bgUri = backgroundFile.toURI().toString();
        mainMenuRoot.setBackground(new Background(new BackgroundImage(
                new Image(bgUri, 600, 700, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        
        Text title = new Text("TetriJump");
        title.setFont(Font.font("Arial", 50));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");
        
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-padding: 20px; -fx-background-radius: 10px;");
        
        Button startButton = createStyledButton("Start Game", event -> showModeSelection());
        Button optionsButton = createStyledButton("Options", event -> showVolumeSettings());
        Button shopButton = createStyledButton("Shop", e -> {
            InGameMenu.musikPlayer.stoppeAktuelleMusik();
            InGameMenu.musikPlayer.startShopMusik();
            app.openShop();
        });
        Button exitButton = createStyledButton("Exit", event -> System.exit(0));
        
        buttonContainer.getChildren().addAll(startButton, optionsButton, shopButton, exitButton);
        mainMenuRoot.getChildren().addAll(title, buttonContainer);
        
        scene = new Scene(mainMenuRoot, 600, 700);
        buttonsAdded = true;
    }

    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: #222222; -fx-border-color: #00FFFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 10px 20px; -fx-font-family: 'Press Start 2P';");
        button.setMinWidth(200);
        button.setOnAction(action);
        
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: #00FFFF; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 10px 20px; -fx-font-family: 'Press Start 2P';"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: #222222; -fx-border-color: #00FFFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-padding: 10px 20px; -fx-font-family: 'Press Start 2P';"));
    
        return button;
    }

    private void showModeSelection() {
        openPanel("Select a Game Mode", new String[]{"Classic Tetris", "Jump Tetris"}, mode -> {
            InGameMenu.musikPlayer.MenuOffen = false;
            InGameMenu.musikPlayer.startGameMusik();
            app.startGameWithMode(mode);
        });
    }

    private void showVolumeSettings() {
        VBox panel = createPanel("Adjust Volume");
        Slider volumeSlider = new Slider(0, 100, LautstaerkeEinstellungen.loadVolumeSetting() * 100);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100;
            LautstaerkeEinstellungen.saveaVolumeSetting(volume);
            InGameMenu.musikPlayer.setAktuelleLautstaerke(volume);
        });
        panel.getChildren().add(volumeSlider);
        openPanel(panel);
    }

    private void openPanel(String titleText, String[] options, java.util.function.Consumer<String> onSelect) {
        VBox panel = createPanel(titleText);
        ToggleGroup group = new ToggleGroup();
        for (String option : options) {
            RadioButton btn = new RadioButton(option);
            btn.setToggleGroup(group);
            panel.getChildren().add(btn);
        }
        Button confirmButton = createStyledButton("Confirm", event -> {
            RadioButton selected = (RadioButton) group.getSelectedToggle();
            if (selected != null) onSelect.accept(selected.getText());
        });
        panel.getChildren().add(confirmButton);
        openPanel(panel);
    }

    private VBox createPanel(String titleText) {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 20px; -fx-background-radius: 10px;");
        
        Text title = new Text(titleText);
        title.setFont(Font.font("Arial", 25));
        title.setFill(Color.WHITE);
        panel.getChildren().add(title);
        
        Button closeButton = createStyledButton("Close", event -> closeActivePanel());
        panel.getChildren().add(closeButton);
        
        return panel;
    }

    private void openPanel(VBox panel) {
        closeActivePanel();
        activePanel = panel;
        mainMenuRoot.getChildren().add(panel);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), panel);
        transition.setFromX(600);
        transition.setToX(0);
        transition.play();
    }

    private void closeActivePanel() {
        if (activePanel != null) {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), activePanel);
            transition.setFromX(0);
            transition.setToX(600);
            transition.setOnFinished(event -> mainMenuRoot.getChildren().remove(activePanel));
            transition.play();
            activePanel = null;
        }
    }

    public Scene getScene() {
        return this.scene;
    }
}
