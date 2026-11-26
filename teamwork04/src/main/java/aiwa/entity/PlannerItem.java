package aiwa.entity;

import java.time.LocalDateTime;

/**
 * A task, logged session, planned study block, or exam displayed on the weekly
 * planner.
 */
public class PlannerItem {
	private int itemId;
	private String type;
	private String title;
	private String courseName;
	private LocalDateTime startsAt;
	private LocalDateTime endsAt;
	private String status;
	private String priority;
	private boolean completed;

	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public LocalDateTime getStartsAt() {
		return startsAt;
	}
	public void setStartsAt(LocalDateTime startsAt) {
		this.startsAt = startsAt;
	}
	public LocalDateTime getEndsAt() {
		return endsAt;
	}
	public void setEndsAt(LocalDateTime endsAt) {
		this.endsAt = endsAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean isUserEvent() {
		return "study".equals(type) || "exam".equals(type);
	}
}
