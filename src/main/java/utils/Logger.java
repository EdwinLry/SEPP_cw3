package utils;

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

    public void log(long time, String userId, String actionName, String inputs, String status) {
        //TODO: use tinylog to log the message
    }
}
