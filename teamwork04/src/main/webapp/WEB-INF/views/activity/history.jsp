<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% String pageTitle = "Quiz History"; %>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%
    List<StudySession> history = (List<StudySession>) request.getAttribute("history");
    int attempts = history == null ? 0 : history.size();
    int perfectScores = 0;
    int percentSum = 0;
    if (history != null) {
        for (StudySession session : history) {
            int percent = session.getPercent();
            percentSum += percent;
            if (session.getTotalQuestions() > 0
                    && session.getScore() == session.getTotalQuestions()) {
                perfectScores++;
            }
        }
    }
    int averagePercent = attempts == 0 ? 0 : Math.round((float) percentSum / attempts);
%>

<h1>Quiz History</h1>
<p class="muted">Review your recent attempts and see how your scores are improving.</p>

<section style="display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:16px;margin:20px 0 28px">
    <div class="card">
        <div class="muted">Total attempts</div>
        <strong style="font-size:1.8em"><%= attempts %></strong>
    </div>
    <div class="card">
        <div class="muted">Average score</div>
        <strong style="font-size:1.8em"><%= averagePercent %>%</strong>
    </div>
    <div class="card">
        <div class="muted">Perfect scores</div>
        <strong style="font-size:1.8em"><%= perfectScores %></strong>
    </div>
</section>

<h2>Recent results</h2>
<% if (history == null || history.isEmpty()) { %>
    <div class="card">
        <p>No quiz history yet. Complete a quiz to see your results here.</p>
        <a class="btn" href="<%= ctx %>/study">Browse lessons</a>
    </div>
<% } else { %>
    <div style="overflow-x:auto">
        <table>
            <thead>
                <tr>
                    <th>Quiz</th>
                    <th>Score</th>
                    <th>Progress</th>
                    <th>Completed</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <% for (StudySession session : history) {
                    int percent = session.getPercent();
                    String status = percent == 100 ? "Perfect!"
                            : percent >= 70 ? "Passed" : "Keep practicing";
                    String statusClass = percent == 100 ? "ok"
                            : percent >= 70 ? "done" : "ng";
                %>
                    <tr>
                        <td><strong><%= h(session.getQuizTitle()) %></strong></td>
                        <td><%= session.getScore() %> / <%= session.getTotalQuestions() %></td>
                        <td>
                            <div class="bar"><span style="width:<%= percent %>%"></span></div>
                            <span><%= percent %>% · <span class="<%= statusClass %>"><%= status %></span></span>
                        </td>
                        <td><%= h(session.getCompletedAt()) %></td>
                        <td><a href="<%= ctx %>/activity?quizId=<%= session.getQuizId() %>">Retry</a></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
<% } %>

<p style="margin-top:24px">
    <a class="btn" href="<%= ctx %>/dashboard">Back to dashboard</a>
</p>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
