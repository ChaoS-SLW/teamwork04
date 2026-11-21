
package aiwa.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.Question;
import aiwa.entity.QuizOption;
import aiwa.model.QuizModel;

@WebServlet("/QuizSubmitController")
public class QuizSubmitController extends HttpServlet {

	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		// 1. Quiz ID ကို ယူမယ်
		String id = request.getParameter("quizId");

		if (id == null) {
			response.sendError(400);
			return;
		}

		int quizId = 0;

		try {
			quizId = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			response.sendError(400);
			return;
		}

		if (quizId <= 0) {
			response.sendError(400);
			return;
		}

		// 2. Model ကို ခေါ်မယ်

		String dbPath = getServletContext().getRealPath("/WEB-INF/studyflow.db");

		QuizModel model = new QuizModel(dbPath);

		try {

			// 3. Questions တွေကို Database က ယူမယ်
			ArrayList<Question> questions = model.getQuestionsByQuizId(quizId);

			int total = questions.size();
			int correct = 0;

			if (total == 0) {
				response.sendError(404);
				return;
			}

			// 4. Question တစ်ခုချင်းစီ စစ်မယ်
			for (Question question : questions) {

				int questionId = question.getQuestionId();

				String answer = request.getParameter(
						"question_" + questionId);

				if (answer != null) {

					int selectedId = 0;

					try {
						selectedId = Integer.parseInt(answer);
					} catch (NumberFormatException e) {
						selectedId = 0;
					}

					// 5. Database က Options တွေ ယူမယ်
					ArrayList<QuizOption> options = model.getOptionsByQuestionId(questionId);

					// 6. အဖြေမှန်/မှား စစ်မယ်
					for (QuizOption option : options) {

						if (option.getOptionId() == selectedId) {

							if (option.getIsCorrect() == 1) {
								correct++;
							}

							break;
						}
					}
				}
			}

			// 7. Score တွက်မယ်
			int score = correct * 100 / total;

			model.saveResult(quizId, correct, total);

			// 8. JSP ဆီ Result ပို့မယ်
			request.setAttribute("totalQuestions", total);
			request.setAttribute("correctCount", correct);
			request.setAttribute("score", score);
			request.setAttribute("quizId", quizId);

			request.getRequestDispatcher(
					"/WEB-INF/views/quizresultjsp.jsp")
					.forward(request, response);

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
