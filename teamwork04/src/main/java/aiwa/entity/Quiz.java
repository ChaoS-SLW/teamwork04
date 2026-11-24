package aiwa.entity;

public class Quiz {
	private int quizId;
	private String quizTitle;
	private String categoryName;
	private String courseName;
	private String lessonTitle;
	private int questionCount;

	public int getQuizId() { return quizId; }
	public void setQuizId(int quizId) { this.quizId = quizId; }
	public String getQuizTitle() { return quizTitle; }
	public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
	public String getCategoryName() { return categoryName; }
	public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
	public String getCourseName() { return courseName; }
	public void setCourseName(String courseName) { this.courseName = courseName; }
	public String getLessonTitle() { return lessonTitle; }
	public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }
	public int getQuestionCount() { return questionCount; }
	public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
}
