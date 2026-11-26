<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.Map,aiwa.entity.Study,aiwa.entity.Category,aiwa.entity.Quiz,aiwa.entity.Lesson,aiwa.ulti.Html" %>
<%
    List<Study> courses = (List<Study>) request.getAttribute("courses");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Lesson> lessons = (List<Lesson>) request.getAttribute("lessons");
    Map<Integer, List<Quiz>> quizzesByCourse = (Map<Integer, List<Quiz>>) request.getAttribute("quizzesByCourse");
    Map<Integer, Integer> courseCounts = (Map<Integer, Integer>) request.getAttribute("courseCounts");
    Category selectedCategory = (Category) request.getAttribute("selectedCategory");
    Study selectedCourse = (Study) request.getAttribute("selectedCourse");
    Long currentUserId = (Long) request.getAttribute("currentUserId");
    Object flashMessage = request.getAttribute("flashMessage");

    String viewMode = (String) request.getAttribute("viewMode");
    if (viewMode == null) {
        viewMode = "categories";
    }

    String pageTitle = "My Courses";
    String pageSubtitle = "Choose a category to find your courses.";
    if ("courses".equals(viewMode) && selectedCategory != null) {
        pageTitle = selectedCategory.getCategoryName();
        pageSubtitle = "Choose a course to start learning.";
    } else if ("lessons".equals(viewMode) && selectedCourse != null) {
        pageTitle = selectedCourse.getCourseName();
        pageSubtitle = selectedCourse.getDescription() == null ? "" : selectedCourse.getDescription();
    }
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title><%= Html.escape(pageTitle) %> | StudyFlow</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
  </head>
  <body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container sf-page sf-courses-page py-4 py-lg-5">
      <header class="sf-page-heading">
        <div>
          <span class="sf-eyebrow">MAKE SPACE TO LEARN</span>
          <h1 class="fw-bold mt-2"><%= Html.escape(pageTitle) %></h1>
          <p class="text-body-secondary"><%= Html.escape(pageSubtitle) %></p>
        </div>
        <div class="sf-page-actions">
          <a class="btn btn-outline-primary rounded-pill px-4" href="<%= request.getContextPath() %>/DashboardController">← Back to dashboard</a>
        </div>
      </header>

      <% if (!"categories".equals(viewMode)) { %>
      <nav class="sf-breadcrumb" aria-label="Breadcrumb">
        <a href="<%= request.getContextPath() %>/CoursesController">Courses</a>
        <% if (selectedCategory != null) { %>
          <span aria-hidden="true">›</span>
          <% if ("lessons".equals(viewMode)) { %>
            <a href="<%= request.getContextPath() %>/CoursesController?categoryId=<%= selectedCategory.getCategoryId() %>"><%= Html.escape(selectedCategory.getCategoryName()) %></a>
          <% } else { %>
            <span><%= Html.escape(selectedCategory.getCategoryName()) %></span>
          <% } %>
        <% } %>
        <% if ("lessons".equals(viewMode) && selectedCourse != null) { %>
          <span aria-hidden="true">›</span>
          <span><%= Html.escape(selectedCourse.getCourseName()) %></span>
        <% } %>
      </nav>
      <% } %>

      <% if (flashMessage != null) { %>
      <div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div>
      <% } %>
      <% if (request.getAttribute("pageError") != null) { %>
      <div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div>
      <% } %>

      <% if ("categories".equals(viewMode)) { %>
      <section aria-labelledby="categoriesHeading">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <div>
            <h2 class="h5 fw-bold mb-1" id="categoriesHeading">Categories</h2>
            <p class="small text-body-secondary mb-0">Open a category to see its courses.</p>
          </div>
          <span class="badge text-bg-light rounded-pill"><%= categories == null ? 0 : categories.size() %> categories</span>
        </div>

        <% if (categories == null || categories.isEmpty()) { %>
        <div class="sf-empty-state">
          <span aria-hidden="true">📚</span>
          <p class="mb-1">No categories are available yet.</p>
        </div>
        <% } else { %>
        <div class="sf-category-grid">
          <% for (Category category : categories) {
               Integer count = courseCounts == null ? null : courseCounts.get(category.getCategoryId());
               int categoryCourseCount = count == null ? 0 : count; %>
            <a class="sf-category-link" href="<%= request.getContextPath() %>/CoursesController?categoryId=<%= category.getCategoryId() %>">
              <article class="card sf-course-card h-100">
                <div class="card-body">
                  <div>
                    <h3 class="h5 fw-bold mb-1"><%= Html.escape(category.getCategoryName()) %></h3>
                    <p class="small text-body-secondary mb-0"><%= categoryCourseCount %> courses</p>
                  </div>
                  <span class="sf-course-icon" aria-hidden="true">📖</span>
                </div>
              </article>
            </a>
          <% } %>
        </div>
        <% } %>
      </section>
      <% } %>

      <% if ("courses".equals(viewMode)) { %>
      <section aria-labelledby="coursesHeading">
        <div class="sf-course-toolbar">
          <div>
            <h2 class="h5 fw-bold mb-1" id="coursesHeading">Courses</h2>
            <p class="small text-body-secondary mb-0">Shared catalog courses are read-only; your courses can be edited.</p>
          </div>
          <label class="sf-course-search">
            <span aria-hidden="true">⌕</span>
            <span class="visually-hidden">Search courses</span>
            <input type="search" id="courseSearch" autocomplete="off" placeholder="Search courses" aria-controls="courseGrid" />
          </label>
          <span class="badge text-bg-light rounded-pill sf-course-count" id="courseCount" aria-live="polite"><%= courses == null ? 0 : courses.size() %> courses</span>
        </div>

        <p class="sf-course-no-results" id="courseSearchEmpty" role="status" hidden>No courses match your search. Try a different name.</p>

        <% if (courses == null || courses.isEmpty()) { %>
        <div class="sf-empty-state">
          <span aria-hidden="true">📚</span>
          <p class="mb-1">No courses in this category yet.</p>
        </div>
        <% } else { %>
        <div class="sf-course-grid-container">
          <div class="sf-course-grid" id="courseGrid">
            <% for (Study course : courses) {
                 boolean editable = course.getUserId() != null && course.getUserId().equals(currentUserId);
                 List<Quiz> relatedQuizzes = quizzesByCourse == null ? null : quizzesByCourse.get(course.getCourseId());
                 String courseSearchText = course.getCourseName() + " " + course.getCategoryName() + " "
                     + (course.getDescription() == null ? "" : course.getDescription()); %>
            <div class="sf-course-grid-item" data-course-search="<%= Html.escape(courseSearchText) %>">
              <article class="card sf-course-card h-100">
                <div class="card-body d-flex flex-column p-4">
                  <div class="d-flex justify-content-between gap-2">
                    <div>
                      <span class="sf-course-category"><%= Html.escape(course.getCategoryName()) %><%= editable ? "" : " · Shared catalog" %></span>
                      <h3 class="h5 fw-bold mt-2">
                        <a class="sf-course-title-link" href="<%= request.getContextPath() %>/CoursesController?courseId=<%= course.getCourseId() %>"><%= Html.escape(course.getCourseName()) %></a>
                      </h3>
                    </div>
                    <span class="sf-course-icon" aria-hidden="true">📖</span>
                  </div>

                  <% if (course.getDescription() != null && !course.getDescription().isBlank()) { %>
                  <p class="small text-body-secondary"><%= Html.escape(course.getDescription()) %></p>
                  <% } %>

                  <div class="d-flex flex-wrap justify-content-between gap-1 small text-body-secondary mb-2">
                    <span><%= course.getCompletedTaskCount() %>/<%= course.getTaskCount() %> tasks · <%= course.getBestQuizCorrectCount() %>/<%= course.getTotalQuizQuestionCount() %> quiz answers correct</span>
                    <span><%= course.getProgressPercent() %>%</span>
                  </div>
                  <div class="progress mb-3" role="progressbar" aria-label="<%= Html.escape(course.getCourseName()) %> completion" aria-valuenow="<%= course.getProgressPercent() %>" aria-valuemin="0" aria-valuemax="100">
                    <div class="progress-bar" style="width: <%= course.getProgressPercent() %>%"></div>
                  </div>

                  <div class="d-flex flex-wrap gap-2 mt-auto pt-3">
                    <a class="btn btn-sm btn-primary rounded-pill" href="<%= request.getContextPath() %>/CoursesController?courseId=<%= course.getCourseId() %>">Open lessons</a>
                    <% if (relatedQuizzes != null && !relatedQuizzes.isEmpty()) { %>
                    <details class="sf-course-quizzes">
                      <summary class="btn btn-sm btn-outline-primary rounded-pill"><%= relatedQuizzes.size() %> related quizzes</summary>
                      <div class="sf-course-quiz-list mt-3">
                        <p class="small text-body-secondary mb-2">Choose a quiz from <%= Html.escape(course.getCourseName()) %>:</p>
                        <ul class="list-group list-group-flush">
                          <% for (Quiz quiz : relatedQuizzes) { %>
                          <li class="list-group-item px-0">
                            <a class="d-flex justify-content-between align-items-start gap-2" href="<%= request.getContextPath() %>/QuizController?quizId=<%= quiz.getQuizId() %>&amp;courseId=<%= course.getCourseId() %>">
                              <span><strong><%= Html.escape(quiz.getQuizTitle()) %></strong><br /><small class="text-body-secondary"><%= Html.escape(quiz.getLessonTitle()) %> · <%= quiz.getQuestionCount() %> questions</small></span>
                              <span aria-hidden="true">→</span>
                            </a>
                          </li>
                          <% } %>
                        </ul>
                      </div>
                    </details>
                    <a class="btn btn-sm btn-outline-primary rounded-pill" href="<%= request.getContextPath() %>/QuizHistoryController?courseId=<%= course.getCourseId() %>">Quiz history</a>
                    <% } else { %>
                    <button class="btn btn-sm btn-outline-secondary rounded-pill" type="button" disabled>No quiz yet</button>
                    <% } %>

                    <% if (editable) { %>
                    <details class="sf-edit-details">
                      <summary class="btn btn-sm btn-link">Edit</summary>
                      <form method="post" action="<%= request.getContextPath() %>/CoursesController" class="sf-edit-form">
                        <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" />
                        <input type="hidden" name="action" value="update" />
                        <input type="hidden" name="courseId" value="<%= course.getCourseId() %>" />
                        <label class="form-label">Course name<input class="form-control form-control-sm" name="courseName" maxlength="100" required value="<%= Html.escape(course.getCourseName()) %>" /></label>
                        <label class="form-label">Category<select class="form-select form-select-sm" name="categoryId">
                          <% if (categories != null) for (Category category : categories) { %>
                          <option value="<%= category.getCategoryId() %>" <%= category.getCategoryId() == course.getCategoryId() ? "selected" : "" %>><%= Html.escape(category.getCategoryName()) %></option>
                          <% } %>
                        </select></label>
                        <label class="form-label">Level<select class="form-select form-select-sm" name="level">
                          <option <%= "Beginner".equals(course.getLevel()) ? "selected" : "" %>>Beginner</option>
                          <option <%= "Intermediate".equals(course.getLevel()) ? "selected" : "" %>>Intermediate</option>
                          <option <%= "Advanced".equals(course.getLevel()) ? "selected" : "" %>>Advanced</option>
                        </select></label>
                        <label class="form-label">Description<textarea class="form-control form-control-sm" name="description" rows="2" maxlength="500"><%= Html.escape(course.getDescription()) %></textarea></label>
                        <button class="btn btn-sm btn-primary" type="submit">Save changes</button>
                      </form>
                    </details>
                    <form method="post" action="<%= request.getContextPath() %>/CoursesController" onsubmit="return confirm('Delete this course?');">
                      <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" />
                      <input type="hidden" name="action" value="delete" />
                      <input type="hidden" name="courseId" value="<%= course.getCourseId() %>" />
                      <button class="btn btn-sm btn-link text-danger" type="submit">Delete</button>
                    </form>
                    <% } %>
                  </div>
                </div>
              </article>
            </div>
            <% } %>
          </div>
        </div>
        <% } %>
      </section>
      <% } %>

      <% if ("lessons".equals(viewMode)) { %>
      <section aria-label="Course lessons">
        <% if (lessons == null || lessons.isEmpty()) { %>
        <div class="sf-empty-state">
          <span aria-hidden="true">📚</span>
          <p class="mb-1">No lessons in this course yet.</p>
        </div>
        <% } else {
             String lastChapterName = null;
             for (Lesson lesson : lessons) {
                 if (!java.util.Objects.equals(lastChapterName, lesson.getChapterName())) {
                     lastChapterName = lesson.getChapterName(); %>
        <h2 class="h5 fw-bold mt-4 mb-3"><%= Html.escape(lastChapterName) %></h2>
                 <% } %>
        <article class="card sf-lesson-card mb-3">
          <div class="card-body p-4">
            <span class="sf-course-category">Lesson <%= lesson.getLessonOrder() %></span>
            <h3 class="h5 fw-bold mt-1"><%= Html.escape(lesson.getLessonTitle()) %></h3>
            <p class="mb-3"><%= Html.escape(lesson.getContent()) %></p>
            <% if (lesson.getExampleCode() != null && !lesson.getExampleCode().isBlank()) { %>
            <pre class="sf-code"><code><%= Html.escape(lesson.getExampleCode()) %></code></pre>
            <% } %>
            <% if (lesson.getQuizId() > 0 && lesson.getQuestionCount() > 0 && selectedCourse != null) { %>
            <a class="btn btn-sm btn-primary rounded-pill" href="<%= request.getContextPath() %>/QuizController?quizId=<%= lesson.getQuizId() %>&amp;courseId=<%= selectedCourse.getCourseId() %>">Take quiz · <%= lesson.getQuestionCount() %> questions</a>
            <% } %>
          </div>
        </article>
        <% }
           } %>
      </section>
      <% } %>
    </main>

    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
    <script>
      (() => {
        const search = document.getElementById("courseSearch");
        if (!search) return;

        const cards = Array.from(document.querySelectorAll(".sf-course-grid-item"));
        const count = document.getElementById("courseCount");
        const empty = document.getElementById("courseSearchEmpty");

        search.addEventListener("input", () => {
          const query = search.value.trim().toLocaleLowerCase();
          let visible = 0;

          cards.forEach((card) => {
            const matches = card.dataset.courseSearch.toLocaleLowerCase().includes(query);
            card.hidden = !matches;
            if (matches) visible++;
          });

          count.textContent = visible + (visible === 1 ? " course" : " courses");
          empty.hidden = visible !== 0;
        });
      })();
    </script>
  </body>
</html>
