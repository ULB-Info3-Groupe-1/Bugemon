package bugemon.client.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;

/**
 * Logback pattern converter that maps log levels to ANSI foreground colour codes for console output.
 *
 * <p>
 * Registered in the client's {@code logback.xml} and used in console appender patterns. The server has its own copy so
 * that neither runnable application forces a logging backend onto the shared {@code common} module.
 */
public class LevelColorConverter extends HighlightingCompositeConverter {

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        return switch (event.getLevel().toInt()) {
            case Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG;
            case Level.WARN_INT -> ANSIConstants.BOLD + ANSIConstants.YELLOW_FG;
            case Level.INFO_INT -> ANSIConstants.GREEN_FG;
            case Level.DEBUG_INT -> ANSIConstants.CYAN_FG;
            case Level.TRACE_INT -> ANSIConstants.DEFAULT_FG;
            default -> ANSIConstants.DEFAULT_FG;
        };
    }
}
