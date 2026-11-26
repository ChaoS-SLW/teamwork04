package aiwa.model;

import java.sql.SQLException;
import java.util.List;

import aiwa.dao.TaskDAO;
import aiwa.entity.Task;

public class TaskModel {
	private final TaskDAO dao = new TaskDAO();
	public List<Task> getTasks(long userId, String status, String priority, Integer courseId, int limit)
			throws SQLException {
		return dao.findAll(userId, status, priority, courseId, limit);
	}
	public int create(Task task) throws SQLException {
		return dao.create(task);
	}
	public boolean update(Task task) throws SQLException {
		return dao.update(task);
	}
	public boolean complete(int taskId, long userId, boolean completed) throws SQLException {
		return dao.setCompleted(taskId, userId, completed);
	}
	public boolean delete(int taskId, long userId) throws SQLException {
		return dao.delete(taskId, userId);
	}
	public int count(long userId, String status) throws SQLException {
		return dao.count(userId, status);
	}
}
