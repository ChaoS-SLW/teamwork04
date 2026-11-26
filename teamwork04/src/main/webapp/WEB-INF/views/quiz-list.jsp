<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,aiwa.entity.Quiz,aiwa.ulti.Html" %>
<%
    List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
    Object flashMessage = request.getAttribute("flashMessage");
    Integer courseId = (Integer) request.getAttribute("courseId");
    String courseName = (String) request.getAttribute("courseName");
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Quiz Library | StudyFlow</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
  </head>
  <body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container sf-page sf-quiz-page py-4 py-lg-5">
      <header class="sf-page-heading">
        <div>
          <span class="sf-eyebrow">PRACTICE WHAT YOU LEARN</span>
          <h1 class="fw-bold mt-2"><%= courseId == null ? "Quiz Library" : "Quizzes · " + Html.escape(courseName) %></h1>
          <p class="text-body-secondary"><%= courseId == null ? "Choose a quiz from the courses and lessons in the study database." : "Choose a quiz for this course." %></p>
        </div>
        <div class="sf-page-actions">
          <a
            class="btn btn-outline-primary rounded-pill px-4"
            href="<%= request.getContextPath() %>/CoursesController"
            >← Back to courses</a
          ><a
            class="btn btn-outline-primary rounded-pill px-4"
            href="<%= request.getContextPath() %>/QuizHistoryController<%= courseId == null ? "" : "?courseId=" + courseId %>"
            >Quiz history</a
          >
        </div>
      </header>
      <% if (flashMessage != null) { %>
      <div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div>
      <% } %>
<% if (quizzes == null || quizzes.isEmpty()) { %>
      <div class="sf-empty-state">
        <span aria-hidden="true">🧠</span>
        <p class="mb-1">No quizzes are available yet.</p>
        <small class="text-body-secondary"
          >Quizzes appear here when the database has questions linked to a
          lesson.</small
        >
      </div>
      <% } else { %>
      <div class="row g-3">
        <% for (Quiz quiz : quizzes) { %>
        <div class="col-12 col-md-6 col-xl-4">
          <article class="card sf-course-card h-100">
            <div class="card-body p-4">
              <span class="sf-course-category"
                ><%= Html.escape(quiz.getCategoryName()) %> · <%= Html.escape(quiz.getCourseName()) %></span
              >
              <h2 class="h5 fw-bold mt-2"><%= Html.escape(quiz.getQuizTitle()) %></h2>
              <p class="text-body-secondary mb-1"><%= Html.escape(quiz.getLessonTitle()) %></p>
              <p class="small text-body-secondary"><%= quiz.getQuestionCount() %> questions</p>
              <a
                class="btn btn-primary rounded-pill px-4"
                href="<%= request.getContextPath() %>/QuizController?quizId=<%= quiz.getQuizId() %>&amp;courseId=<%= quiz.getCourseId() %>"
                >Start quiz</a
              >
            </div>
          </article>
        </div>
        <% } %>
      </div>
      <% } %>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
  </body>
</html>
