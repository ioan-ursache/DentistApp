package Config;

import java.io.*;

import java.util.Properties;

public class Settings {
    private static final String CONFIG_FILE = "settings.properties";
    private static final Properties prop = new Properties();

    static {
        boolean loaded = false;
        // Try to load from classpath (found that it works for runtime)
        try (InputStream in = Settings.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                prop.load(in);
                loaded = true;
            }
        } catch (IOException ignored) {}

        // Fallback: working directory
        if (!loaded) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                prop.load(reader);
                loaded = true;
            } catch (IOException ignored) {}
        }

        // Fallback: src directory (useful when running from project root without packaging)
        if (!loaded) {
            try (FileReader reader = new FileReader("src" + File.separator + CONFIG_FILE)) {
                prop.load(reader);
                loaded = true;
            } catch (IOException ignored) {}
        }

        if (!loaded) {
            // Applies a set of default settings (we use memory), in case file cannot be found or is unreadable
            System.err.println("Could not load properties file: " + CONFIG_FILE + ". Using default settings...");
            prop.setProperty("Repository", "memory");
            prop.setProperty("Patients", "");
            prop.setProperty("Appointments", "");
            prop.setProperty("DatabaseURL", "jdbc:sqlite:data/database.db");
        }
    }

    private static String sanitize(String value, String defaultValue) {
        // The purpose of this function is to ensure the integrity of the filenames in the configuration file, returning the corrected string.
        // Basically, it ensures there are no quotes surrounding the filename string.
        String v = value != null ? value.trim() : defaultValue;
        if (v == null) return null;
        // Remove optional surrounding quotes
        if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
            v = v.substring(1, v.length() - 1).trim();
        }
        return v;
    }

    // Getter functions for our attributes, supporting default values.
    public static String getRepositoryType() {
        return sanitize(prop.getProperty("Repository", "memory"), "memory").toLowerCase();
    }

    public static String getPatients() {
        return sanitize(prop.getProperty("Patients", "patient.txt"), "patient.txt");
    }
    
    public static String getAppointments() {
        return sanitize(prop.getProperty("Appointments", "appointments.txt"), "appointments.txt");
    }
    
    public static String getDatabaseURL() {
        return sanitize(prop.getProperty("DatabaseURL", "jdbc:sqlite:data/database.db"), "jdbc:sqlite:data/database.db");
    }

}

