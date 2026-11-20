<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    aiwa.service.ActivityService.QuizOutcome outcome =
        (aiwa.service.ActivityService.QuizOutcome) request.getAttribute("outcome");
    StudySession s = outcome.getSession();
    Map<Integer, Integer> selected = outcome.getSelected();
    String pageTitle = "結果";
%>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<h1>結果：<%= h(s.getQuizTitle()) %></h1>
<div class="card">
  <strong style="font-size:1.6em"><%= s.getScore() %> / <%= s.getTotalQuestions() %></strong>
  （正答率 <%= s.getPercent() %>%）
  <div class="bar"><span style="width:<%= s.getPercent() %>%"></span></div>
</div>

<% int no = 1; for (Question q : outcome.getQuestions()) {
       Integer chosen = selected.get(q.getQuestionId());
       boolean right = false;
       for (QuestionOption o : q.getOptions()) {
           if (chosen != null && o.getOptionId() == chosen && o.isCorrect()) right = true;
       } %>
  <div class="card">
    <strong>問<%= no++ %>．<%= h(q.getQuestionText()) %></strong>
    <% if (chosen == null) { %><span class="ng">（未回答）</span>
    <% } else if (right) { %><span class="ok">○ 正解</span>
    <% } else { %><span class="ng">× 不正解</span><% } %>
    <% for (QuestionOption o : q.getOptions()) {
           String cls = o.isCorrect() ? "opt-correct"
                      : (chosen != null && o.getOptionId() == chosen ? "opt-wrong" : ""); %>
      <div class="<%= cls %>" style="padding:4px 6px">
        <%= h(o.getOptionText()) %>
        <% if (o.isCorrect()) { %> ← 正解<% } %>
        <% if (chosen != null && o.getOptionId() == chosen) { %> （あなたの回答）<% } %>
      </div>
    <% } %>
    <% if (q.getExplanation() != null) { %>
      <p class="muted">解説：<%= h(q.getExplanation()) %></p>
    <% } %>
  </div>
<% } %>
<a class="btn" href="<%= ctx %>/activity?quizId=<%= s.getQuizId() %>">もう一度挑戦</a>
<a class="btn btn-sub" href="<%= ctx %>/activity">受験履歴へ</a>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>
