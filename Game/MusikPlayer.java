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
    private ArrayList<String> gameMusikListe;
    private ArrayList<String> menuMusikListe;
    private double aktuelleLautstaerke;

    public MusikPlayer() {
        this.aktuelleLautstaerke = 0.5;
        menuMusikListe = new ArrayList<String>();
        gameMusikListe = new ArrayList<String>();
    
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

    public double getAktuelleLautstaerke() {
        return aktuelleLautstaerke;
    }

    public void setAktuelleLautstaerke(double lautstaerke) {
        aktuelleLautstaerke = lautstaerke;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(lautstaerke);
        }
    }

    // Musik stoppen
    public void stoppeAktuelleMusik() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        } else {
            System.out.println("No music is playing to stop.");
        }
    }


    // Startet die Menü-Musik
    public void startMenuMusik() {
        starteMusik(menuMusikListe, "MenuMusik"); 
    }

    // Startet die Spiel-Musik
    public void startGameMusik() {
        starteMusik(gameMusikListe, "GameMusik");
    }
  
    private void starteMusik(ArrayList<String> musikListe, String ordner) {
        System.out.println(this);
        stoppeAktuelleMusik();
        String ausgewaehlteMusik = waehleZufaelligeMusik(musikListe);
        File musicFile = new File("Musik/" + ordner + "/" + ausgewaehlteMusik);

        if(musicFile.exists()) {
            setUpMediaPlayer(musicFile);
        } else {
            System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
        }
    }

    // Wählt zufällig ein Musikstück aus der Liste
    private String waehleZufaelligeMusik(List<String> musikListe) {
        Random random = new Random();
        return musikListe.get(random.nextInt(musikListe.size()));
    }
  
    private void setUpMediaPlayer(File musicFile) {
        Media media = new Media(musicFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(this.getAktuelleLautstaerke());
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    // Zeigt das Einstellungsfenster für die Lautstärke
     public void showOptionsWindow() {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Optionen");

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        Label volumeLabel = new Label("Lautstärke");
        Slider volumeSlider = new Slider(0, 100, this.getAktuelleLautstaerke() * 100);

        volumeSlider.setBlockIncrement(1);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.setAktuelleLautstaerke(newValue.doubleValue() / 100);
        });

        Button closeButton = new Button("Schließen");
        closeButton.setOnAction(e -> optionsStage.close());

        vbox.getChildren().addAll(volumeLabel, volumeSlider, closeButton);

        Scene scene = new Scene(vbox, 300, 150);
        optionsStage.setScene(scene);
        optionsStage.show();
    }
}
