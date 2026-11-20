package aiwa.service;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import aiwa.dao.StudyDAO;
import aiwa.dao.StudysessionDAO;
import aiwa.dao.TaskDAO;
import aiwa.entity.Category;
import aiwa.entity.Chapter;
import aiwa.entity.Question;
import aiwa.entity.QuestionOption;
import aiwa.entity.Study;
import aiwa.entity.StudySession;
import aiwa.entity.Task;
import aiwa.model.StudyModel;

/**
 * 学習アクティビティ（閲覧・完了・クイズ受験・集計）の業務ロジック
 * Controller は必ずこのクラス経由で DAO を使う。
 */
public class ActivityService {

	private final StudyDAO studyDAO = new StudyDAO();
	private final TaskDAO taskDAO = new TaskDAO();
	private final StudysessionDAO sessionDAO = new StudysessionDAO();

	/** クイズ採点結果（result.jsp に渡す用） */
	public static class QuizOutcome {
		private final StudySession session;
		private final List<Question> questions;
		private final Map<Integer, Integer> selected; // questionId -> 選んだ optionId

		public QuizOutcome(StudySession session, List<Question> questions, Map<Integer, Integer> selected) {
			this.session = session;
			this.questions = questions;
			this.selected = selected;
		}

		public StudySession getSession() {
			return session;
		}

		public List<Question> getQuestions() {
			return questions;
		}

		public Map<Integer, Integer> getSelected() {
			return selected;
		}
	}

	// ---------- 閲覧 ----------

	public List<Category> getCategories() throws SQLException {
		return studyDAO.findAllCategories();
	}

	public Category getCategory(int categoryId) throws SQLException {
		return studyDAO.findCategoryById(categoryId);
	}

	public List<Study> getCourses(int categoryId) throws SQLException {
		return studyDAO.findCoursesByCategory(categoryId);
	}

	/** コース → チャプター → レッスン を組み立てる。コースが無ければ null */
	public StudyModel getCourseModel(int courseId) throws SQLException {
		Study study = studyDAO.findCourseById(courseId);
		if (study == null) {
			return null;
		}
		List<Chapter> chapters = studyDAO.findChaptersByCourse(courseId);
		for (Chapter ch : chapters) {
			ch.setTasks(taskDAO.findByChapter(ch.getChapterId()));
		}
		return new StudyModel(study, chapters);
	}

	public Task getLesson(int lessonId) throws SQLException {
		return taskDAO.findById(lessonId);
	}

	public int getCourseIdOfLesson(int lessonId) throws SQLException {
		return studyDAO.findCourseIdByLesson(lessonId);
	}

	// ---------- 進捗 ----------

	public void changeProgress(int lessonId, boolean completed) throws SQLException {
		taskDAO.setCompleted(lessonId, completed);
	}

	// ---------- クイズ ----------

	public String getQuizTitle(int quizId) throws SQLException {
		return sessionDAO.findQuizTitle(quizId);
	}

	public List<Question> loadQuiz(int quizId) throws SQLException {
		return sessionDAO.findQuestions(quizId);
	}

	/**
	 * 採点して quiz_results に保存する。
	 * @param answers questionId -> 選んだ optionId（未回答の問題は含めなくてよい）
	 * @return 問題が1つも無いクイズなら null
	 */
	public QuizOutcome submitQuiz(int quizId, Map<Integer, Integer> answers) throws SQLException {
		List<Question> questions = sessionDAO.findQuestions(quizId);
		if (questions.isEmpty()) {
			return null;
		}
		int score = 0;
		for (Question q : questions) {
			Integer chosen = answers.get(q.getQuestionId());
			if (chosen == null) {
				continue;
			}
			for (QuestionOption o : q.getOptions()) {
				if (o.getOptionId() == chosen && o.isCorrect()) {
					score++;
					break;
				}
			}
		}
		StudySession s = new StudySession();
		s.setQuizId(quizId);
		s.setScore(score);
		s.setTotalQuestions(questions.size());
		int id = sessionDAO.insert(s);
		StudySession saved = sessionDAO.findById(id); // quiz_title / completed_at つきで取り直す
		return new QuizOutcome(saved, questions, answers);
	}

	public List<StudySession> getHistory(int limit) throws SQLException {
		return sessionDAO.findRecent(limit);
	}

	// ---------- ダッシュボード ----------

	/** ダッシュボード用の集計値をまとめて返す */
	public Map<String, Object> getDashboard() throws SQLException {
		int totalLessons = taskDAO.countAll();
		int completedLessons = taskDAO.countCompleted();

		Map<String, Object> m = new LinkedHashMap<>();
		m.put("totalLessons", totalLessons);
		m.put("completedLessons", completedLessons);
		m.put("progressPercent",
				totalLessons == 0 ? 0 : (int) Math.round(completedLessons * 100.0 / totalLessons));
		m.put("quizCount", sessionDAO.countAll());
		m.put("averagePercent", sessionDAO.averagePercent());
		m.put("recentSessions", sessionDAO.findRecent(5));
		m.put("recentLessons", taskDAO.findRecentCompleted(5));
		m.put("courses", studyDAO.findAllCourses());
		return m;
	}
}
