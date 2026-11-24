package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.Category;
import aiwa.entity.Task;
import aiwa.model.StudyModel;
import aiwa.service.ActivityService;
import aiwa.ulti.Ulti;

/**
 * 学習コンテンツの閲覧
 *   /study                  → カテゴリ一覧
 *   /study?categoryId=1     → そのカテゴリのコース一覧
 *   /study?courseId=1       → コース詳細（チャプター + レッスン + 進捗）
 *   /study?lessonId=3       → レッスン詳細
 */
@WebServlet("/study")
public class StudyController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ActivityService service = new ActivityService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		int lessonId = Ulti.toInt(request.getParameter("lessonId"), -1);
		int courseId = Ulti.toInt(request.getParameter("courseId"), -1);
		int categoryId = Ulti.toInt(request.getParameter("categoryId"), -1);

		try {
			if (lessonId > 0) {
				Task lesson = service.getLesson(lessonId);
				if (lesson == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "レッスンが見つかりません");
					return;
				}
				request.setAttribute("lesson", lesson);
				request.setAttribute("courseId", service.getCourseIdOfLesson(lessonId));
				request.getRequestDispatcher("/WEB-INF/views/study/lesson.jsp").forward(request, response);

			} else if (courseId > 0) {
				StudyModel model = service.getCourseModel(courseId);
				if (model == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "コースが見つかりません");
					return;
				}
				request.setAttribute("model", model);
				request.getRequestDispatcher("/WEB-INF/views/study/course.jsp").forward(request, response);

			} else if (categoryId > 0) {
				Category category = service.getCategory(categoryId);
				if (category == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "カテゴリが見つかりません");
					return;
				}
				request.setAttribute("category", category);
				request.setAttribute("courses", service.getCourses(categoryId));
				request.getRequestDispatcher("/WEB-INF/views/study/courses.jsp").forward(request, response);

			} else {
				request.setAttribute("categories", service.getCategories());
				request.getRequestDispatcher("/WEB-INF/views/study/categories.jsp").forward(request, response);
			}
		} catch (SQLException e) {
			throw new ServletException("DBエラー（StudyController）", e);
		}
	}
}
