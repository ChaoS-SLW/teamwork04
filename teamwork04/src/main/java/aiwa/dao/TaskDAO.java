package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.Task;

public class TaskDAO {
	private static final String SELECT = "SELECT t.*,c.course_name FROM tasks t LEFT JOIN courses c ON c.course_id=t.course_id ";

	public List<Task> findAll(long userId, String status, String priority, Integer courseId, int limit) throws SQLException {
		StringBuilder sql = new StringBuilder(SELECT).append("WHERE t.user_id=? ");
		List<Object> values = new ArrayList<>(); values.add(userId);
		if (status != null && !status.isBlank()) { sql.append("AND t.status=? "); values.add(status); }
		if (priority != null && !priority.isBlank()) { sql.append("AND t.priority=? "); values.add(priority); }
		if (courseId != null && courseId > 0) { sql.append("AND t.course_id=? "); values.add(courseId); }
		if (limit > 0) sql.append("AND t.status <> 'done' ");
		sql.append("ORDER BY CASE WHEN t.status='done' THEN 1 ELSE 0 END, CASE WHEN t.due_at IS NULL THEN 1 ELSE 0 END, t.due_at, t.created_at DESC");
		if (limit > 0) sql.append(" LIMIT ?");
		List<Task> tasks = new ArrayList<>();
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
			int i = 1; for (Object value : values) statement.setObject(i++, value);
			if (limit > 0) statement.setInt(i, limit);
			try (ResultSet rs = statement.executeQuery()) { while (rs.next()) tasks.add(map(rs)); }
		}
		return tasks;
	}

	public int create(Task task) throws SQLException {
		String sql = "INSERT INTO tasks(user_id,course_id,title,description,due_at,priority,status,completed_at) VALUES(?,?,?,?,?,?,?,CASE WHEN ?='done' THEN CURRENT_TIMESTAMP ELSE NULL END)";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, task.getUserId()); setNullableInt(statement, 2, task.getCourseId());
			statement.setString(3, task.getTitle()); statement.setString(4, task.getDescription());
			statement.setString(5, format(task.getDueAt())); statement.setString(6, task.getPriority()); statement.setString(7, task.getStatus()); statement.setString(8, task.getStatus());
			return statement.executeUpdate();
		}
	}

	public boolean update(Task task) throws SQLException {
		String sql = "UPDATE tasks SET course_id=?,title=?,description=?,due_at=?,priority=?,status=?,completed_at=CASE WHEN ?='done' THEN COALESCE(completed_at,CURRENT_TIMESTAMP) ELSE NULL END WHERE task_id=? AND user_id=?";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			setNullableInt(statement, 1, task.getCourseId()); statement.setString(2, task.getTitle()); statement.setString(3, task.getDescription());
			statement.setString(4, format(task.getDueAt())); statement.setString(5, task.getPriority()); statement.setString(6, task.getStatus());
			statement.setString(7, task.getStatus()); statement.setInt(8, task.getTaskId()); statement.setLong(9, task.getUserId());
			return statement.executeUpdate() == 1;
		}
	}

	public boolean setCompleted(int taskId, long userId, boolean completed) throws SQLException {
		String sql = "UPDATE tasks SET status=?,completed_at=? WHERE task_id=? AND user_id=?";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, completed ? "done" : "todo");
			statement.setString(2, completed ? LocalDateTime.now().toString().replace('T', ' ') : null);
			statement.setInt(3, taskId); statement.setLong(4, userId);
			return statement.executeUpdate() == 1;
		}
	}

	public boolean delete(int taskId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM tasks WHERE task_id=? AND user_id=?")) {
			statement.setInt(1, taskId); statement.setLong(2, userId); return statement.executeUpdate() == 1;
		}
	}

	public int count(long userId, String status) throws SQLException {
		String sql = "SELECT COUNT(*) FROM tasks WHERE user_id=?" + (status == null ? "" : " AND status=?");
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId); if (status != null) statement.setString(2, status);
			try (ResultSet rs = statement.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
		}
	}

	private static Task map(ResultSet rs) throws SQLException {
		Task task = new Task(); task.setTaskId(rs.getInt("task_id")); task.setUserId(rs.getLong("user_id"));
		int courseId = rs.getInt("course_id"); task.setCourseId(rs.wasNull() ? null : courseId);
		task.setCourseName(rs.getString("course_name")); task.setTitle(rs.getString("title")); task.setDescription(rs.getString("description"));
		task.setDueAt(parse(rs.getString("due_at"))); task.setPriority(rs.getString("priority")); task.setStatus(rs.getString("status"));
		task.setCreatedAt(parse(rs.getString("created_at"))); task.setCompletedAt(parse(rs.getString("completed_at"))); return task;
	}
	private static String format(LocalDateTime value) { return value == null ? null : value.toString().replace('T', ' '); }
	private static LocalDateTime parse(String value) { return value == null ? null : LocalDateTime.parse(value.replace(' ', 'T')); }
	private static void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException { if (value == null) ps.setNull(index, java.sql.Types.INTEGER); else ps.setInt(index, value); }
}
