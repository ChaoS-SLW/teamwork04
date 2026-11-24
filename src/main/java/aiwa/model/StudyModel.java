package aiwa.model;

import java.sql.SQLException;
import java.util.List;

import aiwa.dao.StudysessionDAO;
import aiwa.entity.StudySession;

public class StudyModel {
	private final StudysessionDAO dao = new StudysessionDAO();
	public int createSession(StudySession session) throws SQLException { return dao.create(session); }
	public List<StudySession> getRecentSessions(long userId, int limit) throws SQLException { return dao.findRecent(userId, limit); }
	public long getTotalMinutes(long userId) throws SQLException { return dao.totalMinutes(userId); }
}
