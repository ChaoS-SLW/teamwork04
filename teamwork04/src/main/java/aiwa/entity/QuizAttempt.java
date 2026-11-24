package aiwa.entity;

public class QuizAttempt {
	private int resultId;
	private int quizId;
	private int courseId;
	private String quizTitle;
	private String courseName;
	private String lessonTitle;
	private int score;
	private int totalQuestions;
	private String completedAt;

	public int getResultId() { return resultId; }
	public void setResultId(int resultId) { this.resultId = resultId; }
	public int getQuizId() { return quizId; }
	public void setQuizId(int quizId) { this.quizId = quizId; }
	public int getCourseId() { return courseId; }
	public void setCourseId(int courseId) { this.courseId = courseId; }
	public String getQuizTitle() { return quizTitle; }
	public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
	public String getCourseName() { return courseName; }
	public void setCourseName(String courseName) { this.courseName = courseName; }
	public String getLessonTitle() { return lessonTitle; }
	public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }
	public int getScore() { return score; }
	public void setScore(int score) { this.score = score; }
	public int getTotalQuestions() { return totalQuestions; }
	public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
	public String getCompletedAt() { return completedAt; }
	public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
	public int getPercent() { return totalQuestions == 0 ? 0 : Math.round(score * 100f / totalQuestions); }
}
