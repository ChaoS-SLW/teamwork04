package aiwa.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import aiwa.dao.Database;

public class ActivityService {
	private static final Set<String> ACTIVITY_TYPES = Set.of(
			"course_opened", "quiz_completed", "task_completed", "task_saved", "note_saved");

	public void recordAction(long userId, String eventType) throws SQLException {
		if (!ACTIVITY_TYPES.contains(eventType))
			throw new IllegalArgumentException("Unknown study activity.");
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO study_activity_events(user_id,event_type) "
								+ "SELECT ?,? WHERE NOT EXISTS (SELECT 1 FROM study_activity_events "
								+ "WHERE user_id=? AND event_type=? AND date(occurred_at)=date('now','localtime'))")) {
			statement.setLong(1, userId);
			statement.setString(2, eventType);
			statement.setLong(3, userId);
			statement.setString(4, eventType);
			statement.executeUpdate();
		}
	}

	public Map<LocalDate, Integer> getDailyActions(long userId, int days) throws SQLException {
		Map<LocalDate, Integer> totals = new HashMap<>();
		LocalDate start = LocalDate.now().minusDays(Math.max(0, days - 1));
		String sql = "SELECT date(occurred_at) AS activity_day,COUNT(*) AS action_count FROM study_activity_events WHERE user_id=? AND date(occurred_at)>=? GROUP BY date(occurred_at)";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setString(2, start.toString());
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next())
					totals.put(LocalDate.parse(rs.getString("activity_day")), rs.getInt("action_count"));
			}
		}
		return totals;
	}

	public Map<LocalDate, Integer> getDailyMinutes(long userId, int days) throws SQLException {
		Map<LocalDate, Integer> totals = new HashMap<>();
		LocalDate start = LocalDate.now().minusDays(Math.max(0, days - 1));
		String sql = "SELECT date(started_at) AS study_day, SUM((julianday(ended_at)-julianday(started_at))*1440) AS minutes "
				+ "FROM study_sessions WHERE user_id=? AND ended_at IS NOT NULL AND date(started_at)>=? GROUP BY date(started_at)";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setString(2, start.toString());
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next())
					totals.put(LocalDate.parse(rs.getString("study_day")), (int) Math.round(rs.getDouble("minutes")));
			}
		}
		return totals;
	}

	public List<Integer> getWeeklyMinutes(long userId) throws SQLException {
		Map<LocalDate, Integer> data = getDailyMinutes(userId, 7);
		List<Integer> values = new ArrayList<>();
		for (int i = 6; i >= 0; i--)
			values.add(data.getOrDefault(LocalDate.now().minusDays(i), 0));
		return values;
	}

	public List<String> getWeekdayLabels() {
		List<String> labels = new ArrayList<>();
		for (int i = 6; i >= 0; i--) {
			DayOfWeek day = LocalDate.now().minusDays(i).getDayOfWeek();
			labels.add(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
		}
		return labels;
	}

	public int getStudyStreak(long userId) throws SQLException {
		Set<LocalDate> days = new HashSet<>();
		String sql = "SELECT date(started_at) AS active_day FROM study_sessions WHERE user_id=? AND ended_at IS NOT NULL UNION SELECT date(occurred_at) FROM study_activity_events WHERE user_id=? ORDER BY 1 DESC";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, userId);
			statement.setLong(2, userId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next())
					days.add(LocalDate.parse(rs.getString(1)));
			}
		}
		LocalDate cursor = LocalDate.now();
		if (!days.contains(cursor))
			cursor = cursor.minusDays(1);
		int streak = 0;
		while (days.contains(cursor)) {
			streak++;
			cursor = cursor.minusDays(1);
		}
		return streak;
	}

	public int getActivityLevel(int minutes) {
		if (minutes <= 0)
			return 0;
		if (minutes < 30)
			return 1;
		if (minutes < 60)
			return 2;
		if (minutes < 120)
			return 3;
		return 4;
	}
}
