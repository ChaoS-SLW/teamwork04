package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.StudySession;

public class StudysessionDAO {
	public StudySession findActive(long userId) throws SQLException {
		String sql = "SELECT s.*,c.course_name FROM study_sessions s LEFT JOIN courses c ON c.course_id=s.course_id WHERE s.user_id=? AND s.ended_at IS NULL ORDER BY s.started_at DESC LIMIT 1";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			try (ResultSet rs = statement.executeQuery()) {
				if (!rs.next())
					return null;
				StudySession session = new StudySession();
				session.setSessionId(rs.getInt("session_id"));
				session.setUserId(rs.getLong("user_id"));
				int courseId = rs.getInt("course_id");
				session.setCourseId(rs.wasNull() ? null : courseId);
				session.setCourseName(rs.getString("course_name"));
				session.setStartedAt(parse(rs.getString("started_at")));
				session.setNotes(rs.getString("notes"));
				return session;
			}
		}
	}

	public int start(StudySession session) throws SQLException {
		String sql = "INSERT INTO study_sessions(user_id,course_id,started_at,notes) VALUES(?,?,?,?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, session.getUserId());
			setNullableInt(statement, 2, session.getCourseId());
			statement.setString(3, format(session.getStartedAt()));
			statement.setString(4, session.getNotes());
			return statement.executeUpdate();
		}
	}

	public boolean stopActive(long userId) throws SQLException {
		String now = format(LocalDateTime.now());
		String sql = "UPDATE study_sessions SET ended_at=? WHERE session_id=(SELECT session_id FROM study_sessions WHERE user_id=? AND ended_at IS NULL ORDER BY started_at DESC LIMIT 1) AND started_at < ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, now);
			statement.setLong(2, userId);
			statement.setString(3, now);
			return statement.executeUpdate() == 1;
		}
	}

	public int create(StudySession session) throws SQLException {
		String sql = "INSERT INTO study_sessions(user_id,course_id,started_at,ended_at,notes) VALUES(?,?,?,?,?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, session.getUserId());
			if (session.getCourseId() == null)
				statement.setNull(2, java.sql.Types.INTEGER);
			else
				statement.setInt(2, session.getCourseId());
			statement.setString(3, format(session.getStartedAt()));
			statement.setString(4, format(session.getEndedAt()));
			statement.setString(5, session.getNotes());
			return statement.executeUpdate();
		}
	}

	public List<StudySession> findRecent(long userId, int limit) throws SQLException {
		List<StudySession> sessions = new ArrayList<>();
		String sql = "SELECT s.*,c.course_name FROM study_sessions s LEFT JOIN courses c ON c.course_id=s.course_id WHERE s.user_id=? AND s.ended_at IS NOT NULL ORDER BY s.started_at DESC LIMIT ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setInt(2, limit);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					StudySession session = new StudySession();
					session.setSessionId(rs.getInt("session_id"));
					session.setUserId(rs.getLong("user_id"));
					int courseId = rs.getInt("course_id");
					session.setCourseId(rs.wasNull() ? null : courseId);
					session.setCourseName(rs.getString("course_name"));
					session.setStartedAt(parse(rs.getString("started_at")));
					session.setEndedAt(parse(rs.getString("ended_at")));
					session.setNotes(rs.getString("notes"));
					sessions.add(session);
				}
			}
		}
		return sessions;
	}

	public long totalMinutes(long userId) throws SQLException {
		String sql = "SELECT COALESCE(SUM((julianday(ended_at)-julianday(started_at))*1440),0) FROM study_sessions WHERE user_id=? AND ended_at IS NOT NULL";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			try (ResultSet rs = statement.executeQuery()) {
				return rs.next() ? Math.round(rs.getDouble(1)) : 0;
			}
		}
	}

	private static String format(LocalDateTime value) {
		return value == null ? null : value.toString().replace('T', ' ');
	}
	private static LocalDateTime parse(String value) {
		return value == null ? null : LocalDateTime.parse(value.replace(' ', 'T'));
	}
	private static void setNullableInt(PreparedStatement statement, int index, Integer value) throws SQLException {
		if (value == null) statement.setNull(index, java.sql.Types.INTEGER);
		else statement.setInt(index, value);
	}
}
