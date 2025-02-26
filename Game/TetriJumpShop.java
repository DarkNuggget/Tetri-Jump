import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class TetriJumpShop {

    private String activeSkin = "Standard"; // Standard-Skin
    private int tetriCoins = 1000; // Startwert für TetriCoins
    private List<Skin> skins = new ArrayList<>();

    public TetriJumpShop() {
        // Initialisiere die Skins
        skins.add(new Skin("Standard", "/Skins/Standard.png", true)); // Standard-Skin ist immer gekauft
        skins.add(new Skin("Dinosaurier", "/Skins/Dinosaurier.png", false));
        skins.add(new Skin("Gentleman", "/Skins/Gentleman.png", false));
        skins.add(new Skin("Wache", "/Skins/Wache.png", false));
    }

    public void showShopScreen(Stage primaryStage) {
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
}
