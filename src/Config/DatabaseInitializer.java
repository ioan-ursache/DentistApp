package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initializeDatabase(String jdbcUrl) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found");
            return;
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {

            // Create patients table
            String createPatients = "CREATE TABLE IF NOT EXISTS patients (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "number TEXT NOT NULL" +
                    ");";
            stmt.execute(createPatients);

            // Create appointments table
            String createAppointments = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "id TEXT PRIMARY KEY," +
                    "patientID TEXT NOT NULL," +
                    "datetime TEXT NOT NULL," +
                    "FOREIGN KEY (patientID) REFERENCES patients(id)" +
                    ");";
            stmt.execute(createAppointments);

            System.out.println("Database tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
