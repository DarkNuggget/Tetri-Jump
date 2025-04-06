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
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class TetriJumpShop {

    private String activeSkin = "Standard"; // Standard-Skin
    private int tetriCoins = 1000; // Startwert für TetriCoins
    private List<Skin> skins = new ArrayList<>();

    public TetriJumpShop() {
        // Initialisiere die Skins
        skins.add(new Skin("Standard", "/Skins/Standard.png", true)); // Standard-Skin immer gekauft
        skins.add(new Skin("Dinosaurier", "/Skins/Dinosaurier.png", false));
        skins.add(new Skin("Gentleman", "/Skins/Gentleman.png", false));
        skins.add(new Skin("Wache", "/Skins/Wache.png", false));
        skins.add(new Skin("berndDasBrot", "/Skins/berndDasBrot.png", false));

        // Lade alle Daten beim Start
        loadActiveSkinFromFile();
        loadTetriCoinsFromFile();
        loadBoughtSkinsFromFile();
    }

    public void showShopScreen(Stage primaryStage, TetriGui tetriGui) {
        ShopScreen shopScreen = new ShopScreen(this, tetriGui); // Pass both instances
        Scene shopScene = shopScreen.getScene(); // Get the scene for the shop
        primaryStage.setTitle("TetriGui - Shop");
        primaryStage.setScene(shopScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // Speichern des aktiven Skins in eine Datei
    private void saveActiveSkinToFile() {
        try {
            File configDir = new File("/Config");
            if (!configDir.exists()) {
                configDir.mkdir(); // Ordner erstellen, falls nicht vorhanden
            }
            FileWriter writer = new FileWriter("Config/Skin.txt");
            writer.write("/Skins/" + activeSkin + ".png");
            writer.close();
            System.out.println("Skin wurde in Config/Skin.txt gespeichert.");
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern des Skins: " + e.getMessage());
        }
    }

    // Laden des aktiven Skins aus der Datei
    private void loadActiveSkinFromFile() {
        File file = new File("Config/Skin.txt");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    String skinPath = scanner.nextLine().trim();
                    for (Skin skin : skins) {
                        if (skin.getImagePath().equals(skinPath)) {
                            this.activeSkin = skin.getName();
                            System.out.println("Geladener aktiver Skin: " + activeSkin);
                            return;
                        }
                    }
                    System.out.println("Kein Skin mit diesem Pfad gefunden: " + skinPath);
                }
            } catch (IOException e) {
                System.err.println("Fehler beim Laden des Skin-Pfads: " + e.getMessage());
            }
        } else {
            System.out.println("Keine Skin-Konfig gefunden. Standard wird verwendet.");
        }
    }

    // Speichern der TetriCoins in eine Datei
    public void saveTetriCoinsToFile() {
    try {
        File configDir = new File("Config");
        if (!configDir.exists()) {
            configDir.mkdir(); // Ordner erstellen, falls nicht vorhanden
        }

        FileWriter writer = new FileWriter("Config/TetriCoins.txt");
        writer.write(String.valueOf(tetriCoins)); // Speichern der TetriCoins
        writer.close();
        System.out.println("TetriCoins wurden in Config/TetriCoins.txt gespeichert.");
    } catch (IOException e) {
        System.err.println("Fehler beim Speichern der TetriCoins: " + e.getMessage());
    }
}


    // Laden der TetriCoins aus der Datei
    public void loadTetriCoinsFromFile() {
    File file = new File("Config/TetriCoins.txt");
    if (file.exists()) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                tetriCoins = Integer.parseInt(scanner.nextLine().trim()); // TetriCoins aus der Datei lesen
                System.out.println("Geladene TetriCoins: " + tetriCoins);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der TetriCoins: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Fehler beim Konvertieren der TetriCoins: " + e.getMessage());
        }
    } else {
        System.out.println("Keine TetriCoins-Konfiguration gefunden. Standardwert wird verwendet.");
    }
}


    // Speichern der gekauften Skins in eine Datei
    public void saveBoughtSkinsToFile() {
    try {
        File configDir = new File("Config");
        if (!configDir.exists()) {
            configDir.mkdir(); // Ordner erstellen, falls nicht vorhanden
        }

        FileWriter writer = new FileWriter("Config/BoughtSkins.txt");
        for (Skin skin : skins) {
            if (skin.isBought()) {
                writer.write(skin.getName() + "\n");
            }
        }
        writer.close();
        System.out.println("Gekaufte Skins wurden in Config/BoughtSkins.txt gespeichert.");
    } catch (IOException e) {
        System.err.println("Fehler beim Speichern der gekauften Skins: " + e.getMessage());
    }
}


    // Laden der gekauften Skins aus der Datei
    private void loadBoughtSkinsFromFile() {
    File file = new File("Config/BoughtSkins.txt");
    if (file.exists()) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String skinName = scanner.nextLine().trim();
                for (Skin skin : skins) {
                    if (skin.getName().equals(skinName)) {
                        skin.setBought(true); // Skin als gekauft markieren
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der gekauften Skins: " + e.getMessage());
        }
    } else {
        System.out.println("Keine gekauften Skins gefunden.");
    }
}


    public String getActiveSkin() {
        return activeSkin;
    }

    public void setActiveSkin(String skinName) {
        this.activeSkin = skinName;
        System.out.println("Aktiver Skin: " + skinName);
        saveActiveSkinToFile();
    }

    public void setTetriCoins(int coins) {
        this.tetriCoins = coins;
        saveTetriCoinsToFile();
    }

    public int getTetriCoins() {
        return tetriCoins;
    }

    public List<Skin> getSkins() {
        return skins;
    }

}

