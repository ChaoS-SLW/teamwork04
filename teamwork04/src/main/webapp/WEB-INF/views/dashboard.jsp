<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% String pageTitle = "ダッシュボード"; %>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%
    Map<String, Object> d = (Map<String, Object>) request.getAttribute("dashboard");
    List<Study> courses = (List<Study>) d.get("courses");
    List<StudySession> sessions = (List<StudySession>) d.get("recentSessions");
    List<Task> lessons = (List<Task>) d.get("recentLessons");
%>
<h1>ダッシュボード</h1>

<div class="card">
  <strong>全体の進捗</strong>
  <div class="bar"><span style="width:<%= d.get("progressPercent") %>%"></span></div>
  <%= d.get("completedLessons") %> / <%= d.get("totalLessons") %> レッスン完了（<%= d.get("progressPercent") %>%）
  <br>クイズ受験回数：<%= d.get("quizCount") %> 回　／　平均正答率：<%= d.get("averagePercent") %>%
</div>

<div class="card">
  <strong>コース別の進捗</strong>
  <ul class="plain">
    <% for (Study s : courses) { if (s.getTotalLessons() == 0) continue; %>
      <li>
        <a href="<%= ctx %>/study?courseId=<%= s.getCourseId() %>"><%= h(s.getCourseName()) %></a>
        <div class="bar"><span style="width:<%= s.getProgressPercent() %>%"></span></div>
        <span class="muted"><%= s.getCompletedLessons() %> / <%= s.getTotalLessons() %>（<%= s.getProgressPercent() %>%）</span>
      </li>
    <% } %>
  </ul>
</div>

<div class="card">
  <strong>最近完了したレッスン</strong>
  <ul class="plain">
    <% for (Task t : lessons) { %>
      <li><a href="<%= ctx %>/study?lessonId=<%= t.getLessonId() %>"><%= h(t.getTitle()) %></a>
          <span class="muted"><%= h(t.getCompletedAt()) %></span></li>
    <% } %>
    <% if (lessons.isEmpty()) { %><li class="muted">まだありません</li><% } %>
  </ul>
</div>

<div class="card">
  <strong>最近のクイズ結果</strong>
  <ul class="plain">
    <% for (StudySession s : sessions) { %>
      <li><%= h(s.getQuizTitle()) %>：<%= s.getScore() %> / <%= s.getTotalQuestions() %>（<%= s.getPercent() %>%）
          <span class="muted"><%= h(s.getCompletedAt()) %></span></li>
    <% } %>
    <% if (sessions.isEmpty()) { %><li class="muted">まだありません</li><% } %>
  </ul>
</div>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
