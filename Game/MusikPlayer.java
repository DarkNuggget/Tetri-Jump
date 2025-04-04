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
  private ArrayList<String> shopMusikListe;  // Neue Liste für die Shop-Musik
  private ArrayList<String> deathScreenMusikListe;  // Neue Liste für die Death-Screen-Musik
  private ArrayList<String> HellListe;  // Neue Liste für die Death-Screen-Musik
  private ArrayList<String> HimmelListe;  // Neue Liste für die Death-Screen-Musik
  private ArrayList<String> jumpSoundListe;  // Neue Liste für die Jump-Sounds
  private ArrayList<String> screamSoundListe;  // Neue Liste für Schrei-Sounds

  private double aktuelleLautstaerke;
  private String letzterSong;  // Speichert den zuletzt abgespielten Song
  public boolean MenuOffen;

  public MusikPlayer() {
    this.aktuelleLautstaerke = 0.3;
    menuMusikListe = new ArrayList<String>();
    gameMusikListe = new ArrayList<String>();
    shopMusikListe = new ArrayList<String>();  // Initialisierung der Shop-Musik-Liste
    deathScreenMusikListe = new ArrayList<String>();  // Initialisierung der Death-Screen-Musik-Liste
    jumpSoundListe = new ArrayList<String>();  // Initialisierung der Jump-Sounds
    HellListe = new ArrayList<String>();  // Initialization missing
    HimmelListe = new ArrayList<String>();  // Initialization missing
    screamSoundListe = new ArrayList<String>();
    
    screamSoundListe.add("Scream1.mp3");
    screamSoundListe.add("Scream2.mp3");
    
    HellListe.add("HellDeath1.mp3");
    HellListe.add("HellDeath2.mp3");
    
    HimmelListe.add("GameMusik1.mp3");
    HimmelListe.add("GameMusik1.mp3");
    
    gameMusikListe.add("GameMusik1.mp3");
    gameMusikListe.add("GameMusik2.mp3");
    gameMusikListe.add("GameMusik3.mp3");
    gameMusikListe.add("GameMusik4.mp3");
    gameMusikListe.add("GameMusik5.mp3");
    gameMusikListe.add("GameMusik6.mp3");
    gameMusikListe.add("GameMusik7.mp3");
    
    menuMusikListe.add("MenuMusik1.mp3");
    menuMusikListe.add("MenuMusik2.mp3");
    
    shopMusikListe.add("ShopMusik2.mp3");
    shopMusikListe.add("ShopMusik1.mp3");
    
    deathScreenMusikListe.add("DeathMusik1.mp3");  // Beispiel-Song für Death-Screen
    deathScreenMusikListe.add("DeathMusik2.mp3");
    
    jumpSoundListe.add("JumpSound1.mp3");  // Beispiel-Sounds für Sprünge
    jumpSoundListe.add("JumpSound2.mp3");
    
    letzterSong = "";  // Zu Beginn gibt es keinen letzten Song
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
  
  public void starteHell() {
    starteMusik(HellListe, "HellDeath");
  }
  
  public void scream() {
    starteMusik(screamSoundListe, "Scream");
}
  
  public void starteHimmel() {
    starteMusik(HimmelListe, "HimmelDeath");
  }
    // Startet die Shop-Musik
  public void startShopMusik() {
    starteMusik(shopMusikListe, "ShopMusik");
  }

    // Startet die Death-Screen-Musik
  public void startDeathScreenMusik() {
    starteMusik(deathScreenMusikListe, "DeathScreenMusik");
  }

    // Startet den Jump-Sound
  public void playJumpSound() {
    starteMusik(jumpSoundListe, "JumpSounds");
  }

  private void starteMusik(ArrayList<String> musikListe, String ordner) {
    stoppeAktuelleMusik();
    String ausgewaehlteMusik = waehleZufaelligeMusik(musikListe);
    File musicFile = new File("Musik/" + ordner + "/" + ausgewaehlteMusik);
    
    if(musicFile.exists()) {
      letzterSong = ausgewaehlteMusik;  // Speichere den zuletzt abgespielten Song
      setUpMediaPlayer(musicFile);
    } else {
      System.out.println("Musikdatei " + musicFile.getName() + " wurde nicht gefunden.");
    }
  }

    // Wählt zufällig ein Musikstück aus der Liste, aber nicht den zuletzt abgespielten Song
  private String waehleZufaelligeMusik(List<String> musikListe) {
    Random random = new Random();
    String ausgewaehlteMusik;
    
    do {
      ausgewaehlteMusik = musikListe.get(random.nextInt(musikListe.size()));
    } while (ausgewaehlteMusik.equals(letzterSong));  // Verhindert, dass der letzte Song erneut gespielt wird
    return ausgewaehlteMusik;
  }

  private void setUpMediaPlayer(File musicFile) {
    Media media = new Media(musicFile.toURI().toString());
    mediaPlayer = new MediaPlayer(media);
    mediaPlayer.setVolume(this.getAktuelleLautstaerke());
    mediaPlayer.setCycleCount(1);  // Einmaliges Abspielen des Songs
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
  
  public boolean istMusikSpielend() {
    return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
  }

  
}
