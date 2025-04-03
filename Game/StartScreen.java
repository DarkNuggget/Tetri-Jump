import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
  private StackPane backgroundStack;
  private TetriJump tetriJump;
  
  public StartScreen(TetriGui app) {       
    this.app = app;
    
    createUI();
    
  }
  
  private void createUI() {
    if (buttonsAdded) return;
    
    InGameMenu.musikPlayer.startMenuMusik();
    InGameMenu.musikPlayer.MenuOffen = true;
    
    backgroundStack = new StackPane();
    
    // Hintergrund
    File backgroundFile = new File("Hintergrund/MainMenuHintergrund.jpg");
    String bgUri = backgroundFile.toURI().toString();
    ImageView backgroundImageView = new ImageView(new Image(bgUri, 600, 700, false, true));
    backgroundStack.getChildren().add(backgroundImageView);
    
    mainMenuRoot = new VBox(15);
    mainMenuRoot.setAlignment(Pos.CENTER);
    mainMenuRoot.setPrefSize(600, 700);
    
    // Titel
    ImageView titleImage = new ImageView(new Image(new File("Bilder/TetriJump.png").toURI().toString()));
    titleImage.setFitWidth(300);
    titleImage.setFitHeight(100);
    
    VBox buttonContainer = new VBox(10);
    buttonContainer.setAlignment(Pos.CENTER);
    buttonContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-padding: 20px; -fx-background-radius: 10px;");
    
    Button startButton = createStyledButton("Bilder/Play.png", event -> showModeSelection());
    Button optionsButton = createStyledButton("Bilder/Option.png", event -> showVolumeSettings());
    Button shopButton = createStyledButton("Bilder/Shop.png", e -> {
      InGameMenu.musikPlayer.stoppeAktuelleMusik();
      InGameMenu.musikPlayer.startShopMusik();
      app.openShop();
    });
    Button exitButton = createStyledButton("Bilder/Exit.png", event -> System.exit(0));
    
    buttonContainer.getChildren().addAll(startButton, optionsButton, shopButton, exitButton);
    mainMenuRoot.getChildren().addAll(titleImage, buttonContainer);
    
    // Hintergrund und Menü zusammen in StackPane
    backgroundStack.getChildren().add(mainMenuRoot);
    
    scene = new Scene(backgroundStack, 600, 700);
    buttonsAdded = true;
  }
  
  private Button createStyledButton(String imagePath, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
    Button button = new Button();
    ImageView icon = new ImageView(new Image(new File(imagePath).toURI().toString()));
    icon.setFitWidth(140); // Standardbreite
    icon.setFitHeight(40); // Standardhöhe
    button.setGraphic(icon);
    button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    button.setMinWidth(110);
    button.setMinHeight(60);
    button.setOnAction(action);
    
    // Hover-Effekt hinzufügen
    button.setOnMouseEntered(event -> {
      icon.setFitWidth(160); // Größer beim Hover
      icon.setFitHeight(45);
    });
    
    button.setOnMouseExited(event -> {
      icon.setFitWidth(150); // Zurück zur Originalgröße
      icon.setFitHeight(40);
    });
    
    return button;
  }

  private void showModeSelection() {
    // Hauptmenü ausblenden, aber der Hintergrund bleibt sichtbar
    hideMainMenu();
    
    VBox panel = createPanel("Wähle einen Spielmodus:");
    ToggleGroup group = new ToggleGroup();
    String[] modes = {"Classic Tetris", "Jump Tetris"};
    
    for (String mode : modes) {
      RadioButton btn = new RadioButton(mode);
      btn.setToggleGroup(group);
      btn.setStyle("-fx-text-fill: white;");
      panel.getChildren().add(btn);
    }
    
    Button confirmButton = createStyledButton("Bilder/Play.png", event -> {
      RadioButton selected = (RadioButton) group.getSelectedToggle();
      if (selected != null) {
        String selectedMode = selected.getText();
        InGameMenu.musikPlayer.MenuOffen = false;
        InGameMenu.musikPlayer.startGameMusik();
        app.startGameWithMode(selectedMode);
      }
    });
    panel.getChildren().add(confirmButton);
    
    // Zurück-Button für die Spielauswahl
    Button backButton = createStyledButton("Bilder/Exit.png", event -> closePanelAndReturnToMainMenu());
    panel.getChildren().add(backButton);
    
    openPanel(panel);
  }

  // In StartScreen class
  private void showVolumeSettings() {
    // Hauptmenü ausblenden, aber der Hintergrund bleibt sichtbar
    hideMainMenu();
    
    VBox panel = createPanel("Lautstärke anpassen");
    Slider volumeSlider = new Slider(0, 100, LautstaerkeEinstellungen.loadVolumeSetting() * 100);
    volumeSlider.setShowTickLabels(true);
    volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
      double volume = newVal.doubleValue() / 100;
      LautstaerkeEinstellungen.saveaVolumeSetting(volume);
      InGameMenu.musikPlayer.setAktuelleLautstaerke(volume);
    });
    panel.getChildren().add(volumeSlider);
   
    
    
    
    
    // Button zum Zurückkehren ins Hauptmenü
    Button backButton = createStyledButton("Bilder/Exit.png", event -> closePanelAndReturnToMainMenu());
    panel.getChildren().add(backButton);
    
    openPanel(panel);
  }




  private void hideMainMenu() {
    // Hauptmenü nach links verschieben, aber der Hintergrund bleibt sichtbar
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), mainMenuRoot);
    transition.setFromX(0);   // Startet bei der ursprünglichen Position (links)
    transition.setToX(-700);  // Bewegt das Menü nach links
    transition.play();
  }

  private void closePanelAndReturnToMainMenu() {
    // Panel (Spielmodus-Auswahl oder Optionen) ausblenden
    closeActivePanel();
    
    // Hauptmenü wieder nach links verschieben
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), mainMenuRoot);
    transition.setFromX(-700); // Startet von der linken Seite
    transition.setToX(0);      // Kommt zurück zum ursprünglichen Zustand
    transition.play();
  }

  private void openPanel(String titleText, String[] options, java.util.function.Consumer<String> onSelect) {
    VBox panel = createPanel(titleText);
    ToggleGroup group = new ToggleGroup();
    for (String option : options) {
      RadioButton btn = new RadioButton(option);
      btn.setToggleGroup(group);
      btn.setStyle("-fx-text-fill: white;");
      panel.getChildren().add(btn);
    }
    Button confirmButton = createStyledButton("Bilder/Play.png", event -> {
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
    
    return panel;
  }

  private void openPanel(VBox panel) {
    closeActivePanel();
    activePanel = panel;
    backgroundStack.getChildren().add(panel);
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
      transition.setOnFinished(event -> backgroundStack.getChildren().remove(activePanel));
      transition.play();
      activePanel = null;
    }
  }

  public Scene getScene() {
    return this.scene;
  }
}
