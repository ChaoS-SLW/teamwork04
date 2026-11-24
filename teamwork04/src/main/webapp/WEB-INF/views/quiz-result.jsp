<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="aiwa.entity.QuizAttempt,aiwa.ulti.Html" %>
<% QuizAttempt attempt = (QuizAttempt) request.getAttribute("attempt"); %>
<!DOCTYPE html>
<html lang="en">
<head><title>Quiz Result | StudyFlow</title><%@ include file="/WEB-INF/views/components/head.jspf" %></head>
<body>
<jsp:include page="/WEB-INF/views/components/header.jsp" />
<main class="container-xl sf-page py-4 py-lg-5">
    <section class="card sf-panel mx-auto" style="max-width:720px"><div class="card-body p-4 p-lg-5 text-center">
        <span class="sf-eyebrow">QUIZ COMPLETE</span>
        <h1 class="fw-bold mt-2"><%= Html.escape(attempt.getQuizTitle()) %></h1>
        <p class="text-body-secondary"><%= Html.escape(attempt.getCourseName()) %> · <%= Html.escape(attempt.getLessonTitle()) %></p>
        <p class="display-4 fw-bold my-4"><%= attempt.getScore() %> / <%= attempt.getTotalQuestions() %></p>
        <p class="h4"><%= attempt.getPercent() %>%</p>
        <p class="text-body-secondary">Saved on <%= Html.escape(attempt.getCompletedAt()) %></p>
        <div class="d-flex justify-content-center flex-wrap gap-2 mt-4">
            <a class="btn btn-primary rounded-pill px-4" href="<%= request.getContextPath() %>/QuizController?quizId=<%= attempt.getQuizId() %>">Try again</a>
            <a class="btn btn-outline-primary rounded-pill px-4" href="<%= request.getContextPath() %>/QuizHistoryController">Quiz history</a>
            <a class="btn btn-outline-primary rounded-pill px-4" href="<%= request.getContextPath() %>/home">Home</a>
        </div>
    </div></section>
</main>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body>
</html>
