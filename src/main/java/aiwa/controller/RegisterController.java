package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.dao.UserDAO;
import aiwa.entity.User;
import aiwa.ulti.PasswordHasher;
import aiwa.ulti.Html;

@WebServlet("/RegisterController")
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
	private final UserDAO users = new UserDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession(false) != null && request.getSession(false).getAttribute("userId") instanceof Number) {
			response.sendRedirect(request.getContextPath() + "/DashboardController"); return;
		}
		ControllerSupport.exposeFlash(request);
		request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!Html.validCsrfToken(request)) { showError(request,response,"This form expired. Refresh the page and try again."); return; }
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirmPassword");
		username = username == null ? "" : username.trim();
		email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
		if (username.length() < 2 || username.length() > 60) { showError(request, response, "Name must be between 2 and 60 characters."); return; }
		if (email.length() > 254 || !EMAIL.matcher(email).matches()) { showError(request, response, "Enter a valid email address."); return; }
		if (password == null || password.length() < 10 || password.length() > 128) { showError(request, response, "Password must be between 10 and 128 characters."); return; }
		if (!password.equals(confirm)) { showError(request, response, "The passwords do not match."); return; }
		try {
			User user = new User(); user.setUsername(username); user.setEmail(email); user.setPasswordHash(PasswordHasher.hash(password.toCharArray()));
			long id = users.create(user);
			ControllerSupport.establishUser(request, id, username);
			response.sendRedirect(request.getContextPath() + "/DashboardController");
		} catch (SQLException e) {
			String message = e.getMessage() != null && e.getMessage().contains("UNIQUE")
					? "That email or name is already registered. Try signing in."
					: "Your account could not be created. Please try again.";
			showError(request, response, message);
		}
	}

	private void showError(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
		request.setAttribute("registerError", message);
		request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
	}
}
