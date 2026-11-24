<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    StudyModel model = (StudyModel) request.getAttribute("model");
    Study study = model.getStudy();
    String pageTitle = study.getCourseName();
%>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<h1><%= h(study.getCourseName()) %> <span class="muted">［<%= h(study.getLevel()) %>］</span></h1>
<p class="muted"><%= h(study.getDescription()) %></p>
<div class="bar"><span style="width:<%= model.getProgressPercent() %>%"></span></div>
<p class="muted"><%= model.getCompletedLessons() %> / <%= model.getTotalLessons() %> レッスン完了（<%= model.getProgressPercent() %>%）</p>

<% for (Chapter ch : model.getChapters()) { %>
  <div class="card">
    <strong>第<%= ch.getChapterOrder() %>章　<%= h(ch.getChapterName()) %></strong>
    <ul class="plain">
      <% for (Task t : ch.getTasks()) { %>
        <li>
          <% if (t.isCompleted()) { %><span class="done">✔</span><% } else { %>○<% } %>
          <a href="<%= ctx %>/study?lessonId=<%= t.getLessonId() %>"><%= h(t.getTitle()) %></a>
          <% if (t.hasQuiz()) { %><span class="muted">（クイズあり）</span><% } %>
        </li>
      <% } %>
      <% if (ch.getTasks().isEmpty()) { %><li class="muted">レッスンはまだありません</li><% } %>
    </ul>
  </div>
<% } %>
<p><a href="<%= ctx %>/study?categoryId=<%= study.getCategoryId() %>">← <%= h(study.getCategoryName()) %> のコース一覧へ</a></p>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
