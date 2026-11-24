package aiwa.entity;

/** quiz_results テーブル（1回のクイズ受験 = 1回の学習セッション） */
public class StudySession {
	private int resultId;
	private int quizId;
	private String quizTitle; // quizzes と JOIN
	private int score;
	private int totalQuestions;
	private String completedAt;

	/** 正答率（0〜100） */
	public int getPercent() {
		return totalQuestions == 0 ? 0 : (int) Math.round(score * 100.0 / totalQuestions);
	}

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public String getQuizTitle() {
		return quizTitle;
	}

	public void setQuizTitle(String quizTitle) {
		this.quizTitle = quizTitle;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public String getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(String completedAt) {
		this.completedAt = completedAt;
	}
}
