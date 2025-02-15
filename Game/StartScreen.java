import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.util.Duration;
import java.io.File;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.animation.ScaleTransition;


public class StartScreen {
    private TetriGui app;
    private Scene scene;
    private MusikPlayer musikPlayer;
    private TetriJump tetriJump;

    public StartScreen(TetriGui app) {
        this.app = app;
        this.musikPlayer = new MusikPlayer(); // MusikPlayer instanziieren
        createUI();
    }

    public void createUI() {
        musikPlayer.startMenuMusik(); // Menü-Musik starten

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // Hintergrundbild laden
        File backgroundFile = new File("Hintergrund/MainMenuHintergrund.jpg");
        String bgUri = backgroundFile.toURI().toString();
        BackgroundImage bgImage = new BackgroundImage(new javafx.scene.image.Image(bgUri),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(bgImage));

        // Titel-Text für das Spiel
        Text title = new Text("TetriJump");
        title.setFont(new Font(50));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");

        // Start-Button
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        startButton.setOnAction(event -> showModeSelection());

        // Exit-Button
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        exitButton.setOnAction(event -> {
            musikPlayer.stopMusik(); // Stoppe Musik, wenn das Spiel beendet wird
            System.exit(0);
        });

        // Buttons zur VBox hinzufügen
        root.getChildren().addAll(title, startButton, exitButton);

        // Szene erstellen
        scene = new Scene(root, 596, 672);
    }

    private void showModeSelection() {
        // Neues Fenster für die Spielmodi-Auswahl
        Stage modeStage = new Stage();
        modeStage.setTitle("Choose Game Mode");

        VBox modeRoot = new VBox(20);
        modeRoot.setAlignment(Pos.CENTER);

        Text modeTitle = new Text("Select a Game Mode");
        modeTitle.setFont(new Font(30));
        modeTitle.setFill(Color.BLACK);

        // RadioButtons für die Modi
        ToggleGroup modeGroup = new ToggleGroup();

        RadioButton classicTetris = new RadioButton("Classic Tetris");
        classicTetris.setToggleGroup(modeGroup);
        classicTetris.setSelected(true);

        RadioButton jumpTetris = new RadioButton("Jump Tetris");
        jumpTetris.setToggleGroup(modeGroup);

        // Start-Button für den ausgewählten Modus
        Button startModeButton = new Button("Start");
        startModeButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        startModeButton.setOnAction(event -> {
            RadioButton selectedMode = (RadioButton) modeGroup.getSelectedToggle();
            if (selectedMode != null) {
                String mode = selectedMode.getText();
                System.out.println("Starting game in mode: " + mode);
                modeStage.close();
        
                Screen(); 
                //app.startGameWithMode(mode); // Spiel im ausgewählten Modus starten
        
        System.out.println("kannst wieder beim startScreen umstellen indem du in zeile 115 löscht und zeile 116 wie einfügst ");
            }
            musikPlayer.stopMusik(); // Stoppe die Menü-Musik
            musikPlayer.startGameMusik(); // Spiele die Spiel-Musik
        });

        modeRoot.getChildren().addAll(modeTitle, classicTetris, jumpTetris, startModeButton);

        Scene modeScene = new Scene(modeRoot, 400, 300);
        modeStage.setScene(modeScene);
        modeStage.show();
    }


  public void Screen() {
    // Erstelle das Fenster
    Stage screen = new Stage();
    
    // Setze eine festgelegte Fenstergröße
    screen.setWidth(1600);  // Beispiel: Breite 800px
    screen.setHeight(1200);  // Beispiel: Höhe 600px

    // StackPane für das Bild und Musik
    StackPane root = new StackPane();

    // Bild für den Jumpscare
    Image image = new Image("Musik/GameMusik/GameMusik8.jpg");  // Ersetze mit dem Pfad deiner Bilddatei
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(1600);  // Fenstergröße anpassen
    imageView.setFitHeight(1200);

    // Das Bild wird dem StackPane hinzugefügt
    root.getChildren().add(imageView);

    // Pfad zur Musikdatei
    String audioFilePath = "Musik/MenuMusik/MenuMusik1.mp3";  // Pfad zur Audiodatei
    
    // Überprüfe, ob die Audio-Datei existiert
    File audioFile = new File(audioFilePath);
    if (!audioFile.exists()) {
        System.out.println("Fehler: Die Audiodatei " + audioFilePath + " wurde nicht gefunden!");
        return;  // Verlasse die Methode, wenn die Datei nicht existiert
    }

    
    Media sound = new Media(audioFile.toURI().toString());  // Verwende den URI der Datei
    MediaPlayer mediaPlayer = new MediaPlayer(sound);

    
    mediaPlayer.play();  

    // Minimiere alle anderen Fenster von JavaFX
    for (Window window : javafx.stage.Window.getWindows()) {
        if (window instanceof Stage) {
            Stage stage = (Stage) window;  // Casten zu Stage
            // Optionale Anpassungen für andere Fenster, falls gewünscht
        }
    }

   
    Scene jumpscareScene = new Scene(root);
    screen.setScene(jumpscareScene);
    screen.show();

    // Stelle sicher, dass das Fenster im Vordergrund bleibt
    screen.toFront();  // Bringt das Fenster in den Vordergrund

  
    Timeline closeTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
        screen.close();  // Schließt das Fenster nach 3 Sekunden
    }));
    closeTimeline.setCycleCount(1);  // Nur einmal ausführen
    closeTimeline.play();  // Zeitgeber starten
}




    public Scene getScene() {
        return this.scene;
    }
}
