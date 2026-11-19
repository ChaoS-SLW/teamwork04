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
		String sql = "SELECT c.course_id,c.category_id,c.course_name,c.description,c.level,ca.category_name, "
				+ "COUNT(t.task_id) AS task_count, COALESCE(SUM(CASE WHEN t.status='done' THEN 1 ELSE 0 END),0) AS done_count "
				+ "FROM courses c JOIN categories ca ON ca.category_id=c.category_id "
				+ "LEFT JOIN tasks t ON t.course_id=c.course_id AND t.user_id=? "
				+ "GROUP BY c.course_id ORDER BY c.course_name COLLATE NOCASE";
		List<Study> courses = new ArrayList<>();
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					Study course = new Study();
					course.setCourseId(rs.getInt("course_id"));
					course.setCategoryId(rs.getInt("category_id"));
					course.setCourseName(rs.getString("course_name"));
					course.setCategoryName(rs.getString("category_name"));
					course.setDescription(rs.getString("description"));
					course.setLevel(rs.getString("level"));
					course.setTaskCount(rs.getInt("task_count"));
					course.setCompletedTaskCount(rs.getInt("done_count"));
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
		String sql = "INSERT INTO courses(category_id,course_name,description,level) VALUES(?,?,?,?)";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, course.getCategoryId());
			statement.setString(2, course.getCourseName());
			statement.setString(3, course.getDescription());
			statement.setString(4, course.getLevel());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) { return keys.next() ? keys.getInt(1) : -1; }
		}
	}

	public boolean update(Study course) throws SQLException {
		String sql = "UPDATE courses SET category_id=?,course_name=?,description=?,level=? WHERE course_id=?";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, course.getCategoryId());
			statement.setString(2, course.getCourseName());
			statement.setString(3, course.getDescription());
			statement.setString(4, course.getLevel());
			statement.setInt(5, course.getCourseId());
			return statement.executeUpdate() == 1;
		}
	}

	public boolean delete(int courseId) throws SQLException {
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM courses WHERE course_id=?")) {
			statement.setInt(1, courseId);
			return statement.executeUpdate() == 1;
		}
	}
}
