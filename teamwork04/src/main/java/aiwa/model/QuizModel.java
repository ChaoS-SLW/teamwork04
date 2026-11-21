
package aiwa.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import aiwa.entity.Question;
import aiwa.entity.Quiz;
import aiwa.entity.QuizOption;

public class QuizModel {

	private String URL;

	public QuizModel(String dbPath) {
		URL = "jdbc:sqlite:" + dbPath;
	}

	public ArrayList<Quiz> getAllQuizzes()
			throws SQLException, ClassNotFoundException {

		ArrayList<Quiz> quizzes = new ArrayList<>();

		String sql = "SELECT * FROM quizzes ORDER BY quiz_id";

		Class.forName("org.sqlite.JDBC");

		try (Connection conn = DriverManager.getConnection(URL);
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {

				Quiz quiz = new Quiz();

				quiz.setQuizId(rs.getInt("quiz_id"));
				quiz.setLessonId(rs.getInt("lesson_id"));
				quiz.setQuizTitle(rs.getString("quiz_title"));

				quizzes.add(quiz);
			}
		}

		return quizzes;
	}

	public ArrayList<Question> getQuestionsByQuizId(int quizId)
			throws SQLException, ClassNotFoundException {

		ArrayList<Question> questions = new ArrayList<>();

		String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_id";

		Class.forName("org.sqlite.JDBC");

		try (Connection conn = DriverManager.getConnection(URL);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, quizId);

			try (ResultSet rs = stmt.executeQuery()) {

				while (rs.next()) {
					Question question = new Question();

					question.setQuestionId(rs.getInt("question_id"));
					question.setQuizId(rs.getInt("quiz_id"));
					question.setQuestionText(rs.getString("question_text"));
					question.setExplanation(rs.getString("explanation"));

					questions.add(question);
				}
			}
		}

		return questions;
	}

	public ArrayList<QuizOption> getOptionsByQuestionId(int questionId)
			throws SQLException, ClassNotFoundException {

		ArrayList<QuizOption> options = new ArrayList<>();

		String sql = "SELECT * FROM options WHERE question_id = ? ORDER BY option_id";

		Class.forName("org.sqlite.JDBC");

		try (Connection conn = DriverManager.getConnection(URL);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, questionId);

			try (ResultSet rs = stmt.executeQuery()) {

				while (rs.next()) {
					QuizOption option = new QuizOption();

					option.setOptionId(rs.getInt("option_id"));
					option.setQuestionId(rs.getInt("question_id"));
					option.setOptionText(rs.getString("option_text"));
					option.setIsCorrect(rs.getInt("is_correct"));

					options.add(option);
				}
			}
		}

		return options;
	}

	public void saveResult(int quizId, int score, int total)
			throws Exception {

		Class.forName("org.sqlite.JDBC");

		Connection conn = DriverManager.getConnection(URL);
		System.out.println("Database = " + conn.getMetaData().getURL());
		String sql = "INSERT INTO quiz_results "
				+ "(quiz_id, score, total_questions, completed_at) "
				+ "VALUES (?, ?, ?, datetime('now', 'localtime'))";

		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setInt(1, quizId);
		stmt.setInt(2, score);
		stmt.setInt(3, total);

		int rows = stmt.executeUpdate();

		System.out.println("Inserted rows = " + rows);

		stmt.close();
		conn.close();
	}

}
