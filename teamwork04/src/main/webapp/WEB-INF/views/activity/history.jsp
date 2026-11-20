<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% String pageTitle = "受験履歴"; %>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%
    List<StudySession> history = (List<StudySession>) request.getAttribute("history");
%>
<h1>受験履歴</h1>
<% if (history == null || history.isEmpty()) { %>
  <p>まだ受験履歴がありません。レッスンのクイズに挑戦してみましょう。</p>
<% } else { %>
  <table>
    <tr><th>クイズ</th><th>点数</th><th>正答率</th><th>日時</th><th></th></tr>
    <% for (StudySession s : history) { %>
      <tr>
        <td><%= h(s.getQuizTitle()) %></td>
        <td><%= s.getScore() %> / <%= s.getTotalQuestions() %></td>
        <td><%= s.getPercent() %>%</td>
        <td><%= h(s.getCompletedAt()) %></td>
        <td><a href="<%= ctx %>/activity?quizId=<%= s.getQuizId() %>">再挑戦</a></td>
      </tr>
    <% } %>
  </table>
<% } %>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
