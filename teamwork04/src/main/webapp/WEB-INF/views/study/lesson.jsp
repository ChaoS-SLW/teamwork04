<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Task lesson = (Task) request.getAttribute("lesson");
    Integer courseId = (Integer) request.getAttribute("courseId");
    String pageTitle = lesson.getTitle();
%>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<h1><%= h(lesson.getTitle()) %>
  <% if (lesson.isCompleted()) { %><span class="done">✔ 完了</span><% } %></h1>

<div class="card">
  <p><%= h(lesson.getContent()) %></p>
  <% if (lesson.getExampleCode() != null && !lesson.getExampleCode().trim().isEmpty()) { %>
    <pre><code><%= h(lesson.getExampleCode()) %></code></pre>
  <% } %>
</div>

<div class="card">
  <form action="<%= ctx %>/task" method="post" style="display:inline">
    <input type="hidden" name="lessonId" value="<%= lesson.getLessonId() %>">
    <% if (lesson.isCompleted()) { %>
      <input type="hidden" name="action" value="undo">
      <button type="submit" class="btn-sub">未完了に戻す</button>
      <span class="muted">完了日時: <%= h(lesson.getCompletedAt()) %></span>
    <% } else { %>
      <input type="hidden" name="action" value="complete">
      <button type="submit">このレッスンを完了にする</button>
    <% } %>
  </form>
  <% if (lesson.hasQuiz()) { %>
    <a class="btn" href="<%= ctx %>/activity?quizId=<%= lesson.getQuizId() %>">クイズに挑戦：<%= h(lesson.getQuizTitle()) %></a>
  <% } %>
</div>

<% if (courseId != null && courseId > 0) { %>
  <p><a href="<%= ctx %>/study?courseId=<%= courseId %>">← コースに戻る</a></p>
<% } %>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
