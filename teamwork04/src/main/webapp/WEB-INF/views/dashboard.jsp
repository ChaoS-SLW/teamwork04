<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDate,java.time.DayOfWeek,java.time.format.DateTimeFormatter,java.util.List,java.util.Map,aiwa.entity.Study,aiwa.entity.Task,aiwa.entity.PlannerItem,aiwa.ulti.Html,aiwa.ulti.Ulti,aiwa.service.ActivityService" %>
<%
    int courseCount = request.getAttribute("courseCount") instanceof Integer ? (Integer) request.getAttribute("courseCount") : 0;
    int taskCount = request.getAttribute("taskCount") instanceof Integer ? (Integer) request.getAttribute("taskCount") : 0;
    int completedTaskCount = request.getAttribute("completedTaskCount") instanceof Integer ? (Integer) request.getAttribute("completedTaskCount") : 0;
    int taskPercent = taskCount == 0 ? 0 : Math.round(completedTaskCount * 100f / taskCount);
    int streak = request.getAttribute("streak") instanceof Integer ? (Integer) request.getAttribute("streak") : 0;
    List<Study> courses = (List<Study>) request.getAttribute("courses");
    List<Study> inProgressCourses = (List<Study>) request.getAttribute("inProgressCourses");
    List<Task> upcomingTasks = (List<Task>) request.getAttribute("upcomingTasks");
    List<PlannerItem> plannerItems = (List<PlannerItem>) request.getAttribute("plannerItems");
    LocalDate plannerWeekStart = (LocalDate) request.getAttribute("plannerWeekStart");
    DateTimeFormatter plannerTime = DateTimeFormatter.ofPattern("EEE · h:mm a");
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
    <main class="container sf-page sf-dashboard-page py-4 py-lg-5">
      <% if (flashMessage != null) { %>
      <div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div>
      <% } %>
<% if (request.getAttribute("pageError") != null) { %>
      <div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div>
      <% } %>
      <section class="sf-welcome mb-4">
        <div class="row align-items-end g-3">
          <div class="col-12 col-lg-7">
            <span class="sf-eyebrow">YOUR STUDY SPACE ✨</span>
            <h1 class="fw-bold mt-2 mb-2"><%= Ulti.showGreeting() %></h1>
            <p class="text-body-secondary mb-0">
              七転び八起き。
            </p>
          </div>
          <div
            class="col-12 col-lg-5 d-flex flex-wrap gap-2 justify-content-lg-end"
          >
            <a
              href="<%= request.getContextPath() %>/TaskController"
              class="btn btn-primary rounded-pill px-4"
              >+ New task</a
            >
          </div>
        </div>
      </section>
      <section class="row g-3 mb-4" aria-label="Study overview">
        <div class="col-12 col-sm-6 col-lg-4">
          <article class="card sf-stat-card h-100">
            <div class="card-body p-4">
              <span class="sf-stat-icon sf-purple" aria-hidden="true">📚</span>
              <p class="text-body-secondary mt-3 mb-1">Courses</p>
              <p class="display-6 fw-bold mb-1"><%= courseCount %></p>
              <small class="text-body-secondary"
                >Shared catalog and your courses</small
              >
            </div>
          </article>
        </div>
        <div class="col-12 col-sm-6 col-lg-4">
          <article class="card sf-stat-card h-100">
            <div class="card-body p-4">
              <span class="sf-stat-icon sf-green" aria-hidden="true">✅</span>
              <p class="text-body-secondary mt-3 mb-1">Completed Tasks</p>
              <p class="display-6 fw-bold mb-1">
                <%= completedTaskCount %><span class="fs-5 text-body-secondary">
                  / <%= taskCount %></span
                >
              </p>
              <div
                class="progress mt-3"
                role="progressbar"
                aria-label="Task completion"
                aria-valuenow="<%= taskPercent %>"
                aria-valuemin="0"
                aria-valuemax="100"
              >
                <div class="progress-bar" style="width: <%= taskPercent %>%"></div>
              </div>
            </div>
          </article>
        </div>
        <div class="col-12 col-sm-6 col-lg-4">
          <article class="card sf-stat-card h-100">
            <div class="card-body p-4">
              <span class="sf-stat-icon sf-orange" aria-hidden="true">🔥</span>
              <p class="text-body-secondary mt-3 mb-1">Study Streak</p>
              <p class="display-6 fw-bold mb-1">
                <%= streak %> <span class="fs-5">days</span>
              </p>
              <small class="text-body-secondary"
                >Based on saved study sessions</small
              >
            </div>
          </article>
        </div>
      </section>
      <section class="card sf-panel mb-4">
        <div class="card-body p-4">
          <div
            class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3"
          >
            <div>
              <h2 class="h5 fw-bold mb-1">Your Study Activity 🌱</h2>
              <p class="small text-body-secondary mb-0">
                Daily study minutes from the last year.
              </p>
            </div>
            <a class="small" href="<%= request.getContextPath() %>/ActivityController"
              >View analytics</a
            >
          </div>
          <div class="sf-heatmap-scroll">
            <div
              class="sf-heatmap"
              role="img"
              aria-label="Daily study activity heatmap for the past year"
            >
              <%
                LocalDate startDate = LocalDate.now().minusDays(364);
                startDate = startDate.with(DayOfWeek.MONDAY);
                for (int d = 0; d < 371; d++) {
                    LocalDate date = startDate.plusDays(d);
                    int minutes = activity == null ? 0 : activity.getOrDefault(date, 0);
                    int level = levels.getActivityLevel(minutes);
                %><span
                class="sf-heat-cell"
                data-level="<%= level %>"
                title="<%= date %>: <%= minutes %> minutes"
              ></span
              ><%
                }
                %>
            </div>
          </div>
          <div class="d-flex align-items-center justify-content-end gap-2 mt-3">
            <small class="text-body-secondary">Less</small><% for (int i=0;i<=4;i++) { %><span
              class="sf-heat-cell"
              data-level="<%= i %>"
              aria-hidden="true"
            ></span
            ><% } %><small class="text-body-secondary">More</small>
          </div>
        </div>
      </section>
      <section class="row g-4">
        <div class="col-12 col-lg-7">
          <article class="card sf-panel h-100">
            <div class="card-body p-4">
              <div class="mb-4">
                <h2 class="h5 fw-bold mb-1">Study Progress 📈</h2>
                <p class="small text-body-secondary mb-0">
                  Saved study minutes over the last 7 days
                </p>
              </div>
              <div
                class="sf-weekly-chart"
                role="img"
                aria-label="Study minutes during the past seven days"
              >
                <% int maxMinutes=1; if (weeklyMinutes != null) for (Integer value:weeklyMinutes) if(value!=null) maxMinutes=Math.max(maxMinutes,value); %>
<% if (weeklyMinutes != null && weekdays != null) for (int i=0;i<weeklyMinutes.size();i++) { int value=weeklyMinutes.get(i); int height=value==0?2:Math.max(5,(int)Math.round(value*100.0/maxMinutes)); %>
                <div class="sf-bar-item">
                  <div
                    class="sf-bar"
                    style="height: <%= height %>%"
                    title="<%= Html.escape(weekdays.get(i)) %>: <%= value %> minutes"
                  ></div>
                  <span><%= Html.escape(weekdays.get(i)) %></span><small><%= value %>m</small>
                </div>
                <% } %>
              </div>
            </div>
          </article>
        </div>
        <div class="col-12 col-lg-5">
          <article class="card sf-panel h-100">
            <div class="card-body p-4">
              <div
                class="d-flex justify-content-between align-items-center mb-3"
              >
                <div>
                  <span class="sf-eyebrow">YOUR NEXT STEPS</span>
                  <h2 class="h5 fw-bold mb-0 mt-1">Upcoming Tasks 📝</h2>
                </div>
                <a class="small" href="<%= request.getContextPath() %>/TaskController"
                  >Manage tasks</a
                >
              </div>
              <% if (upcomingTasks == null || upcomingTasks.isEmpty()) { %>
              <div class="sf-empty-state">
                <span aria-hidden="true">✨</span>
                <p class="mb-2">Nothing on your list yet.</p>
                <a href="<%= request.getContextPath() %>/TaskController#new-task"
                  >Add your first task</a
                >
              </div>
              <% } else { for (Task task : upcomingTasks) { %>
              <div class="sf-task-row sf-dashboard-task-row">
                <form
                  method="post"
                  action="<%= request.getContextPath() %>/TaskController"
                  class="sf-inline-form"
                >
                  <input
                    type="hidden"
                    name="csrfToken"
                    value="<%= Html.csrfToken(request) %>"
                  /><input type="hidden" name="action" value="toggle" /><input
                    type="hidden"
                    name="taskId"
                    value="<%= task.getTaskId() %>"
                  /><input type="hidden" name="completed" value="true" /><input
                    type="hidden"
                    name="returnTo"
                    value="dashboard"
                  /><button
                    class="sf-check-button"
                    type="submit"
                    aria-label="Complete <%= Html.escape(task.getTitle()) %>"
                  ></button>
                </form>
                <div class="sf-dashboard-task-copy">
                  <h3 class="fs-6 fw-semibold mb-1"><%= Html.escape(task.getTitle()) %></h3>
                  <small class="text-body-secondary"
                    ><%= Html.escape(task.getCourseName() == null ? "No course" : task.getCourseName()) %><% if(task.getDueAt()!=null) { %> · due
                    <%= Html.escape(Html.dateTime(task.getDueAt())) %><% } %></small
                  >
                </div>
                <span class="badge rounded-pill sf-priority-<%= Html.escape(task.getPriority()) %>"
                  ><%= Html.escape(task.getPriority()) %></span
                >
              </div>
              <% } } %>
            </div>
          </article>
        </div>
      </section>
      <section class="card sf-panel sf-dashboard-planner mt-4">
        <div class="card-body p-4">
          <div
            class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-3"
          >
            <div>
              <span class="sf-eyebrow">THIS WEEK</span>
              <h2 class="h5 fw-bold mb-0 mt-1">Planner at a glance 📅</h2>
              <p class="small text-body-secondary mb-0">
                Deadlines, exams, and study time in one place.
              </p>
            </div>
            <a
              class="btn btn-sm btn-outline-primary rounded-pill px-3"
              href="<%= request.getContextPath() %>/PlannerController?week=<%= plannerWeekStart %>"
              >Open weekly planner →</a
            >
          </div>
          <% if (plannerItems == null || plannerItems.isEmpty()) { %>
          <div class="sf-planner-dashboard-empty">
            <span aria-hidden="true">🌿</span>
            <div>
              <strong>Your week is open.</strong>
              <p class="small text-body-secondary mb-0">
                Add a study block or exam to start planning.
              </p>
            </div>
          </div>
          <% } else { %>
          <div class="sf-dashboard-planner-list">
            <% int shownPlannerItems = 0; for (PlannerItem item : plannerItems) { if (shownPlannerItems++ >= 6) break; String kind = "task".equals(item.getType()) ? "Deadline" : "session".equals(item.getType()) ? "Logged study" : "exam".equals(item.getType()) ? "Exam" : item.isCompleted() ? "Done" : "Study block"; %>
            <article
              class="sf-dashboard-planner-item is-<%= Html.escape(item.getType()) %>
<%= item.isCompleted() ? "is-done" : "" %>"
            >
              <span class="sf-dashboard-planner-date"><%= item.getStartsAt().format(plannerTime) %></span>
              <div class="sf-dashboard-planner-copy">
                <strong><%= Html.escape(item.getTitle()) %></strong><small><%= Html.escape(item.getCourseName()) %></small>
              </div>
              <span class="sf-dashboard-planner-kind"><%= kind %></span>
            </article>
            <% } %>
          </div>
          <% if (plannerItems.size() > 6) { %>
          <p class="small text-body-secondary mt-2 mb-0">
            Showing 6 of <%= plannerItems.size() %> items this week.
          </p>
          <% } %>
<% } %>
        </div>
      </section>
      <section class="mt-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h2 class="h5 fw-bold mb-0">Course progress</h2>
          <a class="small" href="<%= request.getContextPath() %>/CoursesController"
            >Manage courses</a
          >
        </div>
        <% if (inProgressCourses == null || inProgressCourses.isEmpty()) { %>
        <div class="sf-empty-state py-4">
          <p class="mb-1">No courses in progress yet.</p>
          <small class="text-body-secondary">Start a course task or complete a lesson quiz to see it here.</small>
        </div>
        <% } else { %>
        <div class="row g-3">
          <% int shown=0; for(Study course:inProgressCourses) { if(shown++>=3) break; %>
          <div class="col-12 col-md-4">
            <article class="card sf-course-mini h-100">
              <div class="card-body">
                <div class="d-flex justify-content-between gap-2">
                  <h3 class="h6 fw-bold"><%= Html.escape(course.getCourseName()) %></h3>
                  <span class="badge rounded-pill text-bg-light">In progress</span>
                </div>
                <div
                  class="progress"
                  role="progressbar"
                  aria-label="<%= Html.escape(course.getCourseName()) %> course completion"
                  aria-valuenow="<%= course.getProgressPercent() %>"
                  aria-valuemin="0"
                  aria-valuemax="100"
                >
                  <div class="progress-bar" style="width: <%= course.getProgressPercent() %>%"></div>
                </div>
                <small class="text-body-secondary"
                  ><%= course.getCompletedTaskCount() %>/<%= course.getTaskCount() %> tasks · <%= course.getBestQuizCorrectCount() %>/<%= course.getTotalQuizQuestionCount() %> quiz answers correct</small
                >
              </div>
            </article>
          </div>
          <% } %>
        </div>
        <% } %>
      </section>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
  </body>
</html>
