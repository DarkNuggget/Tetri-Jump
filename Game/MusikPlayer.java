import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class MusikPlayer {
    private MediaPlayer mediaPlayer;
    private List<String> gameMusikListe;
    private List<String> menuMusikListe; // Deklariere die Liste für die Menü-Musik
    private final String gameMusik = "Musik/GameMusik/";
    private double aktuelleLautstaerke = 0.5;  // Standardlautstärke
    private TetriJump tetriJump;

    public MusikPlayer() {
        mediaPlayer = null; // Initialisiere mediaPlayer
        tetriJump = null;   // Initialisiere tetriJump
        menuMusikListe = new ArrayList<>(); // Initialisiere die Menü-Musik-Liste
        musikArray(); // Beispiel für das Befüllen der Liste
    }

    // Getter und Setter für die Lautstärke
    public double getAktuelleLautstaerke() {
        return aktuelleLautstaerke;
    }

    public void setAktuelleLautstaerke(double lautstaerke) {
        aktuelleLautstaerke = lautstaerke;
    }

    public void musikArray() {
        // Füge Musikstücke zur Spielmusikliste hinzu
        gameMusikListe = new ArrayList<>();
        gameMusikListe.add("GameMusik1.mp3");
        gameMusikListe.add("GameMusik2.mp3");
        gameMusikListe.add("GameMusik3.mp3");
        gameMusikListe.add("GameMusik4.mp3");
        gameMusikListe.add("GameMusik5.mp3");
        gameMusikListe.add("GameMusik6.mp3");
        gameMusikListe.add("GameMusik7.mp3");
        
        // Füge Musikstücke zur Menü-Musikliste hinzu
//        menuMusikListe.add("MenuMusik1.mp3");
        menuMusikListe.add("MenuMusik2.mp3");
//        menuMusikListe.add("MenuMusik3.mp3");
    }

    // Startet die Menü-Musik (zufällig aus der Menü-Liste)
    public void startMenuMusik() {
        stopMusik();  // Stoppe zuerst die aktuelle Musik, wenn sie läuft
        
        String ausgewählteMusik = waehleZufaelligeMusik(menuMusikListe); // Verwendet menuMusikListe
        File musicFile = new File("Musik/MenuMusik/" + ausgewählteMusik);
        speichereMusik("Musik/MenuMusik/", ausgewählteMusik);
        
        if (musicFile.exists()) {
            // Wenn bereits ein MediaPlayer existiert, stoppen und zurücksetzen
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();  // MediaPlayer korrekt freigeben
            }
            
            // Erstelle einen neuen MediaPlayer
            Media media = new Media(musicFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(this.getAktuelleLautstaerke());  // Setze die Lautstärke
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);    // Musik in Schleife abspielen
            mediaPlayer.play();
            // System.out.println("Musikdatei " + musicFile.getName() + " wurde gespielt.");
        } else {
            System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
        }
    }

    // Startet die Spiel-Musik (zufällig aus der Spiel-Liste)
    public void startGameMusik() {
        stopMusik();  // Stoppe zuerst die aktuelle Musik, wenn sie läuft
        
        String ausgewählteMusik = waehleZufaelligeMusik(gameMusikListe); // Verwendet gameMusikListe
        File musicFile = new File("Musik/GameMusik/" + ausgewählteMusik);
        speichereMusik(gameMusik, ausgewählteMusik);
        
        if (musicFile.exists()) {
            // Wenn bereits ein MediaPlayer existiert, stoppen und zurücksetzen
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();  // MediaPlayer korrekt freigeben
            }
            
            // Erstelle einen neuen MediaPlayer
            Media media = new Media(musicFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(this.getAktuelleLautstaerke());  // Setze die Lautstärke
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);    // Musik in Schleife abspielen
            mediaPlayer.play();
            // System.out.println("Musikdatei " + musicFile.getName() + " wurde gespielt.");
        } else {
            System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
        }
    }

    // Methode zum Stoppen der aktuellen Musik
    public void stopMusik() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();  // MediaPlayer korrekt freigeben
            mediaPlayer = null;  // Setze den MediaPlayer auf null, um ihn später ggf. neu zu erstellen
        }
    }

    // Speichert die Musik
    public void speichereMusik(String musikDatei, String ausgewählteMusik) {
        String AktuelleMusik = musikDatei + ausgewählteMusik;
        System.out.println(AktuelleMusik);
    }

    // Wählt zufällig ein Musikstück aus der übergebenen Liste
    private String waehleZufaelligeMusik(List<String> musikListe) {
        Random random = new Random();
        int index = random.nextInt(musikListe.size());
        return musikListe.get(index);
    }

    // Zeigt das Einstellungsfenster für die Lautstärke
    public void showOptionsWindow() {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Options");

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        Label volumeLabel = new Label("Volume");
        Slider volumeSlider = new Slider(0, 100, this.getAktuelleLautstaerke()); // Hole den aktuellen Lautstärkewert
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.setAktuelleLautstaerke(newValue.doubleValue()); // Aktualisiere die Lautstärke
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(this.getAktuelleLautstaerke()); // Setze die Lautstärke im MediaPlayer
                System.out.println("MusikSlider ändert die musik");
            }
        });

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> optionsStage.close());

        vbox.getChildren().addAll(volumeLabel, volumeSlider, closeButton);

        Scene scene = new Scene(vbox, 300, 150);
        optionsStage.setScene(scene);
        optionsStage.show();
    }
}
