package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.Question;
import aiwa.service.ActivityService;
import aiwa.service.ActivityService.QuizOutcome;
import aiwa.ulti.Ulti;

/**
 * クイズ受験 と 受験履歴
 *   GET  /activity                → 受験履歴一覧
 *   GET  /activity?quizId=1       → クイズ画面（問題 + 選択肢）
 *   POST /activity                → 採点して結果画面へ（パラメータ: quizId, q_{questionId}={optionId}）
 */
@WebServlet("/activity")
public class ActivityController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ActivityService service = new ActivityService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		int quizId = Ulti.toInt(request.getParameter("quizId"), -1);

		try {
			if (quizId > 0) {
				String title = service.getQuizTitle(quizId);
				List<Question> questions = service.loadQuiz(quizId);
				if (title == null || questions.isEmpty()) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "クイズが見つかりません");
					return;
				}
				request.setAttribute("quizId", quizId);
				request.setAttribute("quizTitle", title);
				request.setAttribute("questions", questions);
				request.getRequestDispatcher("/WEB-INF/views/activity/quiz.jsp").forward(request, response);
			} else {
				request.setAttribute("history", service.getHistory(50));
				request.getRequestDispatcher("/WEB-INF/views/activity/history.jsp").forward(request, response);
			}
		} catch (SQLException e) {
			throw new ServletException("DBエラー（ActivityController#doGet）", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		int quizId = Ulti.toInt(request.getParameter("quizId"), -1);
		if (quizId <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "quizId が不正です");
			return;
		}

		// q_12=45 → 問題12 に 選択肢45 を選んだ
		Map<Integer, Integer> answers = new HashMap<>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if (name.startsWith("q_")) {
				int questionId = Ulti.toInt(name.substring(2), -1);
				int optionId = Ulti.toInt(request.getParameter(name), -1);
				if (questionId > 0 && optionId > 0) {
					answers.put(questionId, optionId);
				}
			}
		}

		try {
			QuizOutcome outcome = service.submitQuiz(quizId, answers);
			if (outcome == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "クイズが見つかりません");
				return;
			}
			request.setAttribute("outcome", outcome);
			request.getRequestDispatcher("/WEB-INF/views/activity/result.jsp").forward(request, response);
		} catch (SQLException e) {
			throw new ServletException("DBエラー（ActivityController#doPost）", e);
		}
	}
}
