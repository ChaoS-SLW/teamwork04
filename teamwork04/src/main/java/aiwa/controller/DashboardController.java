package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.dao.Database;
import aiwa.entity.Study;
import aiwa.model.CourseModel;
import aiwa.model.TaskModel;
import aiwa.service.ActivityService;

@WebServlet("/DashboardController")
public class DashboardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CourseModel courseModel = new CourseModel();
	private final TaskModel taskModel = new TaskModel();
	private final ActivityService activityService = new ActivityService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("UTF-8");
		long userId = ControllerSupport.userId(request);
		try {
			java.util.List<Study> courses = courseModel.getCourses(userId);
			request.setAttribute("courses", courses);
			request.setAttribute("courseCount", courses.size());
			request.setAttribute("taskCount", taskModel.count(userId, null));
			request.setAttribute("completedTaskCount", taskModel.count(userId, "done"));
			request.setAttribute("upcomingTasks", taskModel.getTasks(userId, null, null, null, 5));
			request.setAttribute("streak", activityService.getStudyStreak(userId));
			request.setAttribute("weeklyMinutes", activityService.getWeeklyMinutes(userId));
			request.setAttribute("weekdays", activityService.getWeekdayLabels());
			Map<LocalDate, Integer> activity = activityService.getDailyMinutes(userId, 365);
			request.setAttribute("activity", activity);
		} catch (SQLException e) { request.setAttribute("pageError", "Your dashboard data could not be loaded. Please try again."); }
		request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);

	}

}
