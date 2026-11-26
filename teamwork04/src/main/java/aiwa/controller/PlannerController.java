package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.PlannerItem;
import aiwa.model.CourseModel;
import aiwa.model.PlannerModel;
import aiwa.ulti.Html;

@WebServlet("/PlannerController")
public class PlannerController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final PlannerModel planner = new PlannerModel();
	private final CourseModel courses = new CourseModel();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setAttribute("activePlanTab", "planner");
		long userId = ControllerSupport.userId(request);
		LocalDate weekStart = weekStart(request.getParameter("week"));
		boolean showCompleted = "1".equals(request.getParameter("showCompleted"));
		request.setAttribute("weekStart", weekStart);
		request.setAttribute("weekEnd", weekStart.plusDays(6));
		request.setAttribute("showCompleted", showCompleted);
		Map<LocalDate, List<PlannerItem>> itemsByDay = new LinkedHashMap<>();
		for (int day = 0; day < 7; day++)
			itemsByDay.put(weekStart.plusDays(day), new ArrayList<>());
		request.setAttribute("itemsByDay", itemsByDay);
		try {
			for (PlannerItem item : planner.getWeek(userId, weekStart, showCompleted)) {
				List<PlannerItem> dayItems = itemsByDay.get(item.getStartsAt().toLocalDate());
				if (dayItems != null)
					dayItems.add(item);
			}
			request.setAttribute("courses", courses.getCourses(userId));
		} catch (SQLException e) {
			request.setAttribute("pageError", "The planner could not be loaded. Please try again.");
		}
		ControllerSupport.exposeFlash(request);
		request.getRequestDispatcher("/WEB-INF/views/planner.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		LocalDate weekStart = weekStart(request.getParameter("week"));
		String redirect = request.getContextPath() + "/PlannerController?week=" + weekStart;
		if (!Html.validCsrfToken(request)) {
			ControllerSupport.flash(request, "danger", "Your form expired. Please try again.");
			response.sendRedirect(redirect);
			return;
		}

		long userId = ControllerSupport.userId(request);
		String action = request.getParameter("action");
		try {
			if ("delete".equals(action)) {
				Integer eventId = ControllerSupport.positiveInt(request.getParameter("eventId"));
				if (eventId == null || !planner.deleteEvent(eventId, userId))
					throw new IllegalArgumentException("That planner item no longer exists.");
				ControllerSupport.flash(request, "success", "Plan removed.");
			} else if ("setCompleted".equals(action)) {
				Integer eventId = ControllerSupport.positiveInt(request.getParameter("eventId"));
				String completedValue = request.getParameter("completed");
				if (eventId == null || (!"true".equals(completedValue) && !"false".equals(completedValue)))
					throw new IllegalArgumentException("Choose a valid study plan.");
				boolean completed = Boolean.parseBoolean(completedValue);
				if (!planner.setCompleted(eventId, userId, completed))
					throw new IllegalArgumentException("That study plan could not be found.");
				ControllerSupport.flash(request, "success",
						completed ? "Study plan marked done." : "Study plan reopened.");
			} else if ("create".equals(action)) {
				PlannerItem item = readEvent(request);
				Integer courseId = ControllerSupport.optionalPositiveInt(request.getParameter("courseId"));
				if (courseId != null && !courses.isAvailableToUser(courseId, userId))
					throw new IllegalArgumentException("Choose one of your courses or a shared catalog course.");
				if ("study".equals(item.getType())
						&& planner.hasStudyOverlap(userId, item.getStartsAt(), item.getEndsAt())) {
					throw new IllegalArgumentException(
							"This study block overlaps another planned or logged study session.");
				}
				planner.create(item, userId, courseId);
				ControllerSupport.flash(request, "success", "Added to your weekly planner.");
			} else {
				throw new IllegalArgumentException("Choose a valid planner action.");
			}
		} catch (IllegalArgumentException e) {
			ControllerSupport.flash(request, "danger", e.getMessage());
		} catch (SQLException e) {
			ControllerSupport.flash(request, "danger",
					"The planner item could not be saved. Check the selected course and try again.");
		}
		response.sendRedirect(redirect);
	}

	private PlannerItem readEvent(HttpServletRequest request) {
		String type = request.getParameter("eventType");
		if (!"study".equals(type) && !"exam".equals(type))
			throw new IllegalArgumentException("Choose a study block or exam.");
		String title = request.getParameter("title");
		if (title == null || title.trim().isEmpty() || title.trim().length() > 120)
			throw new IllegalArgumentException("Enter a title of up to 120 characters.");
		LocalDateTime startsAt = parseDateTime(request.getParameter("startsAt"), "Enter a valid start date and time.");
		if (startsAt.isBefore(LocalDateTime.now().minusMinutes(1)))
			throw new IllegalArgumentException("Planner items must be scheduled in the future.");

		LocalDateTime endsAt = null;
		if ("study".equals(type)) {
			endsAt = parseDateTime(request.getParameter("endsAt"), "Enter an end date and time for the study block.");
			long minutes = Duration.between(startsAt, endsAt).toMinutes();
			if (minutes < 15 || minutes > 720)
				throw new IllegalArgumentException("Study blocks must be between 15 minutes and 12 hours.");
		}

		PlannerItem item = new PlannerItem();
		item.setType(type);
		item.setTitle(title.trim());
		item.setStartsAt(startsAt);
		item.setEndsAt(endsAt);
		return item;
	}

	private LocalDateTime parseDateTime(String value, String message) {
		try {
			return LocalDateTime.parse(value);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException(message);
		}
	}

	private LocalDate weekStart(String value) {
		LocalDate date = LocalDate.now();
		if (value != null && !value.isBlank()) {
			try {
				date = LocalDate.parse(value);
			} catch (DateTimeParseException ignored) {
				/* Fall back to the current week for invalid links. */ }
		}
		return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}
}
