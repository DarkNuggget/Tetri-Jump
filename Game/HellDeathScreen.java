import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HellDeathScreen {
  private Stage primaryStage;
  private Scene previousScene;
  private TetriGui app = new TetriGui(); // Assuming TetriGui has a method to show the start screen.
  public final static MusikPlayer musikPlayer = new MusikPlayer();
  String skinPath = loadSkinPathFromFile();
  
  public HellDeathScreen(Stage primaryStage, Scene previousScene) {
    this.primaryStage = primaryStage;
    this.previousScene = previousScene;
  }
  
  public void show() {
    StackPane root = new StackPane();
    InGameMenu.musikPlayer.stoppeAktuelleMusik();
    InGameMenu.musikPlayer.starteHell();
    musikPlayer.scream();
    // Hintergrundbild: Welt oben und Hölle unten
    Image image = new Image("Hintergrund/Hölle.png"); // Bildpfad anpassen
    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(1400);  // Bildhöhe anpassen
    imageView.setFitWidth(600);    // Bildbreite anpassen
    
    // Neues Bild in der Szene hinzufügen
    Image additionalImage = new Image(new File("Bilder/WellHell.png").toURI().toString()); // Bildpfad anpassen
    ImageView additionalImageView = new ImageView(additionalImage);
    additionalImageView.setFitHeight(300);  // Größe des neuen Bildes anpassen
    additionalImageView.setFitWidth(600);
    
    // Bild mittig in der Szene platzieren
    additionalImageView.setTranslateX(0);
    additionalImageView.setTranslateY(-75);
    additionalImageView.setVisible(false);
    
    // Das Hintergrundbild initial am unteren Rand der Szene positionieren
    imageView.setTranslateY(350);  // Die Höhe der Szene (700) ist der Startpunkt für das Hintergrundbild
    
    // Neues Bild in die Mitte der Szene einfügen
    Image fallingImage = new Image(skinPath); // Pfad aus Datei statt festem Wert
    ImageView fallingImageView = new ImageView(fallingImage);
    fallingImageView.setFitHeight(100);  // Höhe des fallenden Bildes anpassen
    fallingImageView.setFitWidth(100);   // Breite des fallend  // Breite des fallenden Bildes anpassen

    // Bild in die Mitte der Szene platzieren
    fallingImageView.setTranslateX(0);  // Setze das Bild mittig (600px Breite -> 275px ist die Mitte)
    fallingImageView.setTranslateY(-200);  // Setze das Bild etwas über der Mitte
    
    // Score aus Datei lesen
    String scoreText = "";
    File scoreFile = new File("Config/score.txt"); // Pfad zur Score-Datei anpassen
    if (scoreFile.exists()) {
      try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
        String line = reader.readLine();
        if (line != null) {
          scoreText = line;
        }
      } catch (IOException e) {
        scoreText = "Score: Fehler beim Laden";
        e.printStackTrace();
      }
    }
    
    System.out.println(scoreText);
    
    // Text-Element für den Score
    Text scoreDisplay = new Text(scoreText);
    scoreDisplay.setFont(new Font("Arial", 60)); // kleinere Schrift
    scoreDisplay.setFill(Color.RED);
    scoreDisplay.setTranslateY(35); // genau wie das Bild
    scoreDisplay.setTranslateX(-5); 
    scoreDisplay.setStroke(Color.ORANGE); // Schwarze Kontur für bessere Lesbarkeit
    scoreDisplay.setStrokeWidth(3);
    scoreDisplay.setVisible(false);
    
    // Neuen schwarzen Block in der Mitte der Szene hinzufügen
    Rectangle blackBlock = new Rectangle(2000, 2000, Color.BLACK); // Schwarzer Block
    blackBlock.setTranslateX(0);  // In der Mitte der Szene
    blackBlock.setTranslateY(-200);  // Auf Höhe von "Berdt"
    
    // Nebel-Effekt hinzufügen (Verblassen und Weichzeichnen)
    GaussianBlur blur = new GaussianBlur();
    blackBlock.setEffect(blur);
    
    
    root.getChildren().add(additionalImageView);
    root.getChildren().add(imageView);  // Füge das Hintergrundbild zum StackPane hinzu
    root.getChildren().add(fallingImageView);  // Füge das fallende Bild zum StackPane hinzu
    root.getChildren().add(scoreDisplay);
    root.getChildren().add(blackBlock);  // Zum StackPane hinzufügen
    
    // Button für Neustart erstellen
    Button restartButton = createStyledButton("Bilder/MainMenu.png", event -> {
      System.out.println("Returning to Main Menu...");
      InGameMenu.musikPlayer.stoppeAktuelleMusik();
      
      app.showStartScreen();
    });
    
    
    restartButton.setVisible(false);  // Button zunächst unsichtbar machen
    
    // Layout für den Death Screen
    StackPane overlay = new StackPane();
    overlay.getChildren().addAll(restartButton,additionalImageView);
    
    // Button positionieren
    StackPane.setAlignment(restartButton, Pos.BOTTOM_CENTER);
    // Spielfeld bleibt im Hintergrund sichtbar
    StackPane layout = new StackPane();
    layout.getChildren().addAll(root, overlay);
    
    // Erstelle die Szene
    Scene hellDeathScene = new Scene(layout, 600, 700);
    
    // Stage konfigurieren
    primaryStage.setTitle("HellDeathScreen");
    primaryStage.setScene(hellDeathScene);
    primaryStage.show();
    
    // Timer: Verzögerung von 1 Sekunde, bevor die Animation startet
    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));  // Verzögerung von 1 Sekunde
    pause.setOnFinished(event -> startAnimations(imageView, fallingImageView, blackBlock, blur, restartButton, additionalImageView, scoreDisplay));
    
    pause.play();  // Timer starten
  }
  
  private void startAnimations(ImageView imageView, ImageView fallingImageView, Rectangle blackBlock, GaussianBlur blur, Button restartButton, ImageView additionalImageView, Text scoreDisplay){
    // Dauer der Animation für beide Bilder auf 5 Sekunden setzen
    Duration animationDuration = Duration.seconds(3);
    
    // Animation erstellen: Hintergrundbild nach oben bewegen
    TranslateTransition backgroundTransition = new TranslateTransition(animationDuration, imageView);
    backgroundTransition.setToY(-350);  // Bild um 700px nach oben verschieben
    backgroundTransition.play();  // Starte die Animation für das Hintergrundbild
    
    // Animation erstellen: Das fallende Bild nach unten bewegen
    TranslateTransition fallingImageTransition = new TranslateTransition(animationDuration, fallingImageView);
    fallingImageTransition.setToY(400);  // Bewege das Bild nach unten (zur unteren Grenze der Szene)
    fallingImageTransition.play();  // Starte die Animation für das fallende Bild
    
    // FadeTransition für den Nebel-Effekt auf das schwarze Rechteck
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), blackBlock); // Schneller Fade-Effekt
    fadeTransition.setFromValue(1.0);  // Anfangs vollständig sichtbar
    fadeTransition.setToValue(0.0);   // Am Ende völlig unsichtbar
    fadeTransition.setCycleCount(1);  // Nur einmal abspielen
    fadeTransition.play();
    
    // Weichzeichnungs-Effekt sofort stärker einstellen
    blur.setRadius(15); // Sofort stärkerer Weichzeichnungs-Effekt
    fallingImageTransition.setOnFinished(e -> {
      blur.setRadius(0);  // Nach der Animation den Blur-Effekt zurücksetzen
      restartButton.setVisible(true);  // Button nach der Animation sichtbar machen
      additionalImageView.setVisible(true);
      scoreDisplay.setVisible(true);
    });
  }
  
  private Button createStyledButton(String imagePath, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
    Button button = new Button();
    ImageView icon = new ImageView(new Image(new File(imagePath).toURI().toString()));
    icon.setFitWidth(140);
    icon.setFitHeight(40);
    button.setGraphic(icon);
    button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"); 
    button.setMinWidth(110);
    button.setMinHeight(60);
    button.setOnAction(action);
    
    // Hover-Effekt hinzufügen
    button.setOnMouseEntered(event -> {
      icon.setFitWidth(160);
      icon.setFitHeight(45);
    });
    
    button.setOnMouseExited(event -> {
      icon.setFitWidth(150);
      icon.setFitHeight(40);
    });
    
    return button;
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
    return "/Skins/Standard.png"; // Fallback, falls Datei nicht existiert oder fehlerhaft ist
  }

  
}
