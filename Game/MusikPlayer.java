import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import java.io.File;

public class MusikPlayer {
    private MediaPlayer mediaPlayer;

    public void startMenuMusik() {
        File musicFile = new File("Musik1.mp3");
        Media media = new Media(musicFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);  // Musik in Schleife abspielen
        mediaPlayer.play();
    }

    public void stopMenuMusik() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void showOptionsWindow() {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Options");

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        Label volumeLabel = new Label("Volume");
        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setValue(0.5);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
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
