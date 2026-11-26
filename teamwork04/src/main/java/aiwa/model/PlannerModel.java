package aiwa.model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import aiwa.dao.PlannerDAO;
import aiwa.entity.PlannerItem;

public class PlannerModel {
	private final PlannerDAO dao = new PlannerDAO();
	public List<PlannerItem> getWeek(long userId, LocalDate weekStart) throws SQLException {
		return dao.findWeek(userId, weekStart, weekStart.plusDays(7));
	}
	public List<PlannerItem> getWeek(long userId, LocalDate weekStart, boolean includeCompleted) throws SQLException {
		return dao.findWeek(userId, weekStart, weekStart.plusDays(7), includeCompleted);
	}
	public boolean hasStudyOverlap(long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
		return dao.hasStudyOverlap(userId, start, end);
	}
	public int create(PlannerItem item, long userId, Integer courseId) throws SQLException {
		return dao.create(item, userId, courseId);
	}
	public boolean deleteEvent(int eventId, long userId) throws SQLException {
		return dao.deleteEvent(eventId, userId);
	}
	public boolean setCompleted(int eventId, long userId, boolean completed) throws SQLException {
		return dao.setCompleted(eventId, userId, completed);
	}
}
