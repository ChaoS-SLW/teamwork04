package aiwa.ulti;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Html {
	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a");
	private Html() { }
	public static String escape(Object value) {
		if (value == null) return "";
		String text = String.valueOf(value);
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
	}
	public static String dateTime(LocalDateTime value) { return value == null ? "No due date" : DATE_TIME.format(value); }
}
