
package aiwa.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.QuizResult;
import aiwa.model.QuizModel;

@WebServlet("/QuizHistoryController")
public class QuizHistoryController extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String dbPath = getServletContext()
                    .getRealPath("/WEB-INF/studyflow.db");

            QuizModel model = new QuizModel(dbPath);

            ArrayList<QuizResult> history =
                    model.getQuizHistory();

            request.setAttribute("history", history);

            request.getRequestDispatcher(
                    "/WEB-INF/views/quizHistory.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();
            throw new ServletException(e);

        }
    }
}
