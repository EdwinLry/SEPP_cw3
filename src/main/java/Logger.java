import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    // Singleton instance
    private static Logger instance;
    // Internal storage for log entries
    private final List<String> logEntries;
    // Formatter for timestamps
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private constructor to prevent instantiation
    private Logger() {
        logEntries = new ArrayList<>();
    }
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(LocalDateTime time, String userId, String actionName, String inputs, String status) {
        String formattedTime = time.format(formatter);
        String logEntry = String.format("[%s] User: %s, Action: %s, Inputs: %s, Status: %s",
                formattedTime, userId, actionName, inputs, status);
        // Add the log entry to the list (only logging; no printing)
        logEntries.add(logEntry);
    }
}
