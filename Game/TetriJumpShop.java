import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class TetriJumpShop extends Application {

    private Stage primaryStage;
    private String activeSkin = "Standard"; // Standard-Skin
    private int tetriCoins = 1000; // Startwert für TetriCoins
    private List<Skin> skins = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialisiere die Skins
        skins.add(new Skin("Standard", "/Skins/Standard.png", true)); // Standard-Skin ist immer gekauft
        skins.add(new Skin("Dinosaurier", "/Skins/Dinosaurier.png", false));
        skins.add(new Skin("Gentleman", "/Skins/Gentleman.png", false));
        skins.add(new Skin("Wache", "/Skins/Wache.png", false));

        showStartScreen();
    }

    public void showStartScreen() {
        StartScreen startScreen = new StartScreen(this); // Übergibt TetriJumpShop
        Scene startScene = startScreen.getScene(); // Hier wird die Szene des Startbildschirms geholt
        primaryStage.setTitle("TetriGui - Start");
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void showShopScreen() {
        ShopScreen shopScreen = new ShopScreen(this); // Übergibt TetriJumpShop
        Scene shopScene = shopScreen.getScene(); // Hier wird die Szene des Shops geholt
        primaryStage.setTitle("TetriGui - Shop");
        primaryStage.setScene(shopScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public String getActiveSkin() {
        return activeSkin;
    }

    public void setActiveSkin(String skinName) {
        this.activeSkin = skinName;
        System.out.println("Aktiver Skin: " + skinName);
    }

    public void setTetriCoins(int coins) {
        this.tetriCoins = coins;
    }

    public int getTetriCoins() {
        return tetriCoins;
    }

    public List<Skin> getSkins() {
        return skins;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class StartScreen {
    private final TetriJumpShop mainApp;

    public StartScreen(TetriJumpShop mainApp) {
        this.mainApp = mainApp;
    }

    public Scene getScene() {
        VBox startLayout = new VBox(20);
        startLayout.setStyle("-fx-background-color: #e5f5f5;");
        
        Label startLabel = new Label("Willkommen im Tetri-Jump Shop!");
        startLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");
        
        Button enterShopButton = new Button("Shop betreten");
        enterShopButton.setStyle("-fx-font-size: 18px;");
        enterShopButton.setOnAction(e -> mainApp.showShopScreen()); // Wechselt zum Shop

        startLayout.getChildren().addAll(startLabel, enterShopButton);
        
        return new Scene(startLayout, 1200, 600);
    }
}

class ShopScreen {
    private final TetriJumpShop mainApp;
    private int tetriCoins; // Startwert für TetriCoins
    private List<VBox> skinBoxes = new ArrayList<>();
    private Label coinLabel;

    public ShopScreen(TetriJumpShop mainApp) {
        this.mainApp = mainApp;
        this.tetriCoins = mainApp.getTetriCoins(); // Holen der TetriCoins vom Hauptbildschirm
    }

    public Scene getScene() {
        VBox shopLayout = new VBox(20);
        shopLayout.setPadding(new Insets(20));
        shopLayout.setStyle("-fx-background-color: #e5f5f5;");

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
        Label shopTitle = new Label("Tetri-Jump Shop");
        shopTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");
        topBar.getChildren().addAll(shopTitle, coinBox);

        Label shopDescription = new Label("Kaufe und wende Skins für deinen Charakter an!");
        shopDescription.setStyle("-fx-font-size: 18px; -fx-text-fill: #555;");

        GridPane skinGrid = new GridPane();
        skinGrid.setHgap(15);
        skinGrid.setVgap(15);
        skinGrid.setAlignment(Pos.CENTER);

        List<Skin> skins = mainApp.getSkins(); // Skins aus der Hauptanwendung holen

        for (int i = 0; i < skins.size(); i++) {
            int row = i / 6;
            int col = i % 6;
            VBox skinBox = createSkinBox(skins.get(i));
            skinGrid.add(skinBox, col, row);
            skinBoxes.add(skinBox);
        }

        Button backButton = new Button("Zurück");
        backButton.setOnAction(e -> mainApp.showStartScreen());

        shopLayout.getChildren().addAll(topBar, shopDescription, skinGrid, backButton);
        return new Scene(shopLayout, 1200, 600);
    }

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

class Skin {
    private final String name;
    private final String imagePath;
    private boolean bought;

    public Skin(String name, String imagePath, boolean bought) {
        this.name = name;
        this.imagePath = imagePath;
        this.bought = bought;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }
}
