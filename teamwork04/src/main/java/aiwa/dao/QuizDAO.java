package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import aiwa.entity.Quiz;
import aiwa.entity.QuizAttempt;
import aiwa.entity.QuizOption;
import aiwa.entity.QuizQuestion;

/** Reads catalog quizzes and saves authenticated users' attempts. */
public class QuizDAO {
	private static final String ACCESSIBLE_QUIZ = "SELECT q.quiz_id FROM quizzes q "
			+ "JOIN lessons l ON l.lesson_id=q.lesson_id "
			+ "JOIN chapters ch ON ch.chapter_id=l.chapter_id "
			+ "JOIN courses c ON c.course_id=ch.course_id "
			+ "WHERE q.quiz_id=? AND (c.user_id IS NULL OR c.user_id=?)";

	public List<Quiz> findAvailable(long userId) throws SQLException {
		return findAvailable(userId, null);
	}

	public List<Quiz> findAvailableForCourse(long userId, int courseId) throws SQLException {
		return findAvailable(userId, courseId);
	}

	private List<Quiz> findAvailable(long userId, Integer courseId) throws SQLException {
		String sql = "SELECT q.quiz_id,q.quiz_title,c.course_id,ca.category_name,c.course_name,l.lesson_title,COUNT(DISTINCT qu.question_id) question_count "
				+ "FROM quizzes q JOIN lessons l ON l.lesson_id=q.lesson_id "
				+ "JOIN chapters ch ON ch.chapter_id=l.chapter_id JOIN courses c ON c.course_id=ch.course_id "
				+ "JOIN categories ca ON ca.category_id=c.category_id "
				+ "LEFT JOIN questions qu ON qu.quiz_id=q.quiz_id "
				+ "WHERE (c.user_id IS NULL OR c.user_id=?) "
				+ (courseId == null ? "" : "AND c.course_id=? ")
				+ "GROUP BY q.quiz_id HAVING COUNT(DISTINCT qu.question_id)>0 "
				+ "ORDER BY ca.category_name COLLATE NOCASE,c.course_name COLLATE NOCASE,l.lesson_order,q.quiz_title";
		List<Quiz> quizzes = new ArrayList<>();
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			if (courseId != null) statement.setInt(2, courseId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) quizzes.add(mapQuiz(rs));
			}
		}
		return quizzes;
	}

	public Quiz findQuiz(int quizId, long userId) throws SQLException {
		String sql = "SELECT q.quiz_id,q.quiz_title,c.course_id,ca.category_name,c.course_name,l.lesson_title,COUNT(DISTINCT qu.question_id) question_count "
				+ "FROM quizzes q JOIN lessons l ON l.lesson_id=q.lesson_id "
				+ "JOIN chapters ch ON ch.chapter_id=l.chapter_id JOIN courses c ON c.course_id=ch.course_id "
				+ "JOIN categories ca ON ca.category_id=c.category_id "
				+ "LEFT JOIN questions qu ON qu.quiz_id=q.quiz_id "
				+ "WHERE q.quiz_id=? AND (c.user_id IS NULL OR c.user_id=?) GROUP BY q.quiz_id";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, quizId);
			statement.setLong(2, userId);
			try (ResultSet rs = statement.executeQuery()) { return rs.next() ? mapQuiz(rs) : null; }
		}
	}

	public List<QuizQuestion> findQuestions(int quizId) throws SQLException {
		Map<Integer, QuizQuestion> questions = new LinkedHashMap<>();
		String sql = "SELECT q.question_id,q.question_text,q.explanation,o.option_id,o.option_text,o.is_correct "
				+ "FROM questions q LEFT JOIN options o ON o.question_id=q.question_id "
				+ "WHERE q.quiz_id=? ORDER BY q.question_id,o.option_id";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, quizId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					int questionId = rs.getInt("question_id");
					QuizQuestion question = questions.get(questionId);
					if (question == null) {
						question = new QuizQuestion();
						question.setQuestionId(questionId);
						question.setQuestionText(rs.getString("question_text"));
						question.setExplanation(rs.getString("explanation"));
						questions.put(questionId, question);
					}
					int optionId = rs.getInt("option_id");
					if (!rs.wasNull()) {
						QuizOption option = new QuizOption();
						option.setOptionId(optionId);
						option.setOptionText(rs.getString("option_text"));
						option.setCorrect(rs.getInt("is_correct") == 1);
						question.getOptions().add(option);
					}
				}
			}
		}
		return new ArrayList<>(questions.values());
	}

	public int submit(long userId, int quizId, Map<Integer, Integer> selectedOptions) throws SQLException {
		if (!isAvailable(quizId, userId)) throw new IllegalArgumentException("This quiz is not available for your account.");
		List<QuizQuestion> questions = findQuestions(quizId);
		if (questions.isEmpty()) throw new IllegalArgumentException("This quiz does not contain any questions.");

		int score = 0;
		for (QuizQuestion question : questions) {
			Integer selectedId = selectedOptions.get(question.getQuestionId());
			if (selectedId == null) continue;
			for (QuizOption option : question.getOptions()) {
				if (option.getOptionId() == selectedId && option.isCorrect()) { score++; break; }
			}
		}

		String sql = "INSERT INTO quiz_results(user_id,quiz_id,score,total_questions,completed_at) VALUES(?,?,?,?,CURRENT_TIMESTAMP)";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setInt(2, quizId);
			statement.setInt(3, score);
			statement.setInt(4, questions.size());
			statement.executeUpdate();
			try (Statement idStatement = connection.createStatement(); ResultSet id = idStatement.executeQuery("SELECT last_insert_rowid()")) {
				if (!id.next()) throw new SQLException("Could not read the saved quiz result id");
				return id.getInt(1);
			}
		}
	}

	public List<QuizAttempt> findHistory(long userId, int limit) throws SQLException {
		return findHistory(userId, null, limit);
	}

	public List<QuizAttempt> findHistoryForCourse(long userId, int courseId, int limit) throws SQLException {
		return findHistory(userId, courseId, limit);
	}

	private List<QuizAttempt> findHistory(long userId, Integer courseId, int limit) throws SQLException {
		List<QuizAttempt> attempts = new ArrayList<>();
		String sql = historySql() + " WHERE r.user_id=? " + (courseId == null ? "" : "AND c.course_id=? ")
				+ "ORDER BY r.completed_at DESC,r.result_id DESC LIMIT ?";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			if (courseId == null) statement.setInt(2, limit);
			else { statement.setInt(2, courseId); statement.setInt(3, limit); }
			try (ResultSet rs = statement.executeQuery()) { while (rs.next()) attempts.add(mapAttempt(rs)); }
		}
		return attempts;
	}

	public QuizAttempt findAttempt(long userId, int resultId) throws SQLException {
		String sql = historySql() + " WHERE r.user_id=? AND r.result_id=?";
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setInt(2, resultId);
			try (ResultSet rs = statement.executeQuery()) { return rs.next() ? mapAttempt(rs) : null; }
		}
	}

	private boolean isAvailable(int quizId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement(ACCESSIBLE_QUIZ)) {
			statement.setInt(1, quizId);
			statement.setLong(2, userId);
			try (ResultSet rs = statement.executeQuery()) { return rs.next(); }
		}
	}

	private static String historySql() {
		return "SELECT r.result_id,r.quiz_id,c.course_id,q.quiz_title,c.course_name,l.lesson_title,r.score,r.total_questions,r.completed_at "
				+ "FROM quiz_results r JOIN quizzes q ON q.quiz_id=r.quiz_id "
				+ "JOIN lessons l ON l.lesson_id=q.lesson_id JOIN chapters ch ON ch.chapter_id=l.chapter_id "
				+ "JOIN courses c ON c.course_id=ch.course_id";
	}

	private static Quiz mapQuiz(ResultSet rs) throws SQLException {
		Quiz quiz = new Quiz();
		quiz.setQuizId(rs.getInt("quiz_id"));
		quiz.setCourseId(rs.getInt("course_id"));
		quiz.setQuizTitle(rs.getString("quiz_title"));
		quiz.setCategoryName(rs.getString("category_name"));
		quiz.setCourseName(rs.getString("course_name"));
		quiz.setLessonTitle(rs.getString("lesson_title"));
		quiz.setQuestionCount(rs.getInt("question_count"));
		return quiz;
	}

	private static QuizAttempt mapAttempt(ResultSet rs) throws SQLException {
		QuizAttempt attempt = new QuizAttempt();
		attempt.setResultId(rs.getInt("result_id"));
		attempt.setQuizId(rs.getInt("quiz_id"));
		attempt.setCourseId(rs.getInt("course_id"));
		attempt.setQuizTitle(rs.getString("quiz_title"));
		attempt.setCourseName(rs.getString("course_name"));
		attempt.setLessonTitle(rs.getString("lesson_title"));
		attempt.setScore(rs.getInt("score"));
		attempt.setTotalQuestions(rs.getInt("total_questions"));
		attempt.setCompletedAt(rs.getString("completed_at"));
		return attempt;
	}
}
