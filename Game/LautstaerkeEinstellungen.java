import java.io.*;
import java.util.Properties;

public class LautstaerkeEinstellungen {
    private static final String SETTINGS_FILE = "audioSettings.properties";

    // Speichert die Lautstärke in einer Datei
    public static void saveaVolumeSetting(double volume) {
        Properties properties = new Properties();
        properties.setProperty("volume", String.valueOf(volume));

        try (FileOutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(output, "Audio Settings");
            System.out.println("Lautstärke gespeichert: " + volume);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lädt die Lautstärke aus der Datei
    public static double loadVolumeSetting() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
            String volumeStr = properties.getProperty("volume", "0.5");
            return Double.parseDouble(volumeStr);
        } catch (IOException e) {
            e.printStackTrace();
            return 0.5; // Standardwert
        }
    }
}
