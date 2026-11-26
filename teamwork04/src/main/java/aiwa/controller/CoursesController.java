package aiwa.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiwa.entity.Category;
import aiwa.entity.Quiz;
import aiwa.entity.Study;
import aiwa.model.CourseModel;
import aiwa.model.QuizModel;
import aiwa.ulti.Html;

@WebServlet("/CoursesController")
public class CoursesController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CourseModel model = new CourseModel();
	private final QuizModel quizModel = new QuizModel();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		long userId = ControllerSupport.userId(request);

		// URL ထဲက ?categoryId=... နဲ့ ?courseId=... ကို ဖတ်မယ် (မပါရင် null)
		Integer categoryId = ControllerSupport.positiveInt(request.getParameter("categoryId"));
		Integer courseId = ControllerSupport.positiveInt(request.getParameter("courseId"));

		// page ၃ မျိုး: "categories" (ပထမ page) / "courses" (category တစ်ခုထဲ) / "lessons" (course တစ်ခုထဲ)
		String viewMode = "categories";
		request.setAttribute("viewMode", viewMode);

		try {
			List<Study> allCourses = model.getCourses(userId);
			List<Category> categories = model.getCategories();
			categories.sort(Comparator.comparingInt(Category::getCategoryId)); // DB ထဲက အစဉ်အတိုင်း (プログラミング言語 အရင်)

			List<Quiz> availableQuizzes = quizModel.getAvailable(userId);
			Map<Integer, List<Quiz>> quizzesByCourse = new HashMap<>();
			for (Study course : allCourses) quizzesByCourse.put(course.getCourseId(), new ArrayList<>());
			for (Quiz quiz : availableQuizzes) {
				quizzesByCourse.computeIfAbsent(quiz.getCourseId(), key -> new ArrayList<>()).add(quiz);
			}

			Study selectedCourse = null;
			Category selectedCategory = null;

			// ၁။ courseId ပါရင် အဲဒီ course ကို ရှာမယ် (မြင်ခွင့်ရှိတဲ့ course တွေထဲကပဲ ရှာတာမို့ လုံခြုံတယ်)
			if (courseId != null) {
				for (Study c : allCourses) {
					if (c.getCourseId() == courseId.intValue()) selectedCourse = c;
				}
			}
			if (selectedCourse != null) {
				viewMode = "lessons";
				categoryId = selectedCourse.getCategoryId();
				request.setAttribute("lessons", model.getLessons(selectedCourse.getCourseId()));
			}

			// ၂။ category ကို ရှာမယ်
			if (categoryId != null) {
				for (Category c : categories) {
					if (c.getCategoryId() == categoryId.intValue()) selectedCategory = c;
				}
			}

			// ၃။ category တစ်ခုထဲက course တွေပဲ စစ်ထုတ်မယ်
			List<Study> shownCourses = allCourses;
			if (selectedCourse == null && selectedCategory != null) {
				viewMode = "courses";
				shownCourses = new ArrayList<>();
				for (Study c : allCourses) {
					if (c.getCategoryId() == selectedCategory.getCategoryId()) shownCourses.add(c);
				}
				shownCourses.sort(Comparator.comparingInt(Study::getCourseId)); // သင်ယူရမယ့် အစဉ်အတိုင်း
			}

			// category တစ်ခုချင်းမှာ course ဘယ်နှစ်ခု ရှိလဲ (ပထမ page ရဲ့ ကတ်တွေအတွက်)
			Map<Integer, Integer> courseCounts = new HashMap<>();
			for (Study c : allCourses) courseCounts.merge(c.getCategoryId(), 1, Integer::sum);

			request.setAttribute("viewMode", viewMode);
			request.setAttribute("courses", shownCourses);
			request.setAttribute("quizzesByCourse", quizzesByCourse);
			request.setAttribute("categories", categories);
			request.setAttribute("courseCounts", courseCounts);
			request.setAttribute("selectedCategory", selectedCategory);
			request.setAttribute("selectedCourse", selectedCourse);
			request.setAttribute("currentUserId", userId);
		} catch (SQLException e) { request.setAttribute("pageError", "Courses could not be loaded. Please try again."); }
		ControllerSupport.exposeFlash(request);
		request.getRequestDispatcher("/WEB-INF/views/coursesjsp.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		if (!Html.validCsrfToken(request)) { ControllerSupport.flash(request, "danger", "Your form expired. Please try again."); response.sendRedirect(request.getContextPath() + "/CoursesController"); return; }
		String action = request.getParameter("action");
		long userId = ControllerSupport.userId(request);
		try {
			if ("delete".equals(action)) {
				Integer id = ControllerSupport.positiveInt(request.getParameter("courseId"));
				if (id == null) throw new IllegalArgumentException("Select a valid course.");
				if (!model.delete(id, userId)) throw new IllegalArgumentException("That course is not one of your editable courses.");
				ControllerSupport.flash(request, "success", "Course deleted.");
			} else {
				Study course = readCourse(request, userId);
				if ("update".equals(action)) {
					Integer id = ControllerSupport.positiveInt(request.getParameter("courseId"));
					if (id == null) throw new IllegalArgumentException("Select a valid course to edit.");
					course.setCourseId(id);
					if (!model.update(course, userId)) throw new IllegalArgumentException("That course is not one of your editable courses.");
					ControllerSupport.flash(request, "success", "Course updated.");
				} else if ("create".equals(action)) {
					model.create(course); ControllerSupport.flash(request, "success", "Course added.");
				} else throw new IllegalArgumentException("Choose a valid course action.");
			}
		} catch (IllegalArgumentException e) { ControllerSupport.flash(request, "danger", e.getMessage()); }
		catch (SQLException e) {
			String message = e.getMessage() != null && e.getMessage().contains("UNIQUE")
					? "A course with that name already exists in this category."
					: "The course could not be saved. Courses with learning content cannot be deleted.";
			ControllerSupport.flash(request, "danger", message);
		}
		response.sendRedirect(request.getContextPath() + "/CoursesController");
	}

	private Study readCourse(HttpServletRequest request, long userId) {
		String name = request.getParameter("courseName");
		String description = request.getParameter("description");
		String level = request.getParameter("level");
		Integer categoryId = ControllerSupport.positiveInt(request.getParameter("categoryId"));
		if (name == null || name.trim().isEmpty() || name.trim().length() > 100) throw new IllegalArgumentException("Course name is required (up to 100 characters).");
		if (categoryId == null) throw new IllegalArgumentException("Choose a course category.");
		if (description != null && description.length() > 500) throw new IllegalArgumentException("Description must be 500 characters or fewer.");
		if (level == null || !(level.equals("Beginner") || level.equals("Intermediate") || level.equals("Advanced"))) level = "Beginner";
		Study course = new Study(); course.setUserId(userId); course.setCategoryId(categoryId); course.setCourseName(name.trim());
		course.setDescription(description == null || description.isBlank() ? null : description.trim()); course.setLevel(level);
		return course;
	}
}