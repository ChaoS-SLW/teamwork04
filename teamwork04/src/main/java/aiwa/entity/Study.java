package aiwa.entity;

public class Study {
	private int courseId;
	private Long userId;
	private int categoryId;
	private String courseName;
	private String categoryName;
	private String description;
	private String level;
	private int taskCount;
	private int completedTaskCount;
	private int inProgressTaskCount;
	private int attemptedQuizCount;
	private int totalQuizQuestionCount;
	private int bestQuizCorrectCount;
	private int quizCount;

	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getTaskCount() {
		return taskCount;
	}
	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}
	public int getCompletedTaskCount() {
		return completedTaskCount;
	}
	public void setCompletedTaskCount(int completedTaskCount) {
		this.completedTaskCount = completedTaskCount;
	}
	public int getInProgressTaskCount() {
		return inProgressTaskCount;
	}
	public void setInProgressTaskCount(int inProgressTaskCount) {
		this.inProgressTaskCount = inProgressTaskCount;
	}
	public int getAttemptedQuizCount() {
		return attemptedQuizCount;
	}
	public void setAttemptedQuizCount(int attemptedQuizCount) {
		this.attemptedQuizCount = attemptedQuizCount;
	}
	public int getTotalQuizQuestionCount() {
		return totalQuizQuestionCount;
	}
	public void setTotalQuizQuestionCount(int totalQuizQuestionCount) {
		this.totalQuizQuestionCount = totalQuizQuestionCount;
	}
	public int getBestQuizCorrectCount() {
		return bestQuizCorrectCount;
	}
	public void setBestQuizCorrectCount(int bestQuizCorrectCount) {
		this.bestQuizCorrectCount = bestQuizCorrectCount;
	}
	public int getQuizCount() {
		return quizCount;
	}
	public void setQuizCount(int quizCount) {
		this.quizCount = quizCount;
	}
	public int getProgressPercent() {
		int totalUnits = taskCount + totalQuizQuestionCount;
		if (totalUnits == 0)
			return 0;
		int completedUnits = completedTaskCount + Math.min(bestQuizCorrectCount, totalQuizQuestionCount);
		return (int) Math.round(completedUnits * 100.0 / totalUnits);
	}
}
