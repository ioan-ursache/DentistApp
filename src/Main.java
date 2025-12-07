import UI.AppointmentGUI;
import UI.ConsoleUI;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Check if we should launch GUI or Console UI
        if (args.length > 0 && args[0].equals("--console")) {
            // Launch console UI
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
        } else {
            // Launch JavaFX GUI by default
            Application.launch(AppointmentGUI.class, args);
        }
    }
}
