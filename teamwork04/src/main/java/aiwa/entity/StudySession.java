package aiwa.entity;

import java.time.LocalDateTime;

public class StudySession {
	private int sessionId;
	private long userId;
	private Integer courseId;
	private String courseName;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private String notes;

	public int getSessionId() { return sessionId; }
	public void setSessionId(int sessionId) { this.sessionId = sessionId; }
	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }
	public Integer getCourseId() { return courseId; }
	public void setCourseId(Integer courseId) { this.courseId = courseId; }
	public String getCourseName() { return courseName; }
	public void setCourseName(String courseName) { this.courseName = courseName; }
	public LocalDateTime getStartedAt() { return startedAt; }
	public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
	public LocalDateTime getEndedAt() { return endedAt; }
	public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	public long getDurationMinutes() {
		return startedAt == null || endedAt == null ? 0 : java.time.Duration.between(startedAt, endedAt).toMinutes();
	}
}
