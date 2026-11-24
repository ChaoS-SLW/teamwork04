package aiwa.entity;

/** courses テーブル（学習コース：Java, Python など） */
public class Study {
	private int courseId;
	private int categoryId;
	private String categoryName; // categories と JOIN
	private String courseName;
	private String description;
	private String image;
	private String level;
	private int totalLessons; // 集計用
	private int completedLessons; // 集計用

	/** 進捗率（0〜100） */
	public int getProgressPercent() {
		return totalLessons == 0 ? 0 : (int) Math.round(completedLessons * 100.0 / totalLessons);
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getTotalLessons() {
		return totalLessons;
	}

	public void setTotalLessons(int totalLessons) {
		this.totalLessons = totalLessons;
	}

	public int getCompletedLessons() {
		return completedLessons;
	}

	public void setCompletedLessons(int completedLessons) {
		this.completedLessons = completedLessons;
	}
}
