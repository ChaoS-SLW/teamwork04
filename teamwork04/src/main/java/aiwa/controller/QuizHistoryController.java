package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.Study;
import aiwa.model.CourseModel;
import aiwa.model.QuizModel;

@WebServlet("/QuizHistoryController")
public class QuizHistoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final QuizModel model = new QuizModel();
	private final CourseModel courses = new CourseModel();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		try {
			long userId = ControllerSupport.userId(request);
			String requestedCourseId = request.getParameter("courseId");
			Integer courseId = ControllerSupport.positiveInt(requestedCourseId);
			if (requestedCourseId != null && courseId == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
			if (courseId == null) {
				request.setAttribute("attempts", model.getHistory(userId));
			} else {
				if (!courses.isAvailableToUser(courseId, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
				request.setAttribute("courseId", courseId);
				for (Study course : courses.getCourses(userId)) {
					if (course.getCourseId() == courseId) { request.setAttribute("courseName", course.getCourseName()); break; }
				}
				request.setAttribute("attempts", model.getHistoryForCourse(userId, courseId));
			}
			ControllerSupport.exposeFlash(request);
			request.getRequestDispatcher("/WEB-INF/views/quiz-history.jsp").forward(request, response);
		} catch (SQLException e) {
			getServletContext().log("Quiz history could not be loaded", e);
			throw new ServletException("Quiz history could not be loaded", e);
		}
	}
}
