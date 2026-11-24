package aiwa.ulti;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StudyFlowContextListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		String configured = System.getProperty("studyflow.db");
		if (configured == null || configured.trim().isEmpty()) configured = System.getenv("STUDYFLOW_DB");
		if (configured == null || configured.trim().isEmpty()) {
			configured = event.getServletContext().getRealPath("/WEB-INF/studyflow.db");
		}
		if (configured == null || configured.trim().isEmpty()) {
			throw new IllegalStateException("Set -Dstudyflow.db or STUDYFLOW_DB when WEB-INF/studyflow.db is not available as a filesystem path");
		}
		if (System.getProperty("studyflow.db") == null || System.getProperty("studyflow.db").trim().isEmpty()) {
			System.setProperty("studyflow.db", configured);
		}
		event.getServletContext().log("StudyFlow SQLite database: " + configured);
	}
}
