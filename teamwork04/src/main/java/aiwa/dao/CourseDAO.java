package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.Category;
import aiwa.entity.Lesson;
import aiwa.entity.Study;

public class CourseDAO {
	public List<Study> findAll(long userId) throws SQLException {
		String sql = "SELECT c.course_id,c.user_id,c.category_id,c.course_name,c.description,c.level,ca.category_name, "
				+ "COUNT(t.task_id) AS task_count, COALESCE(SUM(CASE WHEN t.status='done' THEN 1 ELSE 0 END),0) AS done_count, "
				+ "COALESCE(SUM(CASE WHEN t.status='in_progress' THEN 1 ELSE 0 END),0) AS in_progress_count, "
				+ "COALESCE(MAX(qp.attempted_quiz_count),0) AS attempted_quiz_count, "
				+ "COALESCE(MAX(qp.best_correct_count),0) AS best_quiz_correct_count "
				+ ",(SELECT COUNT(question.question_id) FROM chapters question_chapter "
				+ "JOIN lessons question_lesson ON question_lesson.chapter_id=question_chapter.chapter_id "
				+ "JOIN quizzes question_quiz ON question_quiz.lesson_id=question_lesson.lesson_id "
				+ "JOIN questions question ON question.quiz_id=question_quiz.quiz_id "
				+ "WHERE question_chapter.course_id=c.course_id) AS quiz_question_count "
				+ ",(SELECT COUNT(DISTINCT q.quiz_id) FROM chapters ch JOIN lessons l ON l.chapter_id=ch.chapter_id "
				+ "JOIN quizzes q ON q.lesson_id=l.lesson_id JOIN questions question ON question.quiz_id=q.quiz_id "
				+ "WHERE ch.course_id=c.course_id) AS quiz_count "
				+ "FROM courses c JOIN categories ca ON ca.category_id=c.category_id "
				+ "LEFT JOIN (SELECT best.user_id,ch.course_id,COUNT(DISTINCT best.quiz_id) AS attempted_quiz_count, "
				+ "SUM(best.score) AS best_correct_count FROM (SELECT user_id,quiz_id,MAX(score) AS score "
				+ "FROM quiz_results GROUP BY user_id,quiz_id) best "
				+ "JOIN quizzes q ON q.quiz_id=best.quiz_id JOIN lessons l ON l.lesson_id=q.lesson_id "
				+ "JOIN chapters ch ON ch.chapter_id=l.chapter_id GROUP BY best.user_id,ch.course_id) qp "
				+ "ON qp.user_id=? AND qp.course_id=c.course_id "
				+ "LEFT JOIN tasks t ON t.course_id=c.course_id AND t.user_id=? "
				+ "WHERE c.user_id IS NULL OR c.user_id=? GROUP BY c.course_id ORDER BY c.course_name COLLATE NOCASE";
		List<Study> courses = new ArrayList<>();
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setLong(2, userId);
			statement.setLong(3, userId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					Study course = new Study();
					course.setCourseId(rs.getInt("course_id"));
					long ownerId = rs.getLong("user_id");
					course.setUserId(rs.wasNull() ? null : ownerId);
					course.setCategoryId(rs.getInt("category_id"));
					course.setCourseName(rs.getString("course_name"));
					course.setCategoryName(rs.getString("category_name"));
					course.setDescription(rs.getString("description"));
					course.setLevel(rs.getString("level"));
					course.setTaskCount(rs.getInt("task_count"));
					course.setCompletedTaskCount(rs.getInt("done_count"));
					course.setInProgressTaskCount(rs.getInt("in_progress_count"));
					course.setAttemptedQuizCount(rs.getInt("attempted_quiz_count"));
					course.setBestQuizCorrectCount(rs.getInt("best_quiz_correct_count"));
					course.setTotalQuizQuestionCount(rs.getInt("quiz_question_count"));
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
				PreparedStatement statement = connection.prepareStatement(
						"SELECT category_id,category_name FROM categories ORDER BY category_name COLLATE NOCASE");
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

	public List<Lesson> findLessons(int courseId, long userId) throws SQLException {
		String sql = "SELECT l.lesson_id, ch.chapter_name, ch.chapter_order, l.lesson_title, l.content, l.example_code, "
				+ "l.lesson_order, q.quiz_id, "
				+ "(SELECT COUNT(*) FROM questions question WHERE question.quiz_id = q.quiz_id) AS question_count "
				+ "FROM lessons l "
				+ "JOIN chapters ch ON ch.chapter_id = l.chapter_id "
				+ "JOIN courses c ON c.course_id = ch.course_id "
				+ "LEFT JOIN quizzes q ON q.lesson_id = l.lesson_id "
				+ "WHERE c.course_id = ? AND (c.user_id IS NULL OR c.user_id = ?) "
				+ "ORDER BY ch.chapter_order, l.lesson_order";
		List<Lesson> lessons = new ArrayList<>();

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, courseId);
			statement.setLong(2, userId);

			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					Lesson lesson = new Lesson();
					lesson.setLessonId(result.getInt("lesson_id"));
					lesson.setChapterName(result.getString("chapter_name"));
					lesson.setChapterOrder(result.getInt("chapter_order"));
					lesson.setLessonTitle(result.getString("lesson_title"));
					lesson.setContent(result.getString("content"));
					lesson.setExampleCode(result.getString("example_code"));
					lesson.setLessonOrder(result.getInt("lesson_order"));
					lesson.setQuizId(result.getInt("quiz_id"));
					lesson.setQuestionCount(result.getInt("question_count"));
					lessons.add(lesson);
				}
			}
		}

		return lessons;
	}

	public boolean update(Study course, long userId) throws SQLException {
		String sql = "UPDATE courses SET category_id=?,course_name=?,description=?,level=? WHERE course_id=? AND user_id=?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
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
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection
						.prepareStatement("DELETE FROM courses WHERE course_id=? AND user_id=?")) {
			statement.setInt(1, courseId);
			statement.setLong(2, userId);
			return statement.executeUpdate() == 1;
		}
	}

	public boolean isAvailableToUser(int courseId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"SELECT 1 FROM courses WHERE course_id=? AND (user_id IS NULL OR user_id=?)")) {
			statement.setInt(1, courseId);
			statement.setLong(2, userId);
			try (ResultSet rs = statement.executeQuery()) {
				return rs.next();
			}
		}
	}
}
