package aiwa.dao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StudyFlowContextListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			Database.initialize(event.getServletContext());
		} catch (javax.servlet.ServletException e) {
			throw new IllegalStateException(e);
		}
	}
}
