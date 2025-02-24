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
    private List<String> menuMusikListe;
    private final String gameMusik = "Musik/GameMusik/";
    private double aktuelleLautstaerke = 0.5;  // Standardlautstärke

    public MusikPlayer() {
        mediaPlayer = null;
        menuMusikListe = new ArrayList<>();
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
        gameMusikListe = new ArrayList<>();
        gameMusikListe.add("GameMusik1.mp3");
        gameMusikListe.add("GameMusik2.mp3");
        gameMusikListe.add("GameMusik3.mp3");
        gameMusikListe.add("GameMusik4.mp3");
        gameMusikListe.add("GameMusik5.mp3");
        gameMusikListe.add("GameMusik6.mp3");
        gameMusikListe.add("GameMusik7.mp3");

        menuMusikListe.add("MenuMusik2.mp3");
    }

    // Musik stoppen
    private void stoppeAktuelleMusik() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    // Startet die Menü-Musik
    public void startMenuMusik() {
        stoppeAktuelleMusik();
        
        String ausgewählteMusik = waehleZufaelligeMusik(menuMusikListe);
        File musicFile = new File("Musik/MenuMusik/" + ausgewählteMusik);
        
        if (musicFile.exists()) {
            setUpMediaPlayer(musicFile);
        } else {
            System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
        }
    }

    // Startet die Spiel-Musik
    public void startGameMusik() {
        stoppeAktuelleMusik();
        
        String ausgewählteMusik = waehleZufaelligeMusik(gameMusikListe);
        File musicFile = new File("Musik/GameMusik/" + ausgewählteMusik);
        
        if (musicFile.exists()) {
            setUpMediaPlayer(musicFile);
        } else {
            System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
        }
    }

    // Einrichten des MediaPlayers
    private void setUpMediaPlayer(File musicFile) {
        Media media = new Media(musicFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(this.getAktuelleLautstaerke());
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    // Wählt zufällig ein Musikstück aus der Liste
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
        Slider volumeSlider = new Slider(0, 100, this.getAktuelleLautstaerke() * 100); // Lautstärkewert von 0 bis 100

        volumeSlider.setBlockIncrement(1);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.setAktuelleLautstaerke(newValue.doubleValue() / 100); // Lautstärke von 0 bis 1
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(this.getAktuelleLautstaerke());
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
