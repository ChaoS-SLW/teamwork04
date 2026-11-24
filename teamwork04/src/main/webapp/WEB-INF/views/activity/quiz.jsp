<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Integer quizId = (Integer) request.getAttribute("quizId");
    String quizTitle = (String) request.getAttribute("quizTitle");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    String pageTitle = quizTitle;
%>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<h1><%= h(quizTitle) %></h1>
<form action="<%= ctx %>/activity" method="post">
  <input type="hidden" name="quizId" value="<%= quizId %>">
  <% int no = 1; for (Question q : questions) { %>
    <div class="card">
      <strong>問<%= no++ %>．<%= h(q.getQuestionText()) %></strong>
      <% for (QuestionOption o : q.getOptions()) { %>
        <label class="opt">
          <input type="radio" name="q_<%= q.getQuestionId() %>" value="<%= o.getOptionId() %>">
          <%= h(o.getOptionText()) %>
        </label>
      <% } %>
    </div>
  <% } %>
  <button type="submit">採点する</button>
</form>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
