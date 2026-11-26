package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.dao.Database;
import aiwa.entity.Task;
import aiwa.model.TaskNoteModel;
import aiwa.model.CourseModel;
import aiwa.model.TaskModel;
import aiwa.ulti.Html;
import aiwa.service.ActivityService;

@WebServlet("/TaskController")
public class TaskController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final TaskModel model = new TaskModel();
	private final TaskNoteModel notes = new TaskNoteModel();
	private final CourseModel courses = new CourseModel();
	private final ActivityService activityService = new ActivityService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setAttribute("activePlanTab", "tasks");
		String status = allowed(request.getParameter("status"), "todo", "in_progress", "done");
		String priority = allowed(request.getParameter("priority"), "low", "medium", "high");
		Integer courseId = ControllerSupport.positiveInt(request.getParameter("courseId"));
		request.setAttribute("filterStatus", status == null ? "" : status);
		request.setAttribute("filterPriority", priority == null ? "" : priority);
		request.setAttribute("filterCourseId", courseId == null ? 0 : courseId);
		try {
			long userId = ControllerSupport.userId(request);
			request.setAttribute("tasks", model.getTasks(userId, status, priority, courseId, 0));
			request.setAttribute("allUserTasks", model.getTasks(userId, null, null, null, 0));
			request.setAttribute("taskNotes", notes.getNotes(userId));
			request.setAttribute("courses", courses.getCourses(userId));
		} catch (SQLException e) {
			request.setAttribute("pageError", "Tasks could not be loaded. Please try again.");
		}
		ControllerSupport.exposeFlash(request);
		request.getRequestDispatcher("/WEB-INF/views/tasks.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		String redirect = "dashboard".equals(request.getParameter("returnTo"))
				? request.getContextPath() + "/DashboardController"
				: request.getContextPath() + "/TaskController";
		if (!Html.validCsrfToken(request)) {
			ControllerSupport.flash(request, "danger", "Your form expired. Please try again.");
			response.sendRedirect(redirect);
			return;
		}
		String action = request.getParameter("action");
		long userId = ControllerSupport.userId(request);
		try {
			if ("delete".equals(action) || "toggle".equals(action)) {
				Integer id = ControllerSupport.positiveInt(request.getParameter("taskId"));
				if (id == null)
					throw new IllegalArgumentException("Select a valid task.");
				boolean changed = "delete".equals(action)
						? model.delete(id, userId)
						: model.complete(id, userId, "true".equals(request.getParameter("completed")));
				if (!changed)
					throw new IllegalArgumentException("That task no longer exists.");
				if ("toggle".equals(action) && "true".equals(request.getParameter("completed")))
					activityService.recordAction(userId, "task_completed");
				ControllerSupport.flash(request, "success",
						"delete".equals(action) ? "Task deleted." : "Task status updated.");
			} else {
				Task task = readTask(request, userId);
				if ("update".equals(action)) {
					Integer id = ControllerSupport.positiveInt(request.getParameter("taskId"));
					if (id == null)
						throw new IllegalArgumentException("Select a valid task to edit.");
					task.setTaskId(id);
					if (!model.update(task))
						throw new IllegalArgumentException("That task no longer exists.");
					activityService.recordAction(userId, "task_saved");
					if ("done".equals(task.getStatus())) activityService.recordAction(userId, "task_completed");
					ControllerSupport.flash(request, "success", "Task updated.");
				} else if ("create".equals(action)) {
					model.create(task);
					activityService.recordAction(userId, "task_saved");
					if ("done".equals(task.getStatus())) activityService.recordAction(userId, "task_completed");
					ControllerSupport.flash(request, "success", "Task added.");
				} else
					throw new IllegalArgumentException("Choose a valid task action.");
			}
		} catch (IllegalArgumentException e) {
			ControllerSupport.flash(request, "danger", e.getMessage());
		} catch (SQLException e) {
			ControllerSupport.flash(request, "danger",
					"The task could not be saved. Check the selected course and try again.");
		}
		response.sendRedirect(redirect);
	}

	private Task readTask(HttpServletRequest request, long userId) throws SQLException {
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		String priority = request.getParameter("priority");
		String status = request.getParameter("status");
		if (title == null || title.trim().isEmpty() || title.trim().length() > 120)
			throw new IllegalArgumentException("Task title is required (up to 120 characters).");
		if (description != null && description.length() > 1000)
			throw new IllegalArgumentException("Description must be 1,000 characters or fewer.");
		if (!"low".equals(priority) && !"medium".equals(priority) && !"high".equals(priority))
			throw new IllegalArgumentException("Choose a valid priority.");
		if (!"todo".equals(status) && !"in_progress".equals(status) && !"done".equals(status))
			status = "todo";
		Task task = new Task();
		task.setUserId(userId);
		task.setTitle(title.trim());
		task.setDescription(description == null || description.isBlank() ? null : description.trim());
		task.setPriority(priority);
		task.setStatus(status);
		task.setCourseId(ControllerSupport.optionalPositiveInt(request.getParameter("courseId")));
		if (task.getCourseId() != null && !courses.isAvailableToUser(task.getCourseId(), userId))
			throw new IllegalArgumentException("Choose one of your courses or a shared catalog course.");
		String due = request.getParameter("dueAt");
		if (due != null && !due.isBlank()) {
			try {
				task.setDueAt(LocalDateTime.parse(due));
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Enter a valid due date and time.");
			}
		}
		return task;
	}

	private String allowed(String value, String... choices) {
		if (value == null || value.isBlank())
			return null;
		for (String choice : choices)
			if (choice.equals(value))
				return value;
		return null;
	}

}
