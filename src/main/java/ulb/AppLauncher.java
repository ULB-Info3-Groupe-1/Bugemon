package ulb;

/**
 * Main entry point (Launcher) of the application
 * 
 * This class serves as a "neutral" entry point to allow the JavaFX application 
 * to run from a JAR file. It bypasses the JavaFX runtime components check
 * performed by the JVM when the main class directly
 */
public class AppLauncher {

    /**
     * Main entry point of the program
     * Delegates execution to the main method of the Main class
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}