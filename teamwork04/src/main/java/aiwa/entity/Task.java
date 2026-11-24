package aiwa.entity;

/**
 * lessons + lesson_progress (+ quizzes) をまとめた「やること（レッスン）」
 */
public class Task {
	private int lessonId;
	private int chapterId;
	private String title; // lesson_title
	private String content;
	private String exampleCode;
	private int lessonOrder;
	private boolean completed; // lesson_progress.is_completed
	private String completedAt; // lesson_progress.completed_at
	private Integer quizId; // クイズが無ければ null
	private String quizTitle;

	public boolean hasQuiz() {
		return quizId != null;
	}

	public int getLessonId() {
		return lessonId;
	}

	public void setLessonId(int lessonId) {
		this.lessonId = lessonId;
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExampleCode() {
		return exampleCode;
	}

	public void setExampleCode(String exampleCode) {
		this.exampleCode = exampleCode;
	}

	public int getLessonOrder() {
		return lessonOrder;
	}

	public void setLessonOrder(int lessonOrder) {
		this.lessonOrder = lessonOrder;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(String completedAt) {
		this.completedAt = completedAt;
	}

	public Integer getQuizId() {
		return quizId;
	}

	public void setQuizId(Integer quizId) {
		this.quizId = quizId;
	}

	public String getQuizTitle() {
		return quizTitle;
	}

	public void setQuizTitle(String quizTitle) {
		this.quizTitle = quizTitle;
	}
}
