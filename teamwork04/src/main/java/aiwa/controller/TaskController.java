package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.service.ActivityService;
import aiwa.ulti.Ulti;

/**
 * レッスンの完了 / 未完了 の切り替え（POST）
 *   POST /task   lessonId=3 & action=complete   → 完了にする
 *   POST /task   lessonId=3 & action=undo       → 未完了に戻す
 * 処理後は元のレッスン画面へリダイレクト（PRGパターン）。
 */
@WebServlet("/task")
public class TaskController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ActivityService service = new ActivityService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath() + "/study");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		int lessonId = Ulti.toInt(request.getParameter("lessonId"), -1);
		String action = request.getParameter("action");
		if (lessonId <= 0 || action == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "lessonId / action が不正です");
			return;
		}

		try {
			if ("complete".equals(action)) {
				service.changeProgress(lessonId, true);
			} else if ("undo".equals(action)) {
				service.changeProgress(lessonId, false);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "未対応の action です");
				return;
			}
		} catch (SQLException e) {
			throw new ServletException("DBエラー（TaskController）", e);
		}
		response.sendRedirect(request.getContextPath() + "/study?lessonId=" + lessonId);
	}
}
