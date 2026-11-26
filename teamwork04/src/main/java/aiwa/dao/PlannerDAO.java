package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import aiwa.entity.PlannerItem;

public class PlannerDAO {
	public List<PlannerItem> findWeek(long userId, LocalDate weekStart, LocalDate weekEndExclusive)
			throws SQLException {
		return findWeek(userId, weekStart, weekEndExclusive, false);
	}

	public List<PlannerItem> findWeek(long userId, LocalDate weekStart, LocalDate weekEndExclusive,
			boolean includeCompleted) throws SQLException {
		String taskSql = "SELECT 'task' AS item_type,t.task_id AS item_id,t.title,COALESCE(c.course_name,'No course') AS course_name,"
				+ "t.due_at AS starts_at,NULL AS ends_at,t.status AS item_status,t.priority,"
				+ "CASE WHEN t.status='done' THEN 1 ELSE 0 END AS is_completed "
				+ "FROM tasks t LEFT JOIN courses c ON c.course_id=t.course_id AND (c.user_id IS NULL OR c.user_id=t.user_id) "
				+ "WHERE t.user_id=? AND t.due_at>=? AND t.due_at<?"
				+ (includeCompleted ? " " : " AND t.status <> 'done' ");
		String sessionSql = includeCompleted
				? "UNION ALL SELECT 'session',s.session_id,COALESCE(NULLIF(s.notes,''),'Study session'),COALESCE(c.course_name,'Independent study'),"
						+ "s.started_at,s.ended_at,'completed',NULL,1 FROM study_sessions s LEFT JOIN courses c ON c.course_id=s.course_id AND (c.user_id IS NULL OR c.user_id=s.user_id) "
						+ "WHERE s.user_id=? AND s.ended_at IS NOT NULL AND s.started_at>=? AND s.started_at<? "
				: "";
		String eventSql = "UNION ALL SELECT e.event_type,e.event_id,e.title,COALESCE(c.course_name,'No course'),e.starts_at,e.ends_at,e.event_type,NULL,e.is_completed "
				+ "FROM calendar_events e LEFT JOIN courses c ON c.course_id=e.course_id AND (c.user_id IS NULL OR c.user_id=e.user_id) "
				+ "WHERE e.user_id=? AND e.starts_at>=? AND e.starts_at<?"
				+ (includeCompleted ? " " : " AND e.is_completed=0 ");
		String sql = "SELECT item_type,item_id,title,course_name,starts_at,ends_at,item_status,priority,is_completed FROM ("
				+ taskSql + sessionSql + eventSql + ") ORDER BY starts_at,item_type";
		List<PlannerItem> items = new ArrayList<>();
		String from = format(weekStart.atStartOfDay());
		String to = format(weekEndExclusive.atStartOfDay());
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setString(2, from);
			statement.setString(3, to);
			int parameter = 4;
			if (includeCompleted) {
				statement.setLong(parameter++, userId);
				statement.setString(parameter++, from);
				statement.setString(parameter++, to);
			}
			statement.setLong(parameter++, userId);
			statement.setString(parameter++, from);
			statement.setString(parameter, to);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					PlannerItem item = new PlannerItem();
					item.setType(rs.getString("item_type"));
					item.setItemId(rs.getInt("item_id"));
					item.setTitle(rs.getString("title"));
					item.setCourseName(rs.getString("course_name"));
					item.setStartsAt(parse(rs.getString("starts_at")));
					item.setEndsAt(parse(rs.getString("ends_at")));
					item.setStatus(rs.getString("item_status"));
					item.setPriority(rs.getString("priority"));
					item.setCompleted(rs.getInt("is_completed") != 0);
					items.add(item);
				}
			}
		}
		return items;
	}

	public boolean hasStudyOverlap(long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
		String sql = "SELECT 1 FROM calendar_events WHERE user_id=? AND event_type='study' AND is_completed=0 AND starts_at<? AND ends_at>? "
				+ "UNION ALL SELECT 1 FROM study_sessions WHERE user_id=? AND ended_at IS NOT NULL AND started_at<? AND ended_at>? LIMIT 1";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setString(2, format(end));
			statement.setString(3, format(start));
			statement.setLong(4, userId);
			statement.setString(5, format(end));
			statement.setString(6, format(start));
			try (ResultSet rs = statement.executeQuery()) {
				return rs.next();
			}
		}
	}

	public int create(PlannerItem item, long userId, Integer courseId) throws SQLException {
		String sql = "INSERT INTO calendar_events(user_id,course_id,event_type,title,starts_at,ends_at) VALUES(?,?,?,?,?,?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			if (courseId == null)
				statement.setNull(2, java.sql.Types.INTEGER);
			else
				statement.setInt(2, courseId);
			statement.setString(3, item.getType());
			statement.setString(4, item.getTitle());
			statement.setString(5, format(item.getStartsAt()));
			if (item.getEndsAt() == null)
				statement.setNull(6, java.sql.Types.VARCHAR);
			else
				statement.setString(6, format(item.getEndsAt()));
			return statement.executeUpdate();
		}
	}

	public boolean deleteEvent(int eventId, long userId) throws SQLException {
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection
						.prepareStatement("DELETE FROM calendar_events WHERE event_id=? AND user_id=?")) {
			statement.setInt(1, eventId);
			statement.setLong(2, userId);
			return statement.executeUpdate() == 1;
		}
	}

	public boolean setCompleted(int eventId, long userId, boolean completed) throws SQLException {
		String sql = "UPDATE calendar_events SET is_completed=? WHERE event_id=? AND user_id=? AND event_type='study'";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, completed ? 1 : 0);
			statement.setInt(2, eventId);
			statement.setLong(3, userId);
			return statement.executeUpdate() == 1;
		}
	}

	private static String format(LocalDateTime value) {
		return value == null ? null : value.toString().replace('T', ' ');
	}
	private static LocalDateTime parse(String value) {
		return value == null ? null : LocalDateTime.parse(value.replace(' ', 'T'));
	}
}
