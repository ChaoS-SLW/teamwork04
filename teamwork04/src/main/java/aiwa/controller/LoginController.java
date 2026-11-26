package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.dao.UserDAO;
import aiwa.entity.User;
import aiwa.ulti.PasswordHasher;
import aiwa.ulti.Html;

@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserDAO users = new UserDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getSession(false) != null && request.getSession(false).getAttribute("userId") instanceof Number) {
			response.sendRedirect(request.getContextPath() + "/DashboardController");
			return;
		}
		request.setAttribute("next", safeNext(request.getParameter("next")));
		ControllerSupport.exposeFlash(request);
		request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!Html.validCsrfToken(request)) {
			request.setAttribute("loginError", "This form expired. Refresh the page and try again.");
			request.setAttribute("next", safeNext(request.getParameter("next")));
			request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
			return;
		}
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String next = safeNext(request.getParameter("next"));
		try {
			username = username == null ? "" : username.trim();
			User user = username.isEmpty() || password == null ? null : users.findActiveByUsername(username);
			if (user == null || !PasswordHasher.verify(password.toCharArray(), user.getPasswordHash())) {
				request.setAttribute("loginError", "Name or password is incorrect.");
				request.setAttribute("loginUsername", username);
				request.setAttribute("next", next);
				request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
				return;
			}
			ControllerSupport.establishUser(request, user.getUserId(), user.getUsername());
			response.sendRedirect(request.getContextPath() + (next == null ? "/DashboardController" : next));
		} catch (SQLException e) {
			request.setAttribute("loginError", "Sign-in is temporarily unavailable. Please try again.");
			request.setAttribute("loginUsername", username);
			request.setAttribute("next", next);
			request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
		}
	}

	private String safeNext(String next) {
		if (next == null || next.isBlank() || !next.startsWith("/") || next.startsWith("//") || next.contains("\\")
				|| next.contains("\r") || next.contains("\n"))
			return null;
		return next;
	}
}
