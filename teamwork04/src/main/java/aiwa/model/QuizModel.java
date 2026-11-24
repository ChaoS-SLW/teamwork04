package aiwa.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import aiwa.dao.QuizDAO;
import aiwa.entity.Quiz;
import aiwa.entity.QuizAttempt;
import aiwa.entity.QuizQuestion;

public class QuizModel {
	private final QuizDAO dao = new QuizDAO();
	public List<Quiz> getAvailable(long userId) throws SQLException { return dao.findAvailable(userId); }
	public List<Quiz> getAvailableForCourse(long userId, int courseId) throws SQLException { return dao.findAvailableForCourse(userId, courseId); }
	public Quiz getQuiz(int quizId, long userId) throws SQLException { return dao.findQuiz(quizId, userId); }
	public List<QuizQuestion> getQuestions(int quizId) throws SQLException { return dao.findQuestions(quizId); }
	public int submit(long userId, int quizId, Map<Integer, Integer> answers) throws SQLException { return dao.submit(userId, quizId, answers); }
	public List<QuizAttempt> getHistory(long userId) throws SQLException { return dao.findHistory(userId, 100); }
	public List<QuizAttempt> getHistoryForCourse(long userId, int courseId) throws SQLException { return dao.findHistoryForCourse(userId, courseId, 100); }
	public QuizAttempt getAttempt(long userId, int resultId) throws SQLException { return dao.findAttempt(userId, resultId); }
}
