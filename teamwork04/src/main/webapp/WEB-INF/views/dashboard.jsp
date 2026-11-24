<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDate,java.time.DayOfWeek,java.util.List,java.util.Map,aiwa.entity.Study,aiwa.entity.Task,aiwa.ulti.Html,aiwa.ulti.Ulti,aiwa.service.ActivityService" %>
<%
    int courseCount = request.getAttribute("courseCount") instanceof Integer ? (Integer) request.getAttribute("courseCount") : 0;
    int taskCount = request.getAttribute("taskCount") instanceof Integer ? (Integer) request.getAttribute("taskCount") : 0;
    int completedTaskCount = request.getAttribute("completedTaskCount") instanceof Integer ? (Integer) request.getAttribute("completedTaskCount") : 0;
    int taskPercent = taskCount == 0 ? 0 : Math.round(completedTaskCount * 100f / taskCount);
    int streak = request.getAttribute("streak") instanceof Integer ? (Integer) request.getAttribute("streak") : 0;
    List<Study> courses = (List<Study>) request.getAttribute("courses");
    List<Task> upcomingTasks = (List<Task>) request.getAttribute("upcomingTasks");
    List<Integer> weeklyMinutes = (List<Integer>) request.getAttribute("weeklyMinutes");
    List<String> weekdays = (List<String>) request.getAttribute("weekdays");
    Map<LocalDate, Integer> activity = (Map<LocalDate, Integer>) request.getAttribute("activity");
    ActivityService levels = new ActivityService();
    Object flashMessage = request.getAttribute("flashMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard | StudyFlow</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container-xl sf-page py-4 py-lg-5">
        <% if (flashMessage != null) { %><div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div><% } %>
        <% if (request.getAttribute("pageError") != null) { %><div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div><% } %>
        <section class="sf-welcome mb-4">
            <div class="row align-items-end g-3"><div class="col-12 col-lg-7">
                <span class="sf-eyebrow">YOUR STUDY SPACE ✨</span>
                <h1 class="fw-bold mt-2 mb-2"><%= Ulti.showGreeting() %></h1>
                <p class="text-body-secondary mb-0">Small steps. Big progress. Let's make today count!</p>
            </div><div class="col-12 col-lg-5 d-flex flex-wrap gap-2 justify-content-lg-end">
                <a href="<%= request.getContextPath() %>/CoursesController" class="btn btn-primary rounded-pill px-4">+ Add course</a>
                <a href="<%= request.getContextPath() %>/TaskController" class="btn btn-outline-primary rounded-pill px-4">+ New task</a>
                <a href="<%= request.getContextPath() %>/QuizController" class="btn btn-outline-primary rounded-pill px-4">Take a quiz</a>
            </div></div>
        </section>
        <section class="row g-3 mb-4" aria-label="Study overview">
            <div class="col-12 col-sm-6 col-lg-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><span class="sf-stat-icon sf-purple" aria-hidden="true">📚</span><p class="text-body-secondary mt-3 mb-1">My Courses</p><p class="display-6 fw-bold mb-1"><%= courseCount %></p><small class="text-body-secondary">Shared catalog and your courses</small></div></article></div>
            <div class="col-12 col-sm-6 col-lg-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><span class="sf-stat-icon sf-green" aria-hidden="true">✅</span><p class="text-body-secondary mt-3 mb-1">Completed Tasks</p><p class="display-6 fw-bold mb-1"><%= completedTaskCount %><span class="fs-5 text-body-secondary"> / <%= taskCount %></span></p><div class="progress mt-3" role="progressbar" aria-label="Task completion" aria-valuenow="<%= taskPercent %>" aria-valuemin="0" aria-valuemax="100"><div class="progress-bar" style="width:<%= taskPercent %>%"></div></div></div></article></div>
            <div class="col-12 col-sm-6 col-lg-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><span class="sf-stat-icon sf-orange" aria-hidden="true">🔥</span><p class="text-body-secondary mt-3 mb-1">Study Streak</p><p class="display-6 fw-bold mb-1"><%= streak %> <span class="fs-5">days</span></p><small class="text-body-secondary">Based on saved study sessions</small></div></article></div>
        </section>
        <section class="card sf-panel mb-4"><div class="card-body p-4">
            <div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3"><div><h2 class="h5 fw-bold mb-1">Your Study Activity 🌱</h2><p class="small text-body-secondary mb-0">Daily study minutes from the last year.</p></div><a class="small" href="<%= request.getContextPath() %>/ActivityController">View analytics</a></div>
            <div class="sf-heatmap-scroll"><div class="sf-heatmap" role="img" aria-label="Daily study activity heatmap for the past year">
                <%
                LocalDate startDate = LocalDate.now().minusDays(364);
                startDate = startDate.with(DayOfWeek.MONDAY);
                for (int d = 0; d < 371; d++) {
                    LocalDate date = startDate.plusDays(d);
                    int minutes = activity == null ? 0 : activity.getOrDefault(date, 0);
                    int level = levels.getActivityLevel(minutes);
                %><span class="sf-heat-cell" data-level="<%= level %>" title="<%= date %>: <%= minutes %> minutes"></span><%
                }
                %>
            </div></div>
            <div class="d-flex align-items-center justify-content-end gap-2 mt-3"><small class="text-body-secondary">Less</small><% for (int i=0;i<=4;i++) { %><span class="sf-heat-cell" data-level="<%= i %>" aria-hidden="true"></span><% } %><small class="text-body-secondary">More</small></div>
        </div></section>
        <section class="row g-4">
            <div class="col-12 col-lg-7"><article class="card sf-panel h-100"><div class="card-body p-4"><div class="mb-4"><h2 class="h5 fw-bold mb-1">Study Progress 📈</h2><p class="small text-body-secondary mb-0">Saved study minutes over the last 7 days</p></div>
                <div class="sf-weekly-chart" role="img" aria-label="Study minutes during the past seven days">
                    <% int maxMinutes=1; if (weeklyMinutes != null) for (Integer value:weeklyMinutes) if(value!=null) maxMinutes=Math.max(maxMinutes,value); %>
                    <% if (weeklyMinutes != null && weekdays != null) for (int i=0;i<weeklyMinutes.size();i++) { int value=weeklyMinutes.get(i); int height=value==0?2:Math.max(5,(int)Math.round(value*100.0/maxMinutes)); %>
                    <div class="sf-bar-item"><div class="sf-bar" style="height:<%= height %>%" title="<%= Html.escape(weekdays.get(i)) %>: <%= value %> minutes"></div><span><%= Html.escape(weekdays.get(i)) %></span><small><%= value %>m</small></div>
                    <% } %>
                </div>
            </div></article></div>
            <div class="col-12 col-lg-5"><article class="card sf-panel h-100"><div class="card-body p-4"><div class="d-flex justify-content-between align-items-center mb-3"><h2 class="h5 fw-bold mb-0">Upcoming Tasks 📝</h2><a class="small" href="<%= request.getContextPath() %>/TaskController">View all</a></div>
                <% if (upcomingTasks == null || upcomingTasks.isEmpty()) { %><div class="sf-empty-state"><span aria-hidden="true">✨</span><p class="mb-2">Nothing on your list yet.</p><a href="<%= request.getContextPath() %>/TaskController">Add your first task</a></div><% } else { for (Task task : upcomingTasks) { %>
                <div class="sf-task-row"><div><h3 class="fs-6 fw-semibold mb-1"><%= Html.escape(task.getTitle()) %></h3><small class="text-body-secondary"><%= Html.escape(task.getCourseName() == null ? "No course" : task.getCourseName()) %><% if(task.getDueAt()!=null) { %> · due <%= Html.escape(Html.dateTime(task.getDueAt())) %><% } %></small></div><span class="badge rounded-pill sf-priority-<%= Html.escape(task.getPriority()) %>"><%= Html.escape(task.getPriority()) %></span></div>
                <% } } %>
            </div></article></div>
        </section>
        <section class="mt-4"><div class="d-flex justify-content-between align-items-center mb-3"><h2 class="h5 fw-bold mb-0">Course progress</h2><a class="small" href="<%= request.getContextPath() %>/CoursesController">Manage courses</a></div><div class="row g-3">
            <% if (courses != null) { int shown=0; for(Study course:courses) { if(shown++>=3) break; %>
            <div class="col-12 col-md-4"><article class="card sf-course-mini h-100"><div class="card-body"><div class="d-flex justify-content-between gap-2"><h3 class="h6 fw-bold"><%= Html.escape(course.getCourseName()) %></h3><span class="small text-body-secondary"><%= course.getProgressPercent() %>%</span></div><div class="progress" role="progressbar" aria-label="<%= Html.escape(course.getCourseName()) %> task completion" aria-valuenow="<%= course.getProgressPercent() %>" aria-valuemin="0" aria-valuemax="100"><div class="progress-bar" style="width:<%= course.getProgressPercent() %>%"></div></div><small class="text-body-secondary"><%= course.getCompletedTaskCount() %> of <%= course.getTaskCount() %> tasks complete</small></div></article></div>
            <% } } %>
        </div></section>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body>
</html>
