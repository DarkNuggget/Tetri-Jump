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
        skinLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #5A9EBB; -fx-effect: dropshadow(gaussian, rgba(0,0,0.5,0.8), 5, 0.5, 1, 1);");

        Button buyButton = new Button("Kaufen");
        buyButton.setDisable(skin.isBought());
        buyButton.setOnAction(e -> {
            if (tetriCoins >= 500) { // Skin kostet 500 TetriCoins
                tetriCoins -= 500;
                mainApp.setTetriCoins(tetriCoins);
                coinLabel.setText("💰 " + tetriCoins);
                skin.setBought(true); // Skin als gekauft markieren
                buyButton.setDisable(true);
                updateButtons(); // Buttons aktualisieren
            } else {
                System.out.println("Nicht genug TetriCoins!");
            }
        });

        Button applyButton = new Button("Anwenden");
        applyButton.setDisable(!skin.isBought() || mainApp.getActiveSkin().equals(skin.getName()));
        applyButton.setOnAction(e -> {
            mainApp.setActiveSkin(skin.getName());
            updateButtons();
        });

        HBox buttonBox = new HBox(10, buyButton, applyButton);
        buttonBox.setAlignment(Pos.CENTER);

        skinBox.getChildren().addAll(skinImage, skinLabel, buttonBox);
        return skinBox;
    }

    private void updateButtons() {
        for (VBox skinBox : skinBoxes) {
            Label label = (Label) skinBox.getChildren().get(1);
            HBox buttonBox = (HBox) skinBox.getChildren().get(2);
            Button applyButton = (Button) buttonBox.getChildren().get(1);
            Button buyButton = (Button) buttonBox.getChildren().get(0);
            String skinName = label.getText();
            Skin skin = mainApp.getSkins().stream().filter(s -> s.getName().equals(skinName)).findFirst().get();

            applyButton.setDisable(!skin.isBought() || mainApp.getActiveSkin().equals(skin.getName()));
            buyButton.setDisable(skin.isBought());
        }
    }
}
