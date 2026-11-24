package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.Task;
import aiwa.ulti.Ulti;

/**
 * lessons / lesson_progress / quizzes を扱う DAO
 * （Task = 「完了させるレッスン」として扱う）
 */
public class TaskDAO {

	private static final String BASE_SQL = "SELECT l.lesson_id, l.chapter_id, l.lesson_title, l.content, l.example_code, l.lesson_order, "
			+ "  COALESCE(p.is_completed, 0) AS is_completed, p.completed_at, "
			+ "  q.quiz_id, q.quiz_title "
			+ "FROM lessons l "
			+ "LEFT JOIN lesson_progress p ON p.lesson_id = l.lesson_id AND p.user_id = "
			+ "(SELECT user_id FROM users WHERE email = 'quiz-guest@studyflow.local') "
			+ "LEFT JOIN quizzes q ON q.lesson_id = l.lesson_id ";

	/** チャプター内のレッスン一覧（lesson_order 順） */
	public List<Task> findByChapter(int chapterId) throws SQLException {
		List<Task> list = new ArrayList<>();
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con
						.prepareStatement(BASE_SQL + "WHERE l.chapter_id = ? ORDER BY l.lesson_order")) {
			ps.setInt(1, chapterId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
		}
		return list;
	}

	/** レッスン1件（見つからなければ null） */
	public Task findById(int lessonId) throws SQLException {
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(BASE_SQL + "WHERE l.lesson_id = ?")) {
			ps.setInt(1, lessonId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		}
		return null;
	}

	/** 完了 / 未完了 の切り替え（lesson_id は UNIQUE なので UPSERT） */
	public void setCompleted(int lessonId, boolean completed) throws SQLException {
		String sql = "INSERT INTO lesson_progress (user_id, lesson_id, is_completed, completed_at) "
				+ "VALUES (?, ?, ?, CASE WHEN ? = 1 THEN datetime('now','localtime') ELSE NULL END) "
				+ "ON CONFLICT(user_id, lesson_id) DO UPDATE SET "
				+ "  is_completed = excluded.is_completed, "
				+ "  completed_at = excluded.completed_at";
		int flag = completed ? 1 : 0;
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, ensureQuizGuestUser(con));
			ps.setInt(2, lessonId);
			ps.setInt(3, flag);
			ps.setInt(4, flag);
			ps.executeUpdate();
		}
	}

	private long ensureQuizGuestUser(Connection con) throws SQLException {
		String email = "quiz-guest@studyflow.local";
		try (PreparedStatement insert = con.prepareStatement(
				"INSERT OR IGNORE INTO users(username, email, password_hash) VALUES (?, ?, ?)")) {
			insert.setString(1, "StudyFlow Quiz Guest");
			insert.setString(2, email);
			insert.setString(3, "!disabled-quiz-guest-account!");
			insert.executeUpdate();
		}
		try (PreparedStatement query = con.prepareStatement("SELECT user_id FROM users WHERE email = ?")) {
			query.setString(1, email);
			try (ResultSet rs = query.executeQuery()) {
				if (rs.next()) return rs.getLong(1);
			}
		}
		throw new SQLException("Could not resolve the quiz guest account");
	}

	public int countAll() throws SQLException {
		return count("SELECT COUNT(*) FROM lessons");
	}

	public int countCompleted() throws SQLException {
		return count("SELECT COUNT(*) FROM lesson_progress WHERE user_id = (SELECT user_id FROM users WHERE email = 'quiz-guest@studyflow.local') AND is_completed = 1");
	}

	/** 最近完了したレッスン（新しい順） */
	public List<Task> findRecentCompleted(int limit) throws SQLException {
		List<Task> list = new ArrayList<>();
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(
						BASE_SQL + "WHERE p.is_completed = 1 ORDER BY p.completed_at DESC LIMIT ?")) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
		}
		return list;
	}

	private int count(String sql) throws SQLException {
		try (Connection con = Ulti.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	private Task map(ResultSet rs) throws SQLException {
		Task t = new Task();
		t.setLessonId(rs.getInt("lesson_id"));
		t.setChapterId(rs.getInt("chapter_id"));
		t.setTitle(rs.getString("lesson_title"));
		t.setContent(rs.getString("content"));
		t.setExampleCode(rs.getString("example_code"));
		t.setLessonOrder(rs.getInt("lesson_order"));
		t.setCompleted(rs.getInt("is_completed") == 1);
		t.setCompletedAt(rs.getString("completed_at"));
		int quizId = rs.getInt("quiz_id");
		t.setQuizId(rs.wasNull() ? null : quizId);
		t.setQuizTitle(rs.getString("quiz_title"));
		return t;
	}
}
