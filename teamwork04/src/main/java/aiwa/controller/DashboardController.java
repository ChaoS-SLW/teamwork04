package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.service.ActivityService;

/**
 * ダッシュボード（全体進捗・クイズ成績・最近の学習）
 *   GET /dashboard
 * JSP では ${dashboard.progressPercent} / ${dashboard.recentSessions} のように使える。
 */
@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ActivityService service = new ActivityService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		try {
			Map<String, Object> dashboard = service.getDashboard();
			request.setAttribute("dashboard", dashboard);
			request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
		} catch (SQLException e) {
			throw new ServletException("DBエラー（DashboardController）", e);
		}
	}
}
