package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.dao.Database;
import aiwa.entity.Study;
import aiwa.model.CourseModel;
import aiwa.model.PlannerModel;
import aiwa.model.TaskModel;
import aiwa.service.ActivityService;

@WebServlet("/DashboardController")
public class DashboardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CourseModel courseModel = new CourseModel();
	private final TaskModel taskModel = new TaskModel();
	private final PlannerModel plannerModel = new PlannerModel();
	private final ActivityService activityService = new ActivityService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("UTF-8");
		long userId = ControllerSupport.userId(request);
		LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		request.setAttribute("plannerWeekStart", weekStart);
		request.setAttribute("plannerItems", java.util.Collections.emptyList());
		try {
			java.util.List<Study> courses = courseModel.getCourses(userId);
			request.setAttribute("courses", courses);
			java.util.List<Study> inProgressCourses = new java.util.ArrayList<>();
			for (Study course : courses) {
				boolean started = course.getCompletedTaskCount() > 0 || course.getInProgressTaskCount() > 0
						|| course.getAttemptedQuizCount() > 0;
				boolean unfinished = course.getProgressPercent() < 100;
				if (started && unfinished)
					inProgressCourses.add(course);
			}
			request.setAttribute("inProgressCourses", inProgressCourses);
			request.setAttribute("courseCount", courses.size());
			request.setAttribute("taskCount", taskModel.count(userId, null));
			request.setAttribute("completedTaskCount", taskModel.count(userId, "done"));
			request.setAttribute("upcomingTasks", taskModel.getTasks(userId, null, null, null, 5));
			request.setAttribute("plannerItems", plannerModel.getWeek(userId, weekStart));
			request.setAttribute("streak", activityService.getStudyStreak(userId));
			request.setAttribute("weeklyMinutes", activityService.getWeeklyMinutes(userId));
			request.setAttribute("weekdays", activityService.getWeekdayLabels());
			Map<LocalDate, Integer> activity = activityService.getDailyMinutes(userId, 365);
			request.setAttribute("activity", activity);
		} catch (SQLException e) {
			request.setAttribute("pageError", "Your dashboard data could not be loaded. Please try again.");
		}
		ControllerSupport.exposeFlash(request);
		request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);

	}

}
