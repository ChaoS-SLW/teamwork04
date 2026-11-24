package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.model.QuizModel;

@WebServlet("/QuizHistoryController")
public class QuizHistoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final QuizModel model = new QuizModel();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		try {
			request.setAttribute("attempts", model.getHistory(ControllerSupport.userId(request)));
			ControllerSupport.exposeFlash(request);
			request.getRequestDispatcher("/WEB-INF/views/quiz-history.jsp").forward(request, response);
		} catch (SQLException e) {
			getServletContext().log("Quiz history could not be loaded", e);
			throw new ServletException("Quiz history could not be loaded", e);
		}
	}
}
