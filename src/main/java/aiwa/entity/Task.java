package aiwa.entity;

import java.time.LocalDateTime;

public class Task {
	private int taskId;
	private long userId;
	private Integer courseId;
	private String courseName;
	private String title;
	private String description;
	private LocalDateTime dueAt;
	private String priority;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime completedAt;

	public int getTaskId() { return taskId; }
	public void setTaskId(int taskId) { this.taskId = taskId; }
	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }
	public Integer getCourseId() { return courseId; }
	public void setCourseId(Integer courseId) { this.courseId = courseId; }
	public String getCourseName() { return courseName; }
	public void setCourseName(String courseName) { this.courseName = courseName; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public LocalDateTime getDueAt() { return dueAt; }
	public void setDueAt(LocalDateTime dueAt) { this.dueAt = dueAt; }
	public String getPriority() { return priority; }
	public void setPriority(String priority) { this.priority = priority; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public LocalDateTime getCompletedAt() { return completedAt; }
	public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
