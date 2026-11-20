<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Category category = (Category) request.getAttribute("category");
    List<Study> courses = (List<Study>) request.getAttribute("courses");
    String pageTitle = category.getCategoryName();
%>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<h1><%= h(category.getCategoryName()) %></h1>
<p class="muted"><%= h(category.getDescription()) %></p>
<% if (courses.isEmpty()) { %>
  <p>このカテゴリにはまだコースがありません。</p>
<% } %>
<% for (Study s : courses) { %>
  <div class="card">
    <a href="<%= ctx %>/study?courseId=<%= s.getCourseId() %>"><%= h(s.getCourseName()) %></a>
    <span class="muted">［<%= h(s.getLevel()) %>］</span>
    <div class="muted"><%= h(s.getDescription()) %></div>
    <div class="bar"><span style="width:<%= s.getProgressPercent() %>%"></span></div>
    <div class="muted"><%= s.getCompletedLessons() %> / <%= s.getTotalLessons() %> レッスン完了（<%= s.getProgressPercent() %>%）</div>
  </div>
<% } %>
<p><a href="<%= ctx %>/study">← カテゴリ一覧へ</a></p>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
