import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;

public class ShopScreen {
    private final TetriJumpShop mainApp;
    private int tetriCoins; // Startwert für TetriCoins
    private List<VBox> skinBoxes = new ArrayList<>();
    private Label coinLabel;
    TetriGui tetriGui;
    MusikPlayer musikPlayer;

    // Konstruktor für ShopScreen, der die TetriJumpShop Instanz benötigt
    public ShopScreen(TetriJumpShop mainApp, TetriGui tetriGui) {
        this.mainApp = mainApp;
        this.tetriCoins = mainApp.getTetriCoins(); // Holen der TetriCoins vom Hauptbildschirm
        this.tetriGui = tetriGui;  // Initialize tetriGui with the passed instance
    }

    // Diese Methode gibt die Scene für den Shop zurück
    public Scene getScene() {
        VBox shopLayout = new VBox(20);
        shopLayout.setPadding(new Insets(20));

        // Hintergrund für das Shop-Layout
        File backgroundFile = new File("Hintergrund/McDonals.jpg");
        String bgUri = backgroundFile.toURI().toString();
        BackgroundImage bgImage = new BackgroundImage(new Image(bgUri),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        shopLayout.setBackground(new Background(bgImage));

        // TetriCoins Kasten oben rechts
        HBox coinBox = new HBox();
        coinBox.setPadding(new Insets(10));
        coinBox.setAlignment(Pos.CENTER);
        coinBox.setStyle("-fx-background-color: #FFD700; -fx-background-radius: 10; -fx-padding: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0.5, 1, 1);");

        coinLabel = new Label("💰 " + tetriCoins);
        coinLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
        coinBox.getChildren().add(coinLabel);

        // Layout für die obere Leiste mit Shop-Name und Coin-Box
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(700); // Abstand zwischen Titel und Coin-Box
        topBar.getChildren().addAll(coinBox);

       
                                                                 
        // Grid für Skins
        GridPane skinGrid = new GridPane();
        skinGrid.setHgap(15);
        skinGrid.setVgap(15);
        skinGrid.setAlignment(Pos.CENTER);

        // Holen der Skins aus der Hauptanwendung
        List<Skin> skins = mainApp.getSkins();

        // Erstellen von Boxen für jedes Skin und Hinzufügen zu einem Grid
        for (int i = 0; i < skins.size(); i++) {
            int row = i / 2;  // Berechnung der Zeile (2 Skins pro Zeile)
            int col = i % 2;  // Berechnung der Spalte (2 Spalten nebeneinander)
            VBox skinBox = createSkinBox(skins.get(i));
            skinGrid.add(skinBox, col, row); // SkinBox zu Grid hinzufügen
            skinBoxes.add(skinBox); // Hinzufügen zur Liste der Skin-Boxen
        }

        // Zurück-Button
        Button backButton = new Button("Zurück");
        backButton.setOnAction(e -> {
            if (tetriGui != null) {
                System.out.println("Zurück zum Startbildschirm");
                tetriGui.showStartScreen();  // Zeigt den Startbildschirm an
                InGameMenu.musikPlayer.stoppeAktuelleMusik();
                InGameMenu.musikPlayer.startMenuMusik();
            } else {
                System.err.println("tetriGui ist null, kann den Startbildschirm nicht anzeigen.");
            }
        });

        // Hinzufügen der Layout-Komponenten zur Haupt-Layout
        shopLayout.getChildren().addAll(topBar, skinGrid, backButton);

        // Rückgabe der Scene für den Shop
        return new Scene(shopLayout, 600, 800);
    }

    // Hilfsmethode zum Erstellen von Skin-Boxen
  private VBox createSkinBox(Skin skin) {
    VBox skinBox = new VBox(10);
    skinBox.setAlignment(Pos.CENTER);

    ImageView skinImage;
    try {
        skinImage = new ImageView(new Image(getClass().getResourceAsStream(skin.getImagePath()), 120, 120, true, true));
    } catch (Exception e) {
        System.out.println("Bild konnte nicht geladen werden: " + skin.getImagePath());
        skinImage = new ImageView();
    }

    Label skinLabel = new Label(skin.getName());
    skinLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #5A9EBB;");

    Button buyButton = new Button("Kaufen");
    Button applyButton = new Button("Anwenden");

    // Initiale Button-Zustände setzen
    buyButton.setDisable(skin.isBought());
    applyButton.setDisable(!skin.isBought() || mainApp.getActiveSkin().equals(skin.getName()));

    // Kauf-Logik
    buyButton.setOnAction(e -> {
    if (tetriCoins >= 500) { // Skin kostet 500 TetriCoins
        tetriCoins -= 500;
        mainApp.setTetriCoins(tetriCoins);
        coinLabel.setText("💰 " + tetriCoins);
        skin.setBought(true); // Skin als gekauft markieren
        updateButtons(); // Buttons aktualisieren
        mainApp.saveBoughtSkinsToFile(); // Gekaufte Skins speichern
        mainApp.saveTetriCoinsToFile(); // TetriCoins speichern
    } else {
        System.out.println("Nicht genug TetriCoins!");
    }
});


    // Anwenden-Logik
    applyButton.setOnAction(e -> {
        mainApp.setActiveSkin(skin.getName());
        updateButtons(); // Buttons aktualisieren
        saveDataToFile(); // Speichern der Daten nach dem Wechsel des aktiven Skins
    });

    HBox buttonBox = new HBox(10, buyButton, applyButton);
    buttonBox.setAlignment(Pos.CENTER);

    skinBox.getChildren().addAll(skinImage, skinLabel, buttonBox);
    return skinBox;
}

  
  private void saveDataToFile() {
    try {
        // TetriCoins speichern
        FileWriter coinWriter = new FileWriter("Config/TetriCoins.txt");
        coinWriter.write(String.valueOf(tetriCoins));
        coinWriter.close();

        // Aktiven Skin speichern
        FileWriter skinWriter = new FileWriter("Config/ActiveSkin.txt");
        skinWriter.write(mainApp.getActiveSkin());
        skinWriter.close();

        System.out.println("Daten wurden erfolgreich gespeichert!");
    } catch (IOException e) {
        System.err.println("Fehler beim Speichern der Daten: " + e.getMessage());
    }
}
  
  private void loadDataFromFile() {
    // TetriCoins laden
    try (Scanner coinScanner = new Scanner(new File("Config/TetriCoins.txt"))) {
        if (coinScanner.hasNextInt()) {
            tetriCoins = coinScanner.nextInt();
            System.out.println("Geladene TetriCoins: " + tetriCoins);
        }
    } catch (IOException e) {
        System.err.println("Fehler beim Laden der TetriCoins: " + e.getMessage());
    }

    // Aktiven Skin laden
    try (Scanner skinScanner = new Scanner(new File("Config/ActiveSkin.txt"))) {
        if (skinScanner.hasNextLine()) {
            String activeSkinName = skinScanner.nextLine().trim();
            mainApp.setActiveSkin(activeSkinName);
            System.out.println("Geladener aktiver Skin: " + activeSkinName);
        }
    } catch (IOException e) {
        System.err.println("Fehler beim Laden des aktiven Skins: " + e.getMessage());
    }
}


    private void updateButtons() {
    for (VBox skinBox : skinBoxes) {
        Label label = (Label) skinBox.getChildren().get(1);
        HBox buttonBox = (HBox) skinBox.getChildren().get(2);
        Button buyButton = (Button) buttonBox.getChildren().get(0);
        Button applyButton = (Button) buttonBox.getChildren().get(1);

        String skinName = label.getText();
        Skin skin = mainApp.getSkins().stream()
            .filter(s -> s.getName().equals(skinName))
            .findFirst()
            .orElse(null);

        if (skin != null) {
            buyButton.setDisable(skin.isBought());
            applyButton.setDisable(!skin.isBought() || mainApp.getActiveSkin().equals(skin.getName()));

            if (mainApp.getActiveSkin().equals(skinName)) {
                applyButton.setText("Aktiv");
                applyButton.setStyle("-fx-background-color: lightgreen;");
            } else {
                applyButton.setText("Anwenden");
                applyButton.setStyle(""); // Zurücksetzen
            }
        }
    }
}

}
