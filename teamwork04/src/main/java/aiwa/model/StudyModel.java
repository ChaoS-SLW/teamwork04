package aiwa.model;

import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

import aiwa.dao.StudysessionDAO;
import aiwa.entity.StudySession;

public class StudyModel {
	private final StudysessionDAO dao = new StudysessionDAO();
	public int createSession(StudySession session) throws SQLException {
		return dao.create(session);
	}
	public StudySession getActiveSession(long userId) throws SQLException { return dao.findActive(userId); }
	public int startSession(long userId, Integer courseId) throws SQLException {
		StudySession session = new StudySession();
		session.setUserId(userId);
		session.setCourseId(courseId);
		session.setStartedAt(LocalDateTime.now());
		return dao.start(session);
	}
	public boolean stopSession(long userId) throws SQLException { return dao.stopActive(userId); }
	public List<StudySession> getRecentSessions(long userId, int limit) throws SQLException {
		return dao.findRecent(userId, limit);
	}
	public long getTotalMinutes(long userId) throws SQLException {
		return dao.totalMinutes(userId);
	}
}
