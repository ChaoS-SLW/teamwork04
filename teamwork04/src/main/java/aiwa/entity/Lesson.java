package aiwa.entity;

public class Lesson {
	private int lessonId;
	private String chapterName;
	private int chapterOrder;
	private String lessonTitle;
	private String content;
	private String exampleCode;
	private int lessonOrder;
	private int quizId;
	private int questionCount;

	public int getLessonId() { return lessonId; }
	public void setLessonId(int lessonId) { this.lessonId = lessonId; }

	public String getChapterName() { return chapterName; }
	public void setChapterName(String chapterName) { this.chapterName = chapterName; }

	public int getChapterOrder() { return chapterOrder; }
	public void setChapterOrder(int chapterOrder) { this.chapterOrder = chapterOrder; }

	public String getLessonTitle() { return lessonTitle; }
	public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }

	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	public String getExampleCode() { return exampleCode; }
	public void setExampleCode(String exampleCode) { this.exampleCode = exampleCode; }

	public int getLessonOrder() { return lessonOrder; }
	public void setLessonOrder(int lessonOrder) { this.lessonOrder = lessonOrder; }

	public int getQuizId() { return quizId; }
	public void setQuizId(int quizId) { this.quizId = quizId; }

	public int getQuestionCount() { return questionCount; }
	public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
}