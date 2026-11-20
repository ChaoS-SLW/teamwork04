package aiwa.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

final class ControllerSupport {
	private ControllerSupport() { }
	static void flash(HttpServletRequest request, String kind, String message) {
		HttpSession session = request.getSession();
		session.setAttribute("flashKind", kind);
		session.setAttribute("flashMessage", message);
	}
	static void exposeFlash(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) return;
		Object kind = session.getAttribute("flashKind");
		Object message = session.getAttribute("flashMessage");
		if (kind != null) request.setAttribute("flashKind", kind);
		if (message != null) request.setAttribute("flashMessage", message);
		session.removeAttribute("flashKind");
		session.removeAttribute("flashMessage");
	}
	static Integer positiveInt(String value) {
		try { int parsed = Integer.parseInt(value); return parsed > 0 ? parsed : null; }
		catch (RuntimeException e) { return null; }
	}
	static Integer optionalPositiveInt(String value) {
		if (value == null || value.isBlank()) return null;
		Integer parsed = positiveInt(value);
		if (parsed == null) throw new IllegalArgumentException("Choose a valid course.");
		return parsed;
	}
}
