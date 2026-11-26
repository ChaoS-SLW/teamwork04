package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.TaskNote;

public class TaskNoteDAO {
	public List<TaskNote> findAll(long userId) throws SQLException {
		String sql = "SELECT n.note_id,n.user_id,n.task_id,n.title,n.note_text,n.updated_at,t.title AS task_title "
				+ "FROM task_notes n LEFT JOIN tasks t ON t.task_id=n.task_id AND t.user_id=n.user_id "
				+ "WHERE n.user_id=? ORDER BY n.updated_at DESC,n.note_id DESC LIMIT 100";
		List<TaskNote> notes = new ArrayList<>();
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					TaskNote note = new TaskNote();
					note.setNoteId(rs.getInt("note_id"));
					note.setUserId(rs.getLong("user_id"));
					int taskId = rs.getInt("task_id");
					note.setTaskId(rs.wasNull() ? null : taskId);
					note.setTitle(rs.getString("title"));
					note.setNoteText(rs.getString("note_text"));
					note.setTaskTitle(rs.getString("task_title"));
					note.setUpdatedAt(parse(rs.getString("updated_at")));
					notes.add(note);
				}
			}
		}
		return notes;
	}

	public boolean ownsTask(int taskId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection
						.prepareStatement("SELECT 1 FROM tasks WHERE task_id=? AND user_id=?")) {
			statement.setInt(1, taskId);
			statement.setLong(2, userId);
			try (ResultSet rs = statement.executeQuery()) {
				return rs.next();
			}
		}
	}

	public int create(TaskNote note) throws SQLException {
		String sql = "INSERT INTO task_notes(user_id,task_id,title,note_text) VALUES(?,?,?,?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, note.getUserId());
			setNullableInt(statement, 2, note.getTaskId());
			statement.setString(3, note.getTitle());
			statement.setString(4, note.getNoteText());
			return statement.executeUpdate();
		}
	}

	public boolean update(TaskNote note) throws SQLException {
		String sql = "UPDATE task_notes SET task_id=?,title=?,note_text=?,updated_at=CURRENT_TIMESTAMP WHERE note_id=? AND user_id=?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			setNullableInt(statement, 1, note.getTaskId());
			statement.setString(2, note.getTitle());
			statement.setString(3, note.getNoteText());
			statement.setInt(4, note.getNoteId());
			statement.setLong(5, note.getUserId());
			return statement.executeUpdate() == 1;
		}
	}

	public boolean delete(int noteId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection
						.prepareStatement("DELETE FROM task_notes WHERE note_id=? AND user_id=?")) {
			statement.setInt(1, noteId);
			statement.setLong(2, userId);
			return statement.executeUpdate() == 1;
		}
	}

	private static LocalDateTime parse(String value) {
		return value == null ? null : LocalDateTime.parse(value.replace(' ', 'T'));
	}
	private static void setNullableInt(PreparedStatement statement, int index, Integer value) throws SQLException {
		if (value == null)
			statement.setNull(index, java.sql.Types.INTEGER);
		else
			statement.setInt(index, value);
	}
}
