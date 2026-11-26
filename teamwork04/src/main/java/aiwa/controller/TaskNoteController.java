package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.TaskNote;
import aiwa.model.TaskNoteModel;
import aiwa.ulti.Html;
import aiwa.service.ActivityService;

@WebServlet("/TaskNoteController")
public class TaskNoteController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final TaskNoteModel model = new TaskNoteModel();
	private final ActivityService activityService = new ActivityService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		String redirect = request.getContextPath() + "/TaskController#task-notes";
		if (!Html.validCsrfToken(request)) {
			ControllerSupport.flash(request, "danger", "Your form expired. Please try again.");
			response.sendRedirect(redirect);
			return;
		}

		long userId = ControllerSupport.userId(request);
		String action = request.getParameter("action");
		try {
			if ("delete".equals(action)) {
				Integer noteId = ControllerSupport.positiveInt(request.getParameter("noteId"));
				if (noteId == null || !model.delete(noteId, userId))
					throw new IllegalArgumentException("That note could not be found.");
				ControllerSupport.flash(request, "success", "Note deleted.");
			} else if ("create".equals(action) || "update".equals(action)) {
				TaskNote note = readNote(request, userId);
				if ("create".equals(action)) {
					model.create(note);
					activityService.recordAction(userId, "note_saved");
					ControllerSupport.flash(request, "success", "Note saved.");
				} else {
					Integer noteId = ControllerSupport.positiveInt(request.getParameter("noteId"));
					if (noteId == null)
						throw new IllegalArgumentException("Choose a valid note to update.");
					note.setNoteId(noteId);
					if (!model.update(note))
						throw new IllegalArgumentException("That note could not be found.");
					activityService.recordAction(userId, "note_saved");
					ControllerSupport.flash(request, "success", "Note updated.");
				}
			} else {
				throw new IllegalArgumentException("Choose a valid note action.");
			}
		} catch (IllegalArgumentException e) {
			ControllerSupport.flash(request, "danger", e.getMessage());
		} catch (SQLException e) {
			ControllerSupport.flash(request, "danger", "The note could not be saved. Please try again.");
		}
		response.sendRedirect(redirect);
	}

	private TaskNote readNote(HttpServletRequest request, long userId) throws SQLException {
		String title = request.getParameter("title");
		String noteText = request.getParameter("noteText");
		if (title == null || title.trim().isEmpty() || title.trim().length() > 100)
			throw new IllegalArgumentException("Enter a note title of up to 100 characters.");
		if (noteText == null || noteText.trim().isEmpty() || noteText.trim().length() > 4000)
			throw new IllegalArgumentException("Write a note of up to 4,000 characters.");
		Integer taskId = ControllerSupport.optionalPositiveInt(request.getParameter("taskId"));
		if (taskId != null && !model.ownsTask(taskId, userId))
			throw new IllegalArgumentException("Choose one of your own tasks for this note.");
		TaskNote note = new TaskNote();
		note.setUserId(userId);
		note.setTaskId(taskId);
		note.setTitle(title.trim());
		note.setNoteText(noteText.trim());
		return note;
	}
}
