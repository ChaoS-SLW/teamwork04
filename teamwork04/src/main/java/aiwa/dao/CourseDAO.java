package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.Category;
import aiwa.entity.Study;

public class CourseDAO {
	public List<Study> findAll(long userId) throws SQLException {
		String sql = "SELECT c.course_id,c.user_id,c.category_id,c.course_name,c.description,c.level,ca.category_name, "
				+ "COUNT(t.task_id) AS task_count, COALESCE(SUM(CASE WHEN t.status='done' THEN 1 ELSE 0 END),0) AS done_count "
				+ ",(SELECT COUNT(DISTINCT q.quiz_id) FROM chapters ch JOIN lessons l ON l.chapter_id=ch.chapter_id "
				+ "JOIN quizzes q ON q.lesson_id=l.lesson_id JOIN questions question ON question.quiz_id=q.quiz_id "
				+ "WHERE ch.course_id=c.course_id) AS quiz_count "
				+ "FROM courses c JOIN categories ca ON ca.category_id=c.category_id "
				+ "LEFT JOIN tasks t ON t.course_id=c.course_id AND t.user_id=? "
				+ "WHERE c.user_id IS NULL OR c.user_id=? GROUP BY c.course_id ORDER BY c.course_name COLLATE NOCASE";
		List<Study> courses = new ArrayList<>();
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setLong(2, userId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					Study course = new Study();
					course.setCourseId(rs.getInt("course_id"));
					long ownerId = rs.getLong("user_id"); course.setUserId(rs.wasNull() ? null : ownerId);
					course.setCategoryId(rs.getInt("category_id"));
					course.setCourseName(rs.getString("course_name"));
					course.setCategoryName(rs.getString("category_name"));
					course.setDescription(rs.getString("description"));
					course.setLevel(rs.getString("level"));
					course.setTaskCount(rs.getInt("task_count"));
					course.setCompletedTaskCount(rs.getInt("done_count"));
					course.setQuizCount(rs.getInt("quiz_count"));
					courses.add(course);
				}
			}
		}
		return courses;
	}

	public List<Category> findCategories() throws SQLException {
		List<Category> categories = new ArrayList<>();
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement("SELECT category_id,category_name FROM categories ORDER BY category_name COLLATE NOCASE");
				ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				Category category = new Category();
				category.setCategoryId(rs.getInt("category_id"));
				category.setCategoryName(rs.getString("category_name"));
				categories.add(category);
			}
		}
		return categories;
	}

	public int create(Study course) throws SQLException {
		String sql = "INSERT INTO courses(user_id,category_id,course_name,description,level) VALUES(?,?,?,?,?)";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			statement.setLong(1, course.getUserId());
			statement.setInt(2, course.getCategoryId());
			statement.setString(3, course.getCourseName());
			statement.setString(4, course.getDescription());
			statement.setString(5, course.getLevel());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) { return keys.next() ? keys.getInt(1) : -1; }
		}
	}

	public boolean update(Study course, long userId) throws SQLException {
		String sql = "UPDATE courses SET category_id=?,course_name=?,description=?,level=? WHERE course_id=? AND user_id=?";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, course.getCategoryId());
			statement.setString(2, course.getCourseName());
			statement.setString(3, course.getDescription());
			statement.setString(4, course.getLevel());
			statement.setInt(5, course.getCourseId());
			statement.setLong(6, userId);
			return statement.executeUpdate() == 1;
		}
	}

	public boolean delete(int courseId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM courses WHERE course_id=? AND user_id=?")) {
			statement.setInt(1, courseId); statement.setLong(2, userId);
			return statement.executeUpdate() == 1;
		}
	}

	public boolean isAvailableToUser(int courseId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM courses WHERE course_id=? AND (user_id IS NULL OR user_id=?)")) {
			statement.setInt(1, courseId); statement.setLong(2, userId);
			try (ResultSet rs = statement.executeQuery()) { return rs.next(); }
		}
	}
}
