<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDate,java.time.DayOfWeek,java.time.LocalDateTime,java.time.format.DateTimeFormatter,java.util.List,java.util.Map,aiwa.entity.Study,aiwa.entity.StudySession,aiwa.ulti.Html,aiwa.service.ActivityService" %>
<%
    List<Study> courses = (List<Study>) request.getAttribute("courses");
    List<StudySession> sessions = (List<StudySession>) request.getAttribute("sessions");
    List<Integer> weeklyMinutes = (List<Integer>) request.getAttribute("weeklyMinutes");
    List<String> weekdays = (List<String>) request.getAttribute("weekdays");
    Map<LocalDate, Integer> activity = (Map<LocalDate, Integer>) request.getAttribute("activity");
    Map<LocalDate, Integer> monthlyActivity = (Map<LocalDate, Integer>) request.getAttribute("monthlyActivity");
    long totalMinutes = request.getAttribute("totalMinutes") instanceof Long ? (Long)request.getAttribute("totalMinutes") : 0;
    int streak = request.getAttribute("streak") instanceof Integer ? (Integer)request.getAttribute("streak") : 0;
    ActivityService levels = new ActivityService();
    Object flashMessage = request.getAttribute("flashMessage");
    String defaultStart = LocalDateTime.now().minusMinutes(30).withSecond(0).withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
%>
<!DOCTYPE html>
<html lang="en">
<head><title>Analytics | StudyFlow</title><%@ include file="/WEB-INF/views/components/head.jspf" %></head>
<body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container-xl sf-page py-4 py-lg-5">
        <header class="sf-page-heading"><div><span class="sf-eyebrow">NOTICE YOUR MOMENTUM</span><h1 class="fw-bold mt-2">Study Analytics</h1><p class="text-body-secondary">Small sessions add up. Log your effort and see it over time.</p></div></header>
        <% if(flashMessage!=null) { %><div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div><% } %>
        <% if(request.getAttribute("pageError")!=null) { %><div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div><% } %>
        <section class="row g-3 mb-4"><div class="col-12 col-sm-6 col-lg-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><span class="sf-stat-icon sf-purple" aria-hidden="true">⏱️</span><p class="text-body-secondary mt-3 mb-1">Total study time</p><p class="display-6 fw-bold mb-0"><%= totalMinutes/60 %><span class="fs-5 text-body-secondary">h</span> <%= totalMinutes%60 %><span class="fs-5 text-body-secondary">m</span></p></div></article></div><div class="col-12 col-sm-6 col-lg-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><span class="sf-stat-icon sf-orange" aria-hidden="true">🔥</span><p class="text-body-secondary mt-3 mb-1">Current streak</p><p class="display-6 fw-bold mb-0"><%= streak %> <span class="fs-5 text-body-secondary">days</span></p></div></article></div><div class="col-12 col-sm-6 col-lg-4"><article class="card sf-stat-card h-100"><div class="card-body p-4"><span class="sf-stat-icon sf-green" aria-hidden="true">📅</span><p class="text-body-secondary mt-3 mb-1">Recent sessions</p><p class="display-6 fw-bold mb-0"><%= sessions==null?0:sessions.size() %></p><small class="text-body-secondary">Most recent 30 saved sessions</small></div></article></div></section>
        <section class="row g-4 mb-4">
            <div class="col-12 col-lg-5"><article class="card sf-panel h-100"><div class="card-body p-4"><h2 class="h5 fw-bold mb-1">Log a study session</h2><p class="small text-body-secondary mb-3">Add a finished session to your activity.</p><form method="post" action="<%= request.getContextPath() %>/ActivityController" class="row g-3">
                <div class="col-12"><label class="form-label" for="studyCourse">Course <span class="text-body-secondary">(optional)</span></label><select class="form-select" id="studyCourse" name="courseId"><option value="">Independent study</option><% if(courses!=null) for(Study c:courses) { %><option value="<%= c.getCourseId() %>"><%= Html.escape(c.getCourseName()) %></option><% } %></select></div>
                <div class="col-12"><label class="form-label" for="startedAt">Date and start time</label><input class="form-control" id="startedAt" name="startedAt" type="datetime-local" value="<%= defaultStart %>" required></div>
                <div class="col-12"><label class="form-label" for="minutes">Duration</label><div class="input-group"><input class="form-control" id="minutes" name="minutes" type="number" min="1" max="720" value="30" required><span class="input-group-text">minutes</span></div></div>
                <div class="col-12"><label class="form-label" for="notes">Note <span class="text-body-secondary">(optional)</span></label><textarea class="form-control" id="notes" name="notes" rows="2" maxlength="500" placeholder="What did you work on?"></textarea></div>
                <div class="col-12"><button class="btn btn-primary rounded-pill px-4" type="submit">Save session</button></div>
            </form></div></article></div>
            <div class="col-12 col-lg-7"><article class="card sf-panel h-100"><div class="card-body p-4"><h2 class="h5 fw-bold mb-1">Last 7 days 📈</h2><p class="small text-body-secondary mb-4">Minutes from sessions you logged</p><div class="sf-weekly-chart" role="img" aria-label="Daily study minutes over seven days">
                <% int maxMinutes=1; if(weeklyMinutes!=null) for(Integer v:weeklyMinutes) if(v!=null) maxMinutes=Math.max(maxMinutes,v); %>
                <% if(weeklyMinutes!=null && weekdays!=null) for(int i=0;i<weeklyMinutes.size();i++) { int value=weeklyMinutes.get(i); int height=value==0?2:Math.max(5,(int)Math.round(value*100.0/maxMinutes)); %><div class="sf-bar-item"><div class="sf-bar" style="height:<%= height %>%" title="<%= Html.escape(weekdays.get(i)) %>: <%= value %> minutes"></div><span><%= Html.escape(weekdays.get(i)) %></span><small><%= value %>m</small></div><% } %>
            </div><p class="small text-body-secondary mt-3 mb-0">The chart updates when you save a session.</p></div></article></div>
        </section>
        <section class="card sf-panel mb-4"><div class="card-body p-4"><h2 class="h5 fw-bold mb-1">30-day trend</h2><p class="small text-body-secondary mb-3">A bar for each day with saved study time.</p><div class="sf-monthly-chart" role="img" aria-label="Daily study minutes over the last 30 days">
            <% int monthMax=1; if(monthlyActivity!=null) for(Integer v:monthlyActivity.values()) if(v!=null) monthMax=Math.max(monthMax,v); for(int d=29;d>=0;d--) { LocalDate date=LocalDate.now().minusDays(d); int mins=monthlyActivity==null?0:monthlyActivity.getOrDefault(date,0); int h=mins==0?2:Math.max(5,(int)Math.round(mins*100.0/monthMax)); %><span class="sf-monthly-bar" style="height:<%= h %>%" title="<%= date %>: <%= mins %> minutes"></span><% } %>
        </div><div class="sf-monthly-labels"><span>30 days ago</span><span>Today</span></div></div></section>
        <section class="card sf-panel mb-4"><div class="card-body p-4"><div class="mb-3"><h2 class="h5 fw-bold mb-1">Your Study Activity 🌱</h2><p class="small text-body-secondary mb-0">Daily study minutes for the last year.</p></div><div class="sf-heatmap-scroll"><div class="sf-heatmap" role="img" aria-label="Daily study activity heatmap">
            <% LocalDate startDate=LocalDate.now().minusDays(364).with(DayOfWeek.MONDAY); for(int d=0;d<371;d++) { LocalDate date=startDate.plusDays(d); int mins=activity==null?0:activity.getOrDefault(date,0); %><span class="sf-heat-cell" data-level="<%= levels.getActivityLevel(mins) %>" title="<%= date %>: <%= mins %> minutes"></span><% } %>
        </div></div></div></section>
        <section class="card sf-panel"><div class="card-body p-4"><h2 class="h5 fw-bold mb-3">Recent sessions</h2>
            <% if(sessions==null || sessions.isEmpty()) { %><div class="sf-empty-state"><span aria-hidden="true">🌱</span><p class="mb-1">Your study history will show up here.</p><small class="text-body-secondary">Log a session to start building your activity record.</small></div><% } else { %><div class="table-responsive"><table class="table align-middle"><thead><tr><th scope="col">When</th><th scope="col">Course</th><th scope="col">Duration</th><th scope="col">Note</th></tr></thead><tbody><% for(StudySession s:sessions) { %><tr><td><%= Html.escape(Html.dateTime(s.getStartedAt())) %></td><td><%= Html.escape(s.getCourseName()==null?"Independent study":s.getCourseName()) %></td><td><%= s.getDurationMinutes() %> min</td><td><%= Html.escape(s.getNotes()) %></td></tr><% } %></tbody></table></div><% } %>
        </div></section>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body>
</html>
