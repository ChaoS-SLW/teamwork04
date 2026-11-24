<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,aiwa.entity.Quiz,aiwa.entity.QuizQuestion,aiwa.entity.QuizOption,aiwa.ulti.Html" %>
<%
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<QuizQuestion> questions = (List<QuizQuestion>) request.getAttribute("questions");
    Object flashMessage = request.getAttribute("flashMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head><title><%= Html.escape(quiz.getQuizTitle()) %> | StudyFlow</title><%@ include file="/WEB-INF/views/components/head.jspf" %></head>
<body>
<jsp:include page="/WEB-INF/views/components/header.jsp" />
<main class="container-xl sf-page py-4 py-lg-5">
    <p><a href="<%= request.getContextPath() %>/QuizController?courseId=<%= quiz.getCourseId() %>">← Back to <%= Html.escape(quiz.getCourseName()) %> quizzes</a></p>
    <header class="sf-page-heading"><div><span class="sf-eyebrow"><%= Html.escape(quiz.getCourseName()) %> · <%= Html.escape(quiz.getLessonTitle()) %></span>
        <h1 class="fw-bold mt-2"><%= Html.escape(quiz.getQuizTitle()) %></h1><p class="text-body-secondary"><%= questions.size() %> questions · unanswered questions count as incorrect</p></div></header>
    <% if (flashMessage != null) { %><div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="alert"><%= Html.escape(flashMessage) %></div><% } %>
    <form method="post" action="<%= request.getContextPath() %>/QuizController">
        <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>">
        <input type="hidden" name="quizId" value="<%= quiz.getQuizId() %>">
        <input type="hidden" name="courseId" value="<%= quiz.getCourseId() %>">
        <% int number = 0; for (QuizQuestion question : questions) { number++; %>
            <fieldset class="card sf-panel mb-3"><div class="card-body p-4">
                <legend class="h5 fw-bold"><span class="text-body-secondary">Question <%= number %>.</span> <%= Html.escape(question.getQuestionText()) %></legend>
                <% for (QuizOption option : question.getOptions()) { %>
                    <label class="d-flex align-items-start gap-2 py-2 px-3 mb-2 rounded border">
                        <input class="form-check-input mt-1" type="radio" name="q_<%= question.getQuestionId() %>" value="<%= option.getOptionId() %>">
                        <span><%= Html.escape(option.getOptionText()) %></span>
                    </label>
                <% } %>
            </div></fieldset>
        <% } %>
        <button class="btn btn-primary rounded-pill px-4" type="submit">Submit answers</button>
    </form>
</main>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body>
</html>
