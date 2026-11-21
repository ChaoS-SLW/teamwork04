
package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.Question;
import aiwa.entity.Quiz;
import aiwa.entity.QuizOption;
import aiwa.model.QuizModel;

@WebServlet("/QuizController")
public class QuizController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String quizIdParam = request.getParameter("quizId");

		if (quizIdParam == null) {
			response.sendError(400, "Quiz ID が必要です");
			return;
		}

		int quizId;

		try {
			quizId = Integer.parseInt(quizIdParam);

			if (quizId <= 0) {
				response.sendError(400, "Quiz ID が正しくありません");
				return;
			}

		} catch (NumberFormatException e) {
			response.sendError(400, "Quiz ID が正しくありません");
			return;
		}

		String dbPath = getServletContext().getRealPath("/WEB-INF/studyflow.db");

		QuizModel model = new QuizModel(dbPath);

		try {
			ArrayList<Quiz> quizzes = model.getAllQuizzes();

			Quiz selectedQuiz = null;

			for (Quiz quiz : quizzes) {
				if (quiz.getQuizId() == quizId) {
					selectedQuiz = quiz;
					break;
				}
			}

			if (selectedQuiz == null) {
				response.sendError(404, "Quiz が見つかりません");
				return;
			}

			ArrayList<Question> questions = model.getQuestionsByQuizId(quizId);

			Map<Integer, ArrayList<QuizOption>> optionsMap = new HashMap<>();

			for (Question question : questions) {
				ArrayList<QuizOption> options = model.getOptionsByQuestionId(
						question.getQuestionId());

				optionsMap.put(question.getQuestionId(), options);
			}

			request.setAttribute("quiz", selectedQuiz);
			request.setAttribute("questions", questions);
			request.setAttribute("optionsMap", optionsMap);

			request.getRequestDispatcher(
					"/WEB-INF/views/quizjsp.jsp")
					.forward(request, response);

		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException(
					"クイズの取得に失敗しました", e);
		}
	}
}
