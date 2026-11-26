<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDate,java.time.LocalDateTime,java.time.format.DateTimeFormatter,java.util.List,java.util.Map,aiwa.entity.PlannerItem,aiwa.entity.Study,aiwa.ulti.Html" %>
<%
	LocalDate weekStart = (LocalDate) request.getAttribute("weekStart");
	LocalDate weekEnd = (LocalDate) request.getAttribute("weekEnd");
	Map<LocalDate, List<PlannerItem>> itemsByDay = (Map<LocalDate, List<PlannerItem>>) request.getAttribute("itemsByDay");
	List<Study> courses = (List<Study>) request.getAttribute("courses");
	Object flashMessage = request.getAttribute("flashMessage");
	DateTimeFormatter dayName = DateTimeFormatter.ofPattern("EEEE");
	DateTimeFormatter shortDate = DateTimeFormatter.ofPattern("MMM d");
	DateTimeFormatter clock = DateTimeFormatter.ofPattern("h:mm a");
	LocalDate today = LocalDate.now();
	boolean showCompleted = Boolean.TRUE.equals(request.getAttribute("showCompleted"));
	String completedQuery = showCompleted ? "&showCompleted=1" : "";
	boolean canPlan = !weekEnd.isBefore(today);
	LocalDateTime suggestedStart = weekStart.isAfter(today) ? weekStart.atTime(9, 0) : LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0);
	LocalDateTime suggestedEnd = suggestedStart.plusHours(1);
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Weekly Planner | StudyFlow</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
  </head>
  <body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container sf-page sf-planner-page py-5">
      <header class="sf-page-heading sf-planner-heading">
        <div>
          <span class="sf-eyebrow">MAKE TIME FOR WHAT MATTERS</span>
          <h1 class="fw-bold mt-2">Weekly Planner</h1>
          <p class="text-body-secondary">
            See deadlines and study time together, one week at a time.
          </p>
        </div>
        <div class="sf-week-switcher" aria-label="Choose week">
          <a
            class="btn btn-outline-primary rounded-pill"
            href="<%= request.getContextPath() %>/PlannerController?week=<%= weekStart.minusWeeks(1) %><%= completedQuery %>"
            aria-label="Previous week"
            >← <span class="d-none d-sm-inline">Previous</span></a
          >
          <strong><%= weekStart.format(shortDate) %> – <%= weekEnd.format(shortDate) %></strong>
          <a
            class="btn btn-outline-primary rounded-pill"
            href="<%= request.getContextPath() %>/PlannerController?week=<%= weekStart.plusWeeks(1) %><%= completedQuery %>"
            aria-label="Next week"
            ><span class="d-none d-sm-inline">Next</span> →</a
          >
          <% if (!weekStart.equals(today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)))) { %><a
            class="btn btn-sm btn-link"
            href="<%= request.getContextPath() %>/PlannerController<%= completedQuery.isEmpty() ? "" : "?showCompleted=1" %>"
            >This week</a
          ><% } %>
        </div>
      </header>
      <%@ include file="/WEB-INF/views/components/plantabs.jspf" %>
      <% if (flashMessage != null) { %>
      <div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div>
      <% } %>
<% if (request.getAttribute("pageError") != null) { %>
      <div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div>
      <% } %>

      <section
        class="card sf-panel sf-planner-create mb-4"
        aria-labelledby="planner-create-title"
      >
        <div class="card-body p-4">
          <div class="sf-section-title">
            <div>
              <span class="sf-eyebrow">PLAN AHEAD</span>
              <h2 class="h5 fw-bold mt-1 mb-1" id="planner-create-title">
                Add a study block or exam
              </h2>
              <p class="small text-body-secondary mb-0">
                Your planned blocks stay separate from completed study time.
              </p>
            </div>
            <span class="sf-planner-create-icon" aria-hidden="true">✦</span>
          </div>
          <% if (canPlan) { %>
          <form
            method="post"
            action="<%= request.getContextPath() %>/PlannerController"
            class="row g-3 mt-1 sf-planner-form"
          >
            <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" /><input
              type="hidden"
              name="action"
              value="create"
            /><input type="hidden" name="week" value="<%= weekStart %>" />
            <div class="col-12 col-md-3">
              <label class="form-label" for="eventType">Plan type</label
              ><select class="form-select" id="eventType" name="eventType">
                <option value="study">Study block</option>
                <option value="exam">Exam</option>
              </select>
            </div>
            <div class="col-12 col-md-5">
              <label class="form-label" for="plannerTitle">Title</label
              ><input
                class="form-control"
                id="plannerTitle"
                name="title"
                maxlength="120"
                required
                placeholder="e.g. Review Java collections"
              />
            </div>
            <div class="col-12 col-md-4">
              <label class="form-label" for="plannerCourse"
                >Course
                <span class="text-body-secondary">(optional)</span></label
              ><select class="form-select" id="plannerCourse" name="courseId">
                <option value="">No course</option>
                <% if (courses != null) for (Study course : courses) { %>
                <option value="<%= course.getCourseId() %>"><%= Html.escape(course.getCourseName()) %></option>
                <% } %>
              </select>
            </div>
            <div class="col-12 col-sm-6 col-lg-4">
              <label class="form-label" for="plannerStart"
                >Date and start time</label
              ><input
                class="form-control"
                type="datetime-local"
                id="plannerStart"
                name="startsAt"
                value="<%= suggestedStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) %>"
                required
              />
            </div>
            <div class="col-12 col-sm-6 col-lg-4 sf-planner-end-field">
              <label class="form-label" for="plannerEnd">End time</label
              ><input
                class="form-control"
                type="datetime-local"
                id="plannerEnd"
                name="endsAt"
                value="<%= suggestedEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) %>"
              />
            </div>
            <div class="col-12 col-lg-4 d-flex align-items-end">
              <button class="btn btn-primary rounded-pill px-4" type="submit">
                Add to planner
              </button>
            </div>
          </form>
          <% } else { %>
          <div class="sf-planner-past-note">
            This is a past week. Choose this week or a future week to add new
            plans.
          </div>
          <% } %>
        </div>
      </section>

      <div class="d-flex justify-content-end mb-3">
        <a
          class="btn btn-sm btn-outline-primary rounded-pill px-3"
          href="<%= request.getContextPath() %>/PlannerController?week=<%= weekStart %><%= showCompleted ? "" : "&showCompleted=1" %>"
          aria-pressed="<%= showCompleted %>"
        ><%= showCompleted ? "Hide completed items" : "Show completed items" %></a>
      </div>

      <section class="sf-week-grid" aria-label="Events for the week">
        <% for (Map.Entry<LocalDate, List<PlannerItem>> dayEntry : itemsByDay.entrySet()) { LocalDate date = dayEntry.getKey(); List<PlannerItem> items = dayEntry.getValue(); boolean isToday = date.equals(today); %>
        <article class="sf-week-day <%= isToday ? "is-today" : "" %>">
          <header class="sf-week-day-heading">
            <div><span><%= date.format(dayName) %></span><strong><%= date.format(shortDate) %></strong></div>
            <% if (isToday) { %><span class="sf-today-label">Today</span><% } %>
          </header>
          <div class="sf-week-day-items">
            <% if (items == null || items.isEmpty()) { %>
            <p class="sf-day-empty">A little room to focus.</p>
            <% } else { for (PlannerItem item : items) { %>
            <article class="sf-planner-item is-<%= Html.escape(item.getType()) %>
<%= item.isCompleted() ? "is-done" : "" %>">
              <div class="sf-planner-item-top">
                <span class="sf-planner-kind"><%= "task".equals(item.getType()) ? "DEADLINE" : "session".equals(item.getType()) ? "COMPLETED" : "exam".equals(item.getType()) ? "EXAM" : item.isCompleted() ? "DONE" : "PLANNED" %></span
                ><% if (item.getStartsAt() != null) { %><time datetime="<%= item.getStartsAt() %>"
                  ><%= item.getStartsAt().format(clock) %><% if (item.getEndsAt() != null) { %> – <%= item.getEndsAt().format(clock) %><% } %></time
                ><% } %>
              </div>
              <strong class="sf-planner-item-title"><%= Html.escape(item.getTitle()) %></strong>
              <small
                ><%= Html.escape(item.getCourseName()) %><% if ("task".equals(item.getType()) && item.getPriority() != null) { %> · <%= Html.escape(item.getPriority()) %>
                priority<% } %></small
              >
              <% if ("task".equals(item.getType())) { %><a
                class="sf-item-link"
                href="<%= request.getContextPath() %>/TaskController"
                >View task →</a
              ><% } %>
<% if (item.isUserEvent()) { %>
              <div class="sf-planner-item-actions">
                <% if ("study".equals(item.getType())) { %>
                <form method="post" action="<%= request.getContextPath() %>/PlannerController">
                  <input
                    type="hidden"
                    name="csrfToken"
                    value="<%= Html.csrfToken(request) %>"
                  /><input
                    type="hidden"
                    name="action"
                    value="setCompleted"
                  /><input
                    type="hidden"
                    name="eventId"
                    value="<%= item.getItemId() %>"
                  /><input
                    type="hidden"
                    name="completed"
                    value="<%= !item.isCompleted() %>"
                  /><input
                    type="hidden"
                    name="week"
                    value="<%= weekStart %>"
                  /><button
                    class="sf-plan-done-button <%= item.isCompleted() ? "is-done" : "" %>"
                    type="submit"
                  >
                    <%= item.isCompleted() ? "↶ Undo done" : "✓ Mark done" %>
                  </button>
                </form>
                <% } %>
                <form
                  method="post"
                  action="<%= request.getContextPath() %>/PlannerController"
                  onsubmit="return confirm('Remove this plan?');"
                >
                  <input
                    type="hidden"
                    name="csrfToken"
                    value="<%= Html.csrfToken(request) %>"
                  /><input type="hidden" name="action" value="delete" /><input
                    type="hidden"
                    name="eventId"
                    value="<%= item.getItemId() %>"
                  /><input
                    type="hidden"
                    name="week"
                    value="<%= weekStart %>"
                  /><button
                    class="sf-plan-remove-button"
                    type="submit"
                    aria-label="Remove plan: <%= Html.escape(item.getTitle()) %>"
                  >
                    Remove
                  </button>
                </form>
              </div>
              <% } %>
            </article>
            <% } } %>
          </div>
        </article>
        <% } %>
      </section>
      <p class="sf-planner-legend">
        <span class="is-study"></span>Planned study
        <span class="is-done"></span>Plan done <span class="is-exam"></span>Exam
        <span class="is-task"></span>Task deadline
        <span class="is-session"></span>Logged study
      </p>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
    <script>
      (() => {
        const type = document.getElementById("eventType");
        const endField = document.querySelector(".sf-planner-end-field");
        const endInput = document.getElementById("plannerEnd");
        const updateEndField = () => {
          const isStudy = type.value === "study";
          endField.hidden = !isStudy;
          endInput.required = isStudy;
        };
        type.addEventListener("change", updateEndField);
        updateEndField();
      })();
    </script>
  </body>
</html>
