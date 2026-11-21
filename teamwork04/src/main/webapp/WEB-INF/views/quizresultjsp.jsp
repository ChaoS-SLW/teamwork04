
<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%
    int total = (int) request.getAttribute("totalQuestions");
    int correct = (int) request.getAttribute("correctCount");
    int score = (int) request.getAttribute("score");
    int quizId = (int) request.getAttribute("quizId");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>クイズ結果</title>
</head>
<body>

    <h1>クイズ結果</h1>

    <h2>問題数：<%= total %></h2>

    <h2>正解数：<%= correct %></h2>

    <h2>正解率：<%= score %>%</h2>

    <% if (score >= 80) { %>
        <p>素晴らしいです！</p>
    <% } else if (score >= 60) { %>
        <p>よくできました！</p>
    <% } else { %>
        <p>もう一度頑張りましょう！</p>
    <% } %>

    <a href="<%= request.getContextPath() %>/QuizController?quizId=<%= quizId %>">
        もう一度挑戦する
    </a>

</body>
</html>
