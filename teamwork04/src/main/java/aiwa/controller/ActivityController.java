package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.dao.Database;
import aiwa.entity.StudySession;
import aiwa.model.CourseModel;
import aiwa.model.StudyModel;
import aiwa.service.ActivityService;
import aiwa.ulti.Html;

@WebServlet("/ActivityController")
public class ActivityController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final StudyModel studyModel = new StudyModel();
	private final CourseModel courseModel = new CourseModel();
	private final ActivityService activityService = new ActivityService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("UTF-8");
		long userId = ControllerSupport.userId(request);
		try {
			request.setAttribute("sessions", studyModel.getRecentSessions(userId, 30));
			request.setAttribute("courses", courseModel.getCourses(userId));
			request.setAttribute("totalMinutes", studyModel.getTotalMinutes(userId));
			request.setAttribute("streak", activityService.getStudyStreak(userId));
			request.setAttribute("weeklyMinutes", activityService.getWeeklyMinutes(userId));
			request.setAttribute("weekdays", activityService.getWeekdayLabels());
			request.setAttribute("monthlyActivity", activityService.getDailyMinutes(userId, 30));
			request.setAttribute("activity", activityService.getDailyMinutes(userId, 365));
			request.setAttribute("weeklyActions", activityService.getDailyActions(userId, 7));
			request.setAttribute("monthlyActions", activityService.getDailyActions(userId, 30));
			request.setAttribute("activityActions", activityService.getDailyActions(userId, 365));
			request.setAttribute("activeSession", studyModel.getActiveSession(userId));
		} catch (SQLException e) {
			request.setAttribute("pageError", "Study activity could not be loaded. Please try again.");
		}
		ControllerSupport.exposeFlash(request);
		request.getRequestDispatcher("/WEB-INF/views/analytics.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		if (!Html.validCsrfToken(request)) {
			ControllerSupport.flash(request, "danger", "Your form expired. Please try again.");
			response.sendRedirect(request.getContextPath() + "/ActivityController");
			return;
		}
		long userId = ControllerSupport.userId(request);
		String action = request.getParameter("action");
		if ("timer_start".equals(action) || "timer_stop".equals(action)) {
			try {
				if ("timer_start".equals(action)) {
					if (studyModel.getActiveSession(userId) != null)
						throw new IllegalArgumentException("A study timer is already running.");
					Integer courseId = ControllerSupport.optionalPositiveInt(request.getParameter("courseId"));
					if (courseId != null && !courseModel.isAvailableToUser(courseId, userId))
						throw new IllegalArgumentException("Choose one of your courses or a shared catalog course.");
					studyModel.startSession(userId, courseId);
					ControllerSupport.flash(request, "success", "Study timer started.");
				} else {
					if (!studyModel.stopSession(userId))
						throw new IllegalArgumentException("There is no running timer to stop.");
					ControllerSupport.flash(request, "success", "Study session saved.");
				}
			} catch (IllegalArgumentException e) {
				ControllerSupport.flash(request, "danger", e.getMessage());
			} catch (SQLException e) {
				ControllerSupport.flash(request, "danger", "The timer could not be updated. Refresh and try again.");
			}
			response.sendRedirect(request.getContextPath() + "/ActivityController");
			return;
		}
		try {
			if (studyModel.getActiveSession(userId) != null)
				throw new IllegalArgumentException("Stop the running timer before saving another study session.");
			String startValue = request.getParameter("startedAt");
			String minutesValue = request.getParameter("minutes");
			LocalDateTime start;
			try {
				start = LocalDateTime.parse(startValue);
			} catch (RuntimeException e) {
				throw new IllegalArgumentException("Enter a valid study date and start time.");
			}
			int minutes;
			try {
				minutes = Integer.parseInt(minutesValue);
			} catch (RuntimeException e) {
				throw new IllegalArgumentException("Enter a valid study duration.");
			}
			if (minutes < 1 || minutes > 720)
				throw new IllegalArgumentException("Study duration must be between 1 and 720 minutes.");
			if (start.isAfter(LocalDateTime.now()))
				throw new IllegalArgumentException("Study time cannot be in the future.");
			String notes = request.getParameter("notes");
			if (notes != null && notes.length() > 500)
				throw new IllegalArgumentException("Notes must be 500 characters or fewer.");
			StudySession session = new StudySession();
			session.setUserId(ControllerSupport.userId(request));
			session.setCourseId(ControllerSupport.optionalPositiveInt(request.getParameter("courseId")));
			if (session.getCourseId() != null
					&& !courseModel.isAvailableToUser(session.getCourseId(), session.getUserId()))
				throw new IllegalArgumentException("Choose one of your courses or a shared catalog course.");
			session.setStartedAt(start);
			session.setEndedAt(start.plusMinutes(minutes));
			session.setNotes(notes == null || notes.isBlank() ? null : notes.trim());
			studyModel.createSession(session);
			ControllerSupport.flash(request, "success", "Study session saved.");
		} catch (IllegalArgumentException e) {
			ControllerSupport.flash(request, "danger", e.getMessage());
		} catch (SQLException e) {
			ControllerSupport.flash(request, "danger",
					"The study session could not be saved. Check the selected course and try again.");
		}
		response.sendRedirect(request.getContextPath() + "/ActivityController");
	}
}
