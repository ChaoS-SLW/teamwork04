package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.Quiz;
import aiwa.entity.QuizAttempt;
import aiwa.entity.Study;
import aiwa.model.CourseModel;
import aiwa.model.QuizModel;
import aiwa.ulti.Html;

@WebServlet("/QuizController")
public class QuizController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final QuizModel model = new QuizModel();
	private final CourseModel courses = new CourseModel();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		long userId = ControllerSupport.userId(request);
		ControllerSupport.exposeFlash(request);
		Integer resultId = ControllerSupport.positiveInt(request.getParameter("resultId"));
		Integer quizId = ControllerSupport.positiveInt(request.getParameter("quizId"));
		String requestedCourseId = request.getParameter("courseId");
		Integer courseId = ControllerSupport.positiveInt(requestedCourseId);
		try {
			if (requestedCourseId != null && courseId == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
			if (resultId != null) {
				QuizAttempt attempt = model.getAttempt(userId, resultId);
				if (attempt == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
				request.setAttribute("attempt", attempt);
				request.getRequestDispatcher("/WEB-INF/views/quiz-result.jsp").forward(request, response);
			} else if (quizId != null) {
				Quiz quiz = model.getQuiz(quizId, userId);
				if (quiz == null || quiz.getQuestionCount() < 1) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
				request.setAttribute("quiz", quiz);
				request.setAttribute("questions", model.getQuestions(quizId));
				request.getRequestDispatcher("/WEB-INF/views/quiz-take.jsp").forward(request, response);
			} else {
				if (courseId == null) {
					request.setAttribute("quizzes", model.getAvailable(userId));
				} else {
					if (!courses.isAvailableToUser(courseId, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
					request.setAttribute("courseId", courseId);
					for (Study course : courses.getCourses(userId)) {
						if (course.getCourseId() == courseId) { request.setAttribute("courseName", course.getCourseName()); break; }
					}
					request.setAttribute("quizzes", model.getAvailableForCourse(userId, courseId));
				}
				request.getRequestDispatcher("/WEB-INF/views/quiz-list.jsp").forward(request, response);
			}
		} catch (SQLException e) {
			getServletContext().log("Quiz page could not be loaded", e);
			throw new ServletException("Quiz data could not be loaded", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		if (!Html.validCsrfToken(request)) {
			ControllerSupport.flash(request, "warning", "This quiz form expired. Please open the quiz again.");
			response.sendRedirect(request.getContextPath() + "/QuizController");
			return;
		}
		Integer quizId = ControllerSupport.positiveInt(request.getParameter("quizId"));
		if (quizId == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
		Map<Integer, Integer> answers = new HashMap<>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if (!name.startsWith("q_")) continue;
			Integer questionId = ControllerSupport.positiveInt(name.substring(2));
			Integer optionId = ControllerSupport.positiveInt(request.getParameter(name));
			if (questionId != null && optionId != null) answers.put(questionId, optionId);
		}
		try {
			int resultId = model.submit(ControllerSupport.userId(request), quizId, answers);
			response.sendRedirect(request.getContextPath() + "/QuizController?resultId=" + resultId);
		} catch (IllegalArgumentException e) {
			ControllerSupport.flash(request, "warning", e.getMessage());
			response.sendRedirect(request.getContextPath() + "/QuizController?quizId=" + quizId);
		} catch (SQLException e) {
			getServletContext().log("Quiz attempt could not be saved", e);
			ControllerSupport.flash(request, "danger", "Your quiz result could not be saved. Please try again.");
			response.sendRedirect(request.getContextPath() + "/QuizController?quizId=" + quizId);
		}
	}
}
