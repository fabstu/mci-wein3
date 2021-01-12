package mci.aufnehmen;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
    /**
     * Matches the given text to the pattern. If it matches, it returns true,
     * else false.
     *
     * @param text string.
     * @return whether the text follows the allowed pattern.
     */
    public static boolean matchesPattern(Pattern p, String text) {
        Matcher matcher = p.matcher(text);
        return matcher.matches();
    }

    /**
     * Internal to a text formatter. Implements whether the text change is
     * allowed.
     *
     * @return the change incase allowed. If not allowed it returns null.
     */
    public static UnaryOperator<TextFormatter.Change> patternFilter(Pattern p) {
        return patternFilterWithHandler(p, null);
    }

    /**
     * Internal to a text formatter. Implements whether the text change is
     * allowed.
     *
     * @return the change incase allowed. If not allowed it returns null.
     */
    public static UnaryOperator<TextFormatter.Change> patternFilterWithHandler(Pattern p, TextFilterHandler handler) {
        return change -> {
            // Return non-content-changing changes.
            if (!change.isContentChange()) {
                return change;
            }

            String before = change.getControlText();
            String after = change.getControlNewText();

            // Do not apply an edit which did not change anything.
            if (before.equals(after)) {
                return null;
            }

            // Debugging.
            System.out.println("before: " + before + " after: " + after);

            if (!RegexHelper.matchesPattern(p, after)) {
                return null;
            }

            if(handler == null) {
                return change;
            }

            return handler.decide(change);
        };
    }

    public static UnaryOperator<TextFormatter.Change> sanityFilterWithHandler(TextFilterHandler handler) {
        return change -> {
            // Return non-content-changing changes.
            if (!change.isContentChange()) {
                return change;
            }

            String before = change.getControlText();
            String after = change.getControlNewText();

            // Do not apply an edit which did not change anything.
            if (before.equals(after)) {
                return null;
            }

            // Debugging.
            System.out.println("before: " + before + " after: " + after);

            if (handler == null) {
                return change;
            }

            return handler.decide(change);
        };
    }

    public interface TextFilterHandler {
        public TextFormatter.Change decide(TextFormatter.Change change);
    }
}
