package aiwa.ulti;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

public final class Html {
	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a");
	private static final SecureRandom RANDOM = new SecureRandom();
	private Html() {
	}
	public static String escape(Object value) {
		if (value == null)
			return "";
		String text = String.valueOf(value);
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}
	public static String dateTime(LocalDateTime value) {
		return value == null ? "No due date" : DATE_TIME.format(value);
	}
	public static String csrfToken(HttpServletRequest request) {
		javax.servlet.http.HttpSession session = request.getSession(true);
		Object token = session.getAttribute("csrfToken");
		if (token instanceof String)
			return (String) token;
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		String created = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		session.setAttribute("csrfToken", created);
		return created;
	}
	public static boolean validCsrfToken(HttpServletRequest request) {
		javax.servlet.http.HttpSession session = request.getSession(false);
		if (session == null || !(session.getAttribute("csrfToken") instanceof String))
			return false;
		String expected = (String) session.getAttribute("csrfToken");
		String submitted = request.getParameter("csrfToken");
		return submitted != null && MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
				submitted.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
}
