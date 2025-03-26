import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.IOException;

public class GridEinstellungen {

    private static final String FILE_PATH = "config/gridSetting.txt";  // relativer Pfad zur Datei

    // Speichert den Status der Gitter-Anzeige
   // Diese Methode sollte in der Klasse GridEinstellungen sein
public static void saveGridSetting(boolean isGridEnabled) {
    // Speichern des Wertes, zum Beispiel in einer Datei oder in den Einstellungen
    // Beispiel: Speichern in einer Properties-Datei
    Properties properties = new Properties();
    properties.setProperty("gridEnabled", String.valueOf(isGridEnabled));
    try (FileOutputStream out = new FileOutputStream("settings.properties")) {
        properties.store(out, null);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    // Lädt den Status der Gitter-Anzeige
    public static boolean loadGridSetting() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();  // Liest die Zeile aus
                return Boolean.parseBoolean(line);  // Gibt den Wert als boolean zurück
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;  // Standardwert, falls die Datei nicht existiert oder leer ist
    }
}
