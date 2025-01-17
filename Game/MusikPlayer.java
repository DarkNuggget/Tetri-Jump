import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MusikPlayer {
    private MediaPlayer mediaPlayer;
    private List<String> menuMusikListe;
    private List<String> gameMusikListe;
    private final String menuMusik = "Musik/MenuMusik/";
    private final String gameMusik = "Musik/GameMusik/";
    private double aktuelleLautstaerke = 1.0;  // Standardlautstärke
  
    // Getter und Setter für die Lautstärke
    public double getAktuelleLautstaerke() {
        return aktuelleLautstaerke;
    }

    public void setAktuelleLautstaerke(double lautstaerke) {
        aktuelleLautstaerke = lautstaerke;
    }

    public MusikPlayer() {
        // Musikdateien für das Menü
        menuMusikListe = new ArrayList<>();
        menuMusikListe.add("MenuMusik1.mp3");
        menuMusikListe.add("MenuMusik2.mp3");
        menuMusikListe.add("MenuMusik3.mp3");

        // Musikdateien für das Spiel
        gameMusikListe = new ArrayList<>();
        gameMusikListe.add("GameMusik1.mp3");
        gameMusikListe.add("GameMusik2.mp3");
        gameMusikListe.add("GameMusik3.mp3");
        gameMusikListe.add("GameMusik4.mp3");
        gameMusikListe.add("GameMusik5.mp3");
    }

    // Startet die Menü-Musik (zufällig aus der Menü-Liste)
    public void startMenuMusik() {
        stopMusik();  // Stoppe zuerst die aktuelle Musik
        String ausgewählteMusik = waehleZufaelligeMusik(menuMusikListe);
        File musicFile = new File(menuMusik + ausgewählteMusik);

        if (musicFile.exists()) {
            Media media = new Media(musicFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(this.getAktuelleLautstaerke()); // Setze die Lautstärke
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);  // Musik in Schleife abspielen
            mediaPlayer.play();
        } else {
            System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
        }
    }

    // Startet die Spiel-Musik (zufällig aus der Spiel-Liste)
    public void startGameMusik() {
        stopMusik();  // Stoppe zuerst die aktuelle Musik
        String ausgewählteMusik = waehleZufaelligeMusik(gameMusikListe);
        File musicFile = new File(gameMusik + ausgewählteMusik);

        if (musicFile.exists()) {
            Media media = new Media(musicFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(this.getAktuelleLautstaerke()); // Setze die Lautstärke
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);  // Musik in Schleife abspielen
            mediaPlayer.play();
        } else {
            System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
        }
    }

    // Stoppt die aktuell abgespielte Musik
    public void stopMusik() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null; // Vermeidet ungewollte Instanzen
        } else {
            System.out.println("musikPlayer ist Null (musikPlayer ist nicht initialisiert!)");
        }
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
        Slider volumeSlider = new Slider(0, 1, this.getAktuelleLautstaerke()); // Hole den aktuellen Lautstärkewert
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.setAktuelleLautstaerke(newValue.doubleValue()); // Aktualisiere die Lautstärke
            
                mediaPlayer.setVolume(this.getAktuelleLautstaerke()); // Setze die Lautstärke im MediaPlayer
            
        });

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> optionsStage.close());

        vbox.getChildren().addAll(volumeLabel, volumeSlider, closeButton);

        Scene scene = new Scene(vbox, 300, 150);
        optionsStage.setScene(scene);
        optionsStage.show();
    }
}
