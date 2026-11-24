<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,aiwa.entity.QuizAttempt,aiwa.ulti.Html" %>
<%
    List<QuizAttempt> attempts = (List<QuizAttempt>) request.getAttribute("attempts");
    int average = 0, perfect = 0;
    if (attempts != null && !attempts.isEmpty()) {
        int sum = 0;
        for (QuizAttempt attempt : attempts) { sum += attempt.getPercent(); if (attempt.getPercent() == 100) perfect++; }
        average = Math.round((float) sum / attempts.size());
    }
%>
<!DOCTYPE html>
<html lang="en">
<head><title>Quiz History | StudyFlow</title><%@ include file="/WEB-INF/views/components/head.jspf" %></head>
<body>
<jsp:include page="/WEB-INF/views/components/header.jsp" />
<main class="container-xl sf-page py-4 py-lg-5">
    <header class="sf-page-heading"><div><span class="sf-eyebrow">YOUR PRACTICE</span><h1 class="fw-bold mt-2">Quiz History</h1><p class="text-body-secondary">Only your own saved attempts are shown.</p></div>
        <a class="btn btn-primary rounded-pill px-4" href="<%= request.getContextPath() %>/QuizController">Browse quizzes</a></header>
    <section class="row g-3 mb-4" aria-label="Quiz summary">
        <div class="col-12 col-sm-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><p class="text-body-secondary mb-1">Attempts</p><p class="display-6 fw-bold mb-0"><%= attempts == null ? 0 : attempts.size() %></p></div></article></div>
        <div class="col-12 col-sm-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><p class="text-body-secondary mb-1">Average score</p><p class="display-6 fw-bold mb-0"><%= average %>%</p></div></article></div>
        <div class="col-12 col-sm-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><p class="text-body-secondary mb-1">Perfect scores</p><p class="display-6 fw-bold mb-0"><%= perfect %></p></div></article></div>
    </section>
    <% if (attempts == null || attempts.isEmpty()) { %>
        <div class="sf-empty-state"><span aria-hidden="true">🧠</span><p class="mb-1">No quiz attempts yet.</p><small class="text-body-secondary">Your results will appear here after you complete a quiz.</small></div>
    <% } else { %>
        <div class="table-responsive"><table class="table align-middle"><thead><tr><th>Quiz</th><th>Course</th><th>Score</th><th>Completed</th><th></th></tr></thead><tbody>
        <% for (QuizAttempt attempt : attempts) { %>
            <tr><td><strong><%= Html.escape(attempt.getQuizTitle()) %></strong><br><small class="text-body-secondary"><%= Html.escape(attempt.getLessonTitle()) %></small></td>
                <td><%= Html.escape(attempt.getCourseName()) %></td><td><%= attempt.getScore() %> / <%= attempt.getTotalQuestions() %> (<%= attempt.getPercent() %>%)</td>
                <td><%= Html.escape(attempt.getCompletedAt()) %></td><td><a href="<%= request.getContextPath() %>/QuizController?quizId=<%= attempt.getQuizId() %>">Retry</a></td></tr>
        <% } %>
        </tbody></table></div>
    <% } %>
</main>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body>
</html>
