package aiwa.model;

import java.sql.SQLException;
import java.util.List;

import aiwa.dao.TaskNoteDAO;
import aiwa.entity.TaskNote;

public class TaskNoteModel {
	private final TaskNoteDAO dao = new TaskNoteDAO();
	public List<TaskNote> getNotes(long userId) throws SQLException {
		return dao.findAll(userId);
	}
	public boolean ownsTask(int taskId, long userId) throws SQLException {
		return dao.ownsTask(taskId, userId);
	}
	public int create(TaskNote note) throws SQLException {
		return dao.create(note);
	}
	public boolean update(TaskNote note) throws SQLException {
		return dao.update(note);
	}
	public boolean delete(int noteId, long userId) throws SQLException {
		return dao.delete(noteId, userId);
	}
}
