<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% String pageTitle = "カテゴリ一覧"; %>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
%>
<h1>カテゴリ一覧</h1>
<% for (Category c : categories) { %>
  <div class="card">
    <a href="<%= ctx %>/study?categoryId=<%= c.getCategoryId() %>"><%= h(c.getCategoryName()) %></a>
    <span class="muted">（<%= c.getCourseCount() %> コース）</span>
    <div class="muted"><%= h(c.getDescription()) %></div>
  </div>
<% } %>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
