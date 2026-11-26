<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDate,java.time.DayOfWeek,java.time.LocalDateTime,java.time.format.DateTimeFormatter,java.util.List,java.util.Map,aiwa.entity.Study,aiwa.entity.StudySession,aiwa.ulti.Html,aiwa.service.ActivityService" %>
<%
    List<Study> courses = (List<Study>) request.getAttribute("courses");
    List<StudySession> sessions = (List<StudySession>) request.getAttribute("sessions");
    List<Integer> weeklyMinutes = (List<Integer>) request.getAttribute("weeklyMinutes");
    List<String> weekdays = (List<String>) request.getAttribute("weekdays");
    Map<LocalDate, Integer> activity = (Map<LocalDate, Integer>) request.getAttribute("activity");
    Map<LocalDate, Integer> monthlyActivity = (Map<LocalDate, Integer>) request.getAttribute("monthlyActivity");
    Map<LocalDate, Integer> weeklyActions = (Map<LocalDate, Integer>) request.getAttribute("weeklyActions");
    Map<LocalDate, Integer> monthlyActions = (Map<LocalDate, Integer>) request.getAttribute("monthlyActions");
    Map<LocalDate, Integer> activityActions = (Map<LocalDate, Integer>) request.getAttribute("activityActions");
    StudySession activeSession = (StudySession) request.getAttribute("activeSession");
    long totalMinutes = request.getAttribute("totalMinutes") instanceof Long ? (Long)request.getAttribute("totalMinutes") : 0;
    int streak = request.getAttribute("streak") instanceof Integer ? (Integer)request.getAttribute("streak") : 0;
    ActivityService levels = new ActivityService();
    Object flashMessage = request.getAttribute("flashMessage");
    String defaultStart = LocalDateTime.now().minusMinutes(30).withSecond(0).withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

    String weeklyLabelsJson = weekdays == null ? "[]" : "[\"" + String.join("\",\"", weekdays) + "\"]";
    String weeklyValuesJson = weeklyMinutes == null ? "[]" : weeklyMinutes.toString();
    StringBuilder weeklyActionsJson = new StringBuilder("[");
    for (int i = 6; i >= 0; i--) {
        if (i < 6) weeklyActionsJson.append(',');
        weeklyActionsJson.append(weeklyActions == null ? 0 : weeklyActions.getOrDefault(LocalDate.now().minusDays(i), 0));
    }
    weeklyActionsJson.append(']');
    StringBuilder monthlyLabelsJson = new StringBuilder("[");
    StringBuilder monthlyValuesJson = new StringBuilder("[");
    StringBuilder monthlyActionsJson = new StringBuilder("[");
    for (int daysAgo = 29; daysAgo >= 0; daysAgo--) {
        LocalDate date = LocalDate.now().minusDays(daysAgo);
        if (daysAgo < 29) {
            monthlyLabelsJson.append(',');
            monthlyValuesJson.append(',');
            monthlyActionsJson.append(',');
        }
        monthlyLabelsJson.append('\"').append(date).append('\"');
        monthlyValuesJson.append(monthlyActivity == null ? 0 : monthlyActivity.getOrDefault(date, 0));
        monthlyActionsJson.append(monthlyActions == null ? 0 : monthlyActions.getOrDefault(date, 0));
    }
    monthlyLabelsJson.append(']');
    monthlyValuesJson.append(']');
    monthlyActionsJson.append(']');
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Analytics | StudyFlow</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
  </head>
  <body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container sf-page sf-analytics-page py-5">
      <header class="sf-page-heading">
        <div>
          <span class="sf-eyebrow">NOTICE YOUR MOMENTUM</span>
          <h1 class="fw-bold mt-2">Study Analytics</h1>
          <p class="text-body-secondary">
            Small sessions add up. Log your effort and see it over time.
          </p>
        </div>
      </header>
      <% if(flashMessage!=null) { %>
      <div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div>
      <% } %>
<% if(request.getAttribute("pageError")!=null) { %>
      <div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div>
      <% } %>
      <section class="row g-3 mb-4">
        <div class="col-12 col-sm-6 col-lg-4">
          <article class="card sf-stat-card h-100">
            <div class="card-body p-4">
              <span class="sf-stat-icon sf-purple" aria-hidden="true">⏱️</span>
              <p class="text-body-secondary mt-3 mb-1">Total study time</p>
              <p class="display-6 fw-bold mb-0">
                <%= totalMinutes/60 %><span class="fs-5 text-body-secondary">h</span>
                <%= totalMinutes%60 %><span class="fs-5 text-body-secondary">m</span>
              </p>
            </div>
          </article>
        </div>
        <div class="col-12 col-sm-6 col-lg-4">
          <article class="card sf-stat-card h-100">
            <div class="card-body p-4">
              <span class="sf-stat-icon sf-orange" aria-hidden="true">🔥</span>
              <p class="text-body-secondary mt-3 mb-1">Current streak</p>
              <p class="display-6 fw-bold mb-0">
                <%= streak %> <span class="fs-5 text-body-secondary">days</span>
              </p>
            </div>
          </article>
        </div>
        <div class="col-12 col-sm-6 col-lg-4">
          <article class="card sf-stat-card h-100">
            <div class="card-body p-4">
              <span class="sf-stat-icon sf-green" aria-hidden="true">📅</span>
              <p class="text-body-secondary mt-3 mb-1">Recent sessions</p>
              <p class="display-6 fw-bold mb-0"><%= sessions==null?0:sessions.size() %></p>
              <small class="text-body-secondary"
                >Most recent 30 saved sessions</small
              >
            </div>
          </article>
        </div>
      </section>
      <section class="row g-4 mb-4">
        <div class="col-12 col-lg-5">
          <article class="card sf-panel h-100">
            <div class="card-body p-4">
              <h2 class="h5 fw-bold mb-1">Study timer</h2>
              <p class="small text-body-secondary mb-3">
                Track real study time. Your session is saved when you stop the timer.
              </p>
              <% if(activeSession == null) { %>
              <form method="post" action="<%= request.getContextPath() %>/ActivityController" class="mb-4">
                <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" />
                <input type="hidden" name="action" value="timer_start" />
                <label class="form-label" for="timerCourse">Course <span class="text-body-secondary">(optional)</span></label>
                <div class="d-flex gap-2">
                  <select class="form-select" id="timerCourse" name="courseId">
                    <option value="">Independent study</option>
                    <% if(courses!=null) for(Study c:courses) { %><option value="<%= c.getCourseId() %>"><%= Html.escape(c.getCourseName()) %></option><% } %>
                  </select>
                  <button class="btn btn-primary rounded-pill px-4" type="submit">Start</button>
                </div>
              </form>
              <% } else { %>
              <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4 p-3 rounded-3 bg-body-tertiary">
                <div>
                  <span class="small text-body-secondary d-block">Timer running · <%= Html.escape(activeSession.getCourseName()==null?"Independent study":activeSession.getCourseName()) %></span>
                  <strong class="fs-3" id="studyTimerElapsed" data-start="<%= activeSession.getStartedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) %>" aria-live="off">00:00:00</strong>
                </div>
                <form method="post" action="<%= request.getContextPath() %>/ActivityController">
                  <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" />
                  <input type="hidden" name="action" value="timer_stop" />
                  <button class="btn btn-outline-danger rounded-pill px-4" type="submit">Stop &amp; save</button>
                </form>
              </div>
              <% } %>
              <hr />
              <% if(activeSession != null) { %>
              <p class="small text-body-secondary mb-0" role="status">
                Stop the running timer before adding a past session.
              </p>
              <% } else { %>
              <h3 class="h6 fw-bold mb-1">Log a past session</h3>
              <p class="small text-body-secondary mb-3">Add a finished session manually.</p>
              <form
                method="post"
                action="<%= request.getContextPath() %>/ActivityController"
                class="row g-3"
              >
                <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" />
                <div class="col-12">
                  <label class="form-label" for="studyCourse"
                    >Course
                    <span class="text-body-secondary">(optional)</span></label
                  ><select class="form-select" id="studyCourse" name="courseId">
                    <option value="">Independent study</option>
                    <% if(courses!=null) for(Study c:courses) { %>
                    <option value="<%= c.getCourseId() %>"><%= Html.escape(c.getCourseName()) %></option>
                    <% } %>
                  </select>
                </div>
                <div class="col-12">
                  <label class="form-label" for="startedAt"
                    >Date and start time</label
                  ><input
                    class="form-control"
                    id="startedAt"
                    name="startedAt"
                    type="datetime-local"
                    value="<%= defaultStart %>"
                    required
                  />
                </div>
                <div class="col-12">
                  <label class="form-label" for="minutes">Duration</label>
                  <div class="input-group">
                    <input
                      class="form-control"
                      id="minutes"
                      name="minutes"
                      type="number"
                      min="1"
                      max="720"
                      value="30"
                      required
                    /><span class="input-group-text">minutes</span>
                  </div>
                </div>
                <div class="col-12">
                  <label class="form-label" for="notes"
                    >Note
                    <span class="text-body-secondary">(optional)</span></label
                  ><textarea
                    class="form-control"
                    id="notes"
                    name="notes"
                    rows="2"
                    maxlength="500"
                    placeholder="What did you work on?"
                  ></textarea>
                </div>
                <div class="col-12">
                  <button
                    class="btn btn-primary rounded-pill px-4"
                    type="submit"
                  >
                    Save session
                  </button>
                </div>
              </form>
              <% } %>
            </div>
          </article>
        </div>
        <div class="col-12 col-lg-7">
          <article class="card sf-panel h-100">
            <div class="card-body p-4">
              <h2 class="h5 fw-bold mb-1">Last 7 days 📈</h2>
              <p class="small text-body-secondary mb-4">
                Study minutes and completed activities
              </p>
              <div class="sf-analytics-chart sf-weekly-chart">
                <canvas
                  id="weeklyStudyChart"
                  role="img"
                  aria-label="Daily study minutes over the last seven days"
                ></canvas>
              </div>
              <p class="small text-body-secondary mt-3 mb-0">
                Activity includes course visits, quiz attempts, task updates, and saved notes.
              </p>
            </div>
          </article>
        </div>
      </section>
      <section class="card sf-panel mb-4">
        <div class="card-body p-4">
          <h2 class="h5 fw-bold mb-1">30-day trend</h2>
          <p class="small text-body-secondary mb-3">
            Study minutes and completed activities by day.
          </p>
          <div class="sf-analytics-chart sf-monthly-chart">
            <canvas
              id="monthlyStudyChart"
              role="img"
              aria-label="Daily study minutes over the last 30 days"
            ></canvas>
          </div>
        </div>
      </section>
      <section class="card sf-panel mb-4">
        <div class="card-body p-4">
          <div class="mb-3">
            <h2 class="h5 fw-bold mb-1">Your Study Activity 🌱</h2>
            <p class="small text-body-secondary mb-0">
              Study minutes and activity days for the last year.
            </p>
          </div>
          <div class="sf-heatmap-scroll">
            <div
              class="sf-heatmap"
              role="img"
              aria-label="Daily study activity heatmap"
            >
              <% LocalDate startDate=LocalDate.now().minusDays(364).with(DayOfWeek.MONDAY); for(int d=0;d<371;d++) { LocalDate date=startDate.plusDays(d); int mins=activity==null?0:activity.getOrDefault(date,0); int actions=activityActions==null?0:activityActions.getOrDefault(date,0); int level=levels.getActivityLevel(mins); if(actions>0 && level==0) level=1; %><span
                class="sf-heat-cell"
                data-level="<%= level %>"
                title="<%= date %>: <%= mins %> minutes, <%= actions %> activities"
              ></span
              ><% } %>
            </div>
          </div>
        </div>
      </section>
      <section class="card sf-panel">
        <div class="card-body p-4">
          <h2 class="h5 fw-bold mb-3">Recent sessions</h2>
          <% if(sessions==null || sessions.isEmpty()) { %>
          <div class="sf-empty-state">
            <span aria-hidden="true">🌱</span>
            <p class="mb-1">Your study history will show up here.</p>
            <small class="text-body-secondary"
              >Start the timer, complete a quiz or task, open a course, or save a note to build your activity record.</small
            >
          </div>
          <% } else { %>
          <div class="table-responsive">
            <table class="table align-middle">
              <thead>
                <tr>
                  <th scope="col">When</th>
                  <th scope="col">Course</th>
                  <th scope="col">Duration</th>
                  <th scope="col">Note</th>
                </tr>
              </thead>
              <tbody>
                <% for(StudySession s:sessions) { %>
                <tr>
                  <td><%= Html.escape(Html.dateTime(s.getStartedAt())) %></td>
                  <td><%= Html.escape(s.getCourseName()==null?"Independent study":s.getCourseName()) %></td>
                  <td><%= s.getDurationMinutes() %> min</td>
                  <td><%= Html.escape(s.getNotes()) %></td>
                </tr>
                <% } %>
              </tbody>
            </table>
          </div>
          <% } %>
        </div>
      </section>
    </main>
    <script id="analytics-chart-data" type="application/json">
      {
        "weekly": {
          "labels": <%= weeklyLabelsJson %>,
          "values": <%= weeklyValuesJson %>,
          "actions": <%= weeklyActionsJson.toString() %>
        },
        "monthly": {
          "labels": <%= monthlyLabelsJson.toString() %>,
          "values": <%= monthlyValuesJson.toString() %>,
          "actions": <%= monthlyActionsJson.toString() %>
        }
      }
    </script>
    <script
      src="https://cdn.jsdelivr.net/npm/chart.js@4.6.2/dist/chart.umd.min.js"
      defer
    ></script>
    <script
      src="<%= request.getContextPath() %>/assets/js/analytics.js?v=20261126-1"
      defer
    ></script>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
  </body>
</html>
