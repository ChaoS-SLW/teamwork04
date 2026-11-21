
<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="aiwa.entity.Quiz" %>
<%@ page import="aiwa.entity.Question" %>
<%@ page import="aiwa.entity.QuizOption" %>

<%!
    public String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<%
    Quiz quiz = (Quiz) request.getAttribute("quiz");

    ArrayList<Question> questions =
        (ArrayList<Question>) request.getAttribute("questions");

    Map<Integer, ArrayList<QuizOption>> optionsMap =
        (Map<Integer, ArrayList<QuizOption>>)
        request.getAttribute("optionsMap");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>クイズ | StudyFlow</title>

    <%@ include file="/WEB-INF/views/components/head.jspf" %>
</head>

<body>

<jsp:include page="/WEB-INF/views/components/header.jsp" />

<main class="container py-5">

    <h1>
        <%= quiz != null
            ? escapeHtml(quiz.getQuizTitle())
            : "クイズ" %>
    </h1>

    <% if (questions != null && !questions.isEmpty()) { %>

        <form action="<%= request.getContextPath() %>/QuizSubmitController"
              method="post">

            <input type="hidden"
                   name="quizId"
                   value="<%= quiz.getQuizId() %>">

            <% int number = 1; %>

            <% for (Question question : questions) { %>

                <div class="card mb-4">
                    <div class="card-body">

                        <h5>
                            問題 <%= number++ %>：
                            <%= escapeHtml(question.getQuestionText()) %>
                        </h5>

                        <%
                            ArrayList<QuizOption> options =
                                optionsMap != null
                                ? optionsMap.get(question.getQuestionId())
                                : null;
                        %>

                        <% if (options != null) { %>

                            <% for (QuizOption option : options) { %>

                                <div class="form-check my-2">

                                    <input class="form-check-input"
                                           type="radio"
                                           name="question_<%= question.getQuestionId() %>"
                                           value="<%= option.getOptionId() %>"
                                           id="option_<%= option.getOptionId() %>"
                                           required>

                                    <label class="form-check-label"
                                           for="option_<%= option.getOptionId() %>">

                                        <%= escapeHtml(option.getOptionText()) %>

                                    </label>

                                </div>

                            <% } %>

                        <% } %>

                    </div>
                </div>

            <% } %>

            <button type="submit" class="btn btn-primary">
                回答を送信
            </button>

        </form>

    <% } else { %>

        <p>このクイズには問題がありません。</p>

    <% } %>

</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp" />

</body>
</html>
