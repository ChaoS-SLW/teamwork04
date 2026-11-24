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

import aiwa.entity.Question;
import aiwa.entity.QuestionOption;
import aiwa.entity.StudySession;
import aiwa.ulti.Ulti;

/**
 * quiz_results（学習セッション履歴）と、クイズの問題・選択肢を扱う DAO
 */
public class StudysessionDAO {

	private static final String RESULT_SQL = "SELECT r.result_id, r.quiz_id, z.quiz_title, r.score, r.total_questions, r.completed_at "
			+ "FROM quiz_results r JOIN quizzes z ON z.quiz_id = r.quiz_id ";

	/** 結果を保存して、採番された result_id を返す */
	public int insert(StudySession s) throws SQLException {
		String sql = "INSERT INTO quiz_results (quiz_id, score, total_questions, completed_at) "
				+ "VALUES (?, ?, ?, datetime('now','localtime'))";
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, s.getQuizId());
			ps.setInt(2, s.getScore());
			ps.setInt(3, s.getTotalQuestions());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : -1;
			}
		}
	}

	public StudySession findById(int resultId) throws SQLException {
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(RESULT_SQL + "WHERE r.result_id = ?")) {
			ps.setInt(1, resultId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		}
		return null;
	}

	/** 最近の受験履歴（新しい順） */
	public List<StudySession> findRecent(int limit) throws SQLException {
		List<StudySession> list = new ArrayList<>();
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(
						RESULT_SQL + "ORDER BY r.completed_at DESC, r.result_id DESC LIMIT ?")) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
		}
		return list;
	}

	public int countAll() throws SQLException {
		try (Connection con = Ulti.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM quiz_results")) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	/** 全体の平均正答率（%）。履歴が無ければ 0 */
	public int averagePercent() throws SQLException {
		String sql = "SELECT AVG(score * 100.0 / total_questions) FROM quiz_results";
		try (Connection con = Ulti.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			return rs.next() ? (int) Math.round(rs.getDouble(1)) : 0;
		}
	}

	/** クイズ名（無ければ null） */
	public String findQuizTitle(int quizId) throws SQLException {
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT quiz_title FROM quizzes WHERE quiz_id = ?")) {
			ps.setInt(1, quizId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getString(1) : null;
			}
		}
	}

	/** クイズの問題 + 選択肢をまとめて取得 */
	public List<Question> findQuestions(int quizId) throws SQLException {
		Map<Integer, Question> map = new LinkedHashMap<>();
		String sql = "SELECT q.question_id, q.quiz_id, q.question_text, q.explanation, "
				+ "       o.option_id, o.option_text, o.is_correct "
				+ "FROM questions q LEFT JOIN options o ON o.question_id = q.question_id "
				+ "WHERE q.quiz_id = ? ORDER BY q.question_id, o.option_id";
		try (Connection con = Ulti.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, quizId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int qid = rs.getInt("question_id");
					Question q = map.get(qid);
					if (q == null) {
						q = new Question();
						q.setQuestionId(qid);
						q.setQuizId(rs.getInt("quiz_id"));
						q.setQuestionText(rs.getString("question_text"));
						q.setExplanation(rs.getString("explanation"));
						map.put(qid, q);
					}
					int oid = rs.getInt("option_id");
					if (!rs.wasNull()) {
						QuestionOption o = new QuestionOption();
						o.setOptionId(oid);
						o.setQuestionId(qid);
						o.setOptionText(rs.getString("option_text"));
						o.setCorrect(rs.getInt("is_correct") == 1);
						q.getOptions().add(o);
					}
				}
			}
		}
		return new ArrayList<>(map.values());
	}

	private StudySession map(ResultSet rs) throws SQLException {
		StudySession s = new StudySession();
		s.setResultId(rs.getInt("result_id"));
		s.setQuizId(rs.getInt("quiz_id"));
		s.setQuizTitle(rs.getString("quiz_title"));
		s.setScore(rs.getInt("score"));
		s.setTotalQuestions(rs.getInt("total_questions"));
		s.setCompletedAt(rs.getString("completed_at"));
		return s;
	}
}
