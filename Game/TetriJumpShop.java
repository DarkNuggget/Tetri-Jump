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
    private String activeSkin = "Skin 1"; // Standard-Skin

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
      
        showStartScreen();
    }

    public void showShopScreen() {
        ShopScreen shopScreen = new ShopScreen(this);
        Scene shopScene = shopScreen.getScene();

        primaryStage.setTitle("TetriGui - Shop");
        primaryStage.setScene(shopScene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(800);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public void showStartScreen() {
        StartScreen startScreen = new StartScreen(this);
        Scene startScene = startScreen.getScene();
        primaryStage.setTitle("TetriGui - Start");
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public String getActiveSkin() {
        return activeSkin;
    }

    public void setActiveSkin(String skinName) {
        this.activeSkin = skinName;
        System.out.println("Aktiver Skin: " + skinName);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class ShopScreen {
    private final TetriJumpShop mainApp;

    public ShopScreen(TetriJumpShop mainApp) {
        this.mainApp = mainApp;
    }

    public Scene getScene() {
        VBox shopLayout = new VBox(20);
        shopLayout.setPadding(new Insets(20));
        shopLayout.setStyle("-fx-background-color: #e5f5f5;");

        Label shopTitle = new Label("Tetri-Jump Shop");
        shopTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");

        Label shopDescription = new Label("Kaufe und wende Skins für deinen Charakter an!");
        shopDescription.setStyle("-fx-font-size: 18px; -fx-text-fill: #555;");

        GridPane skinGrid = new GridPane();
        skinGrid.setHgap(15);
        skinGrid.setVgap(15);
        skinGrid.setAlignment(Pos.CENTER);

        List<Skin> skins = new ArrayList<>();
        skins.add(new Skin("Skin 1", "#TetriJumpStandardCharacter.png", true)); // Standard-Skin
        skins.add(new Skin("Skin 2", "#TetriJumpDinoSkin.png", false));
        skins.add(new Skin("Skin 3", "", false));

        for (int i = 0; i < skins.size(); i++) {
            int row = i / 6;
            int col = i % 6;
            VBox skinBox = createSkinBox(skins.get(i));
            skinGrid.add(skinBox, col, row);
        }

        Button backButton = new Button("Zurück");
        backButton.setOnAction(e -> mainApp.showStartScreen());
        backButton.setStyle("-fx-font-size: 16px; -fx-background-color: #4A90E2; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-cursor: hand;");

        shopLayout.getChildren().addAll(shopTitle, shopDescription, skinGrid, backButton);

        return new Scene(shopLayout, 1000, 800);
    }

    private VBox createSkinBox(Skin skin) {
        VBox skinBox = new VBox(10);
        skinBox.setStyle("-fx-border-color: #4A90E2; -fx-border-width: 2px; -fx-background-color: #ffffff; -fx-padding: 15px; -fx-alignment: center; -fx-border-radius: 10px;");

        ImageView skinImage;
        try {
            skinImage = new ImageView(new Image(getClass().getResourceAsStream(skin.getImagePath()), 120, 120, true, true));
        } catch (Exception e) {
            System.out.println("Bild konnte nicht geladen werden: " + skin.getImagePath());
            skinImage = new ImageView();
        }

        Label skinLabel = new Label(skin.getName());
        skinLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");

        Button buyButton = new Button("Kaufen");
        buyButton.setDisable(skin.isStandard() || mainApp.getActiveSkin().equals(skin.getName()));
        buyButton.setOnAction(e -> {
            System.out.println(skin.getName() + " gekauft!");
            buyButton.setDisable(true);
        });
        buyButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4A90E2; -fx-text-fill: white; -fx-padding: 5px 15px; -fx-border-radius: 5px; -fx-cursor: hand;");

        Button applyButton = new Button("Anwenden");
        applyButton.setDisable(mainApp.getActiveSkin().equals(skin.getName()));
        applyButton.setOnAction(e -> {
            mainApp.setActiveSkin(skin.getName());
            System.out.println(skin.getName() + " angewendet!");
        });
        applyButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4A90E2; -fx-text-fill: white; -fx-padding: 5px 15px; -fx-border-radius: 5px; -fx-cursor: hand;");

        HBox buttonBox = new HBox(10, buyButton, applyButton);
        buttonBox.setStyle("-fx-alignment: center;");

        skinBox.getChildren().addAll(skinImage, skinLabel, buttonBox);
        return skinBox;
    }
}

class Skin {
    private final String name;
    private final String imagePath;
    private final boolean isStandard;

    public Skin(String name, String imagePath, boolean isStandard) {
        this.name = name;
        this.imagePath = imagePath;
        this.isStandard = isStandard;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isStandard() {
        return isStandard;
    }
}

class MusikPlayer {
    public void stopMusik() {
        System.out.println("Musik gestoppt.");
    }
}

class StartScreen {
    private final TetriJumpShop mainApp;

    public StartScreen(TetriJumpShop mainApp) {
        this.mainApp = mainApp;
    }

    public Scene getScene() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f9f9f9;");

        Label title = new Label("Willkommen im Tetri-Jump Shop!");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");

        Button shopButton = new Button("Zum Shop");
        shopButton.setOnAction(e -> mainApp.showShopScreen());
        shopButton.setStyle("-fx-font-size: 16px; -fx-background-color: #4A90E2; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-cursor: hand;");

        layout.getChildren().addAll(title, shopButton);

        return new Scene(layout, 800, 600);
    }
}
