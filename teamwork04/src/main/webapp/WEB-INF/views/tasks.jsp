<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDateTime,java.util.List,aiwa.entity.Study,aiwa.entity.Task,aiwa.entity.TaskNote,aiwa.ulti.Html" %>
<%
    List<Study> courses = (List<Study>) request.getAttribute("courses");
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
    List<Task> allUserTasks = (List<Task>) request.getAttribute("allUserTasks");
    List<TaskNote> taskNotes = (List<TaskNote>) request.getAttribute("taskNotes");
    Object flashMessage = request.getAttribute("flashMessage");
%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>My Tasks | StudyFlow</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
  </head>
  <body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container sf-page sf-tasks-page py-5">
      <header class="sf-page-heading">
        <div>
          <span class="sf-eyebrow">ONE STEP AT A TIME</span>
          <h1 class="fw-bold mt-2">My Tasks</h1>
          <p class="text-body-secondary">
            Keep assignments clear and deadlines in reach.
          </p>
        </div>
        <a class="btn btn-primary rounded-pill px-4" href="#new-task"
          >+ New task</a
        >
      </header>
      <%@ include file="/WEB-INF/views/components/plantabs.jspf" %>
      <% if (flashMessage != null) { %>
      <div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div>
      <% } %>
<% if (request.getAttribute("pageError") != null) { %>
      <div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div>
      <% } %>
      <section class="card sf-panel mb-4" id="new-task">
        <div class="card-body p-4">
          <h2 class="h5 fw-bold mb-3">Add an assignment</h2>
          <form
            method="post"
            action="<%= request.getContextPath() %>/TaskController"
            class="row g-3"
          >
            <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" />
            <input type="hidden" name="action" value="create" />
            <div class="col-12 col-md-6">
              <label class="form-label" for="taskTitle">Task title</label
              ><input
                class="form-control"
                id="taskTitle"
                name="title"
                maxlength="120"
                required
                placeholder="e.g., Finish Java homework"
              />
            </div>
            <div class="col-12 col-md-6">
              <label class="form-label" for="taskCourse">Course</label
              ><select class="form-select" id="taskCourse" name="courseId">
                <option value="">No course</option>
                <% if(courses!=null) for(Study c:courses) { %>
                <option value="<%= c.getCourseId() %>"><%= Html.escape(c.getCourseName()) %></option>
                <% } %>
              </select>
            </div>
            <div class="col-12">
              <label class="form-label" for="taskDescription"
                >Description
                <span class="text-body-secondary">(optional)</span></label
              ><textarea
                class="form-control"
                id="taskDescription"
                name="description"
                rows="2"
                maxlength="1000"
                placeholder="Add a helpful note"
              ></textarea>
            </div>
            <div class="col-12 col-sm-6 col-lg-4">
              <label class="form-label" for="taskDue"
                >Due date and time
                <span class="text-body-secondary">(optional)</span></label
              ><input
                class="form-control"
                id="taskDue"
                type="datetime-local"
                name="dueAt"
              />
            </div>
            <div class="col-6 col-sm-3 col-lg-2">
              <label class="form-label" for="taskPriority">Priority</label
              ><select class="form-select" id="taskPriority" name="priority">
                <option value="low">Low</option>
                <option value="medium" selected>Medium</option>
                <option value="high">High</option>
              </select>
            </div>
            <div class="col-6 col-sm-3 col-lg-2">
              <label class="form-label" for="taskStatus">Status</label
              ><select class="form-select" id="taskStatus" name="status">
                <option value="todo" selected>To do</option>
                <option value="in_progress">In progress</option>
                <option value="done">Done</option>
              </select>
            </div>
            <div class="col-12 col-lg-4 d-flex align-items-end">
              <button class="btn btn-primary rounded-pill px-4" type="submit">
                Save task
              </button>
            </div>
          </form>
        </div>
      </section>
      <section class="card sf-panel sf-task-notes mb-4" id="task-notes">
        <div class="card-body p-4">
          <div
            class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-3"
          >
            <div>
              <span class="sf-eyebrow">KEEP YOUR THOUGHTS</span>
              <h2 class="h5 fw-bold mt-1 mb-1">Quick Notes ✍️</h2>
              <p class="small text-body-secondary mb-0">
                Save instructions, ideas, or reminders alongside your tasks.
              </p>
            </div>
            <span class="badge rounded-pill sf-note-count"
              ><%= taskNotes == null ? 0 : taskNotes.size() %> notes</span
            >
          </div>
          <form
            method="post"
            action="<%= request.getContextPath() %>/TaskNoteController"
            class="sf-note-compose mb-4"
          >
            <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" /><input
              type="hidden"
              name="action"
              value="create"
            />
            <div class="row g-3">
              <div class="col-12 col-md-5">
                <label class="form-label" for="noteTitle">Note title</label
                ><input
                  class="form-control"
                  id="noteTitle"
                  name="title"
                  maxlength="100"
                  required
                  placeholder="e.g., Questions for my teacher"
                />
              </div>
              <div class="col-12 col-md-4">
                <label class="form-label" for="noteTask"
                  >Related task
                  <span class="text-body-secondary">(optional)</span></label
                ><select class="form-select" id="noteTask" name="taskId">
                  <option value="">No linked task</option>
                  <% if (allUserTasks != null) for (Task task : allUserTasks) { %>
                  <option value="<%= task.getTaskId() %>"><%= Html.escape(task.getTitle()) %></option>
                  <% } %>
                </select>
              </div>
              <div class="col-12">
                <label class="form-label" for="noteText">Your note</label
                ><textarea
                  class="form-control"
                  id="noteText"
                  name="noteText"
                  rows="3"
                  maxlength="4000"
                  required
                  placeholder="Write a note you want to keep..."
                ></textarea>
              </div>
              <div class="col-12">
                <button class="btn btn-primary rounded-pill px-4" type="submit">
                  Save note
                </button>
              </div>
            </div>
          </form>
          <% if (taskNotes == null || taskNotes.isEmpty()) { %>
          <div class="sf-notes-empty">
            <span aria-hidden="true">🗒️</span>
            <p class="mb-1">Your notes will appear here.</p>
            <small class="text-body-secondary"
              >Add a note above, then edit or remove it whenever you
              need.</small
            >
          </div>
          <% } else { %>
          <div class="sf-note-grid">
            <% for (TaskNote note : taskNotes) { %>
            <article class="sf-note-card">
              <header class="sf-note-card-heading">
                <div>
                  <h3><%= Html.escape(note.getTitle()) %></h3>
                  <small
                    ><%= note.getTaskTitle() == null ? "General note" : "For " + Html.escape(note.getTaskTitle()) %><% if (note.getUpdatedAt() != null) { %> · updated
                    <%= Html.escape(Html.dateTime(note.getUpdatedAt())) %><% } %></small
                  >
                </div>
                <span aria-hidden="true">✎</span>
              </header>
              <p class="sf-note-body"><%= Html.escape(note.getNoteText()) %></p>
              <div class="sf-note-actions">
                <details class="sf-edit-details">
                  <summary>Edit note</summary>
                  <form
                    method="post"
                    action="<%= request.getContextPath() %>/TaskNoteController"
                    class="sf-edit-form sf-note-edit-form"
                  >
                    <input
                      type="hidden"
                      name="csrfToken"
                      value="<%= Html.csrfToken(request) %>"
                    /><input type="hidden" name="action" value="update" /><input
                      type="hidden"
                      name="noteId"
                      value="<%= note.getNoteId() %>"
                    /><label class="form-label"
                      >Title<input
                        class="form-control form-control-sm"
                        name="title"
                        maxlength="100"
                        required
                        value="<%= Html.escape(note.getTitle()) %>" /></label
                    ><label class="form-label"
                      >Related task<select
                        class="form-select form-select-sm"
                        name="taskId"
                      >
                        <option value="">No linked task</option>
                        <% if (allUserTasks != null) for (Task task : allUserTasks) { %>
                        <option value="<%= task.getTaskId() %>" <%= note.getTaskId() != null && note.getTaskId() == task.getTaskId() ? "selected" : "" %>>
                          <%= Html.escape(task.getTitle()) %>
                        </option>
                        <% } %>
                      </select></label
                    ><label class="form-label"
                      >Your note<textarea
                        class="form-control form-control-sm"
                        name="noteText"
                        rows="5"
                        maxlength="4000"
                        required
                      >
<%= Html.escape(note.getNoteText()) %></textarea
                      ></label
                    ><button class="btn btn-sm btn-primary" type="submit">
                      Save changes
                    </button>
                  </form>
                </details>
                <form
                  method="post"
                  action="<%= request.getContextPath() %>/TaskNoteController"
                  onsubmit="return confirm('Delete this note?');"
                >
                  <input
                    type="hidden"
                    name="csrfToken"
                    value="<%= Html.csrfToken(request) %>"
                  /><input type="hidden" name="action" value="delete" /><input
                    type="hidden"
                    name="noteId"
                    value="<%= note.getNoteId() %>"
                  /><button
                    class="btn btn-sm btn-link text-danger"
                    type="submit"
                  >
                    Delete
                  </button>
                </form>
              </div>
            </article>
            <% } %>
          </div>
          <% } %>
        </div>
      </section>
      <section class="card sf-panel">
        <div class="card-body p-4">
          <div
            class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3"
          >
            <div>
              <h2 class="h5 fw-bold mb-1">Assignment list</h2>
              <p class="small text-body-secondary mb-0">
                Your tasks are saved to your account.
              </p>
            </div>
            <form
              class="sf-filters"
              method="get"
              action="<%= request.getContextPath() %>/TaskController"
              aria-label="Filter tasks"
            >
              <select
                class="form-select form-select-sm"
                name="status"
                aria-label="Filter by status"
              >
                <option value="">All statuses</option>
                <option value="todo" <%= "todo".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>To do</option>
                <option value="in_progress" <%= "in_progress".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>In progress</option>
                <option value="done" <%= "done".equals(request.getAttribute("filterStatus")) ? "selected" : "" %>>Completed</option>
              </select>
              <select
                class="form-select form-select-sm"
                name="priority"
                aria-label="Filter by priority"
              >
                <option value="">All priorities</option>
                <option value="high" <%= "high".equals(request.getAttribute("filterPriority")) ? "selected" : "" %>>High</option>
                <option value="medium" <%= "medium".equals(request.getAttribute("filterPriority")) ? "selected" : "" %>>Medium</option>
                <option value="low" <%= "low".equals(request.getAttribute("filterPriority")) ? "selected" : "" %>>Low</option>
              </select>
              <select
                class="form-select form-select-sm"
                name="courseId"
                aria-label="Filter by course"
              >
                <option value="0">All courses</option>
                <% if(courses!=null) for(Study c:courses) { int selected=request.getAttribute("filterCourseId") instanceof Integer ? (Integer)request.getAttribute("filterCourseId") : 0; %>
                <option value="<%= c.getCourseId() %>" <%= selected==c.getCourseId()?"selected":"" %>><%= Html.escape(c.getCourseName()) %></option>
                <% } %>
              </select>
              <button class="btn btn-sm btn-outline-primary" type="submit">
                Filter
              </button>
            </form>
          </div>
          <% if (tasks == null || tasks.isEmpty()) { %>
          <div class="sf-empty-state">
            <span aria-hidden="true">📝</span>
            <p class="mb-1">No tasks match this view.</p>
            <small class="text-body-secondary"
              >Add an assignment above or adjust the filters.</small
            >
          </div>
          <% } else { %>
          <div class="sf-task-list">
            <% for (Task task : tasks) { boolean done="done".equals(task.getStatus()); boolean overdue=!done && task.getDueAt()!=null && task.getDueAt().isBefore(LocalDateTime.now()); %>
            <article class="sf-task-card <%= done?"is-done":"" %>">
              <div class="sf-task-main">
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
                  /><input
                    type="hidden"
                    name="completed"
                    value="<%= !done %>"
                  /><button
                    class="sf-check-button"
                    type="submit"
                    aria-label="Mark <%= Html.escape(task.getTitle()) %>
<%= done?"incomplete":"complete" %>"
                  >
                    <%= done?"✓":"" %>
                  </button>
                </form>
                <div class="sf-task-copy">
                  <h3 class="h6 fw-bold mb-1"><%= Html.escape(task.getTitle()) %></h3>
                  <p class="small text-body-secondary mb-1">
                    <%= Html.escape(task.getCourseName()==null?"No course":task.getCourseName()) %><% if(task.getDueAt()!=null) { %> ·
                    <span class="<%= overdue?"text-danger fw-semibold":"" %>"><%= overdue?"Overdue · ":"Due " %><%= Html.escape(Html.dateTime(task.getDueAt())) %></span
                    ><% } %>
                  </p>
                  <% if(task.getDescription()!=null && !task.getDescription().isBlank()) { %>
                  <p class="small mb-0"><%= Html.escape(task.getDescription()) %></p>
                  <% } %>
                </div>
                <span class="badge rounded-pill sf-priority-<%= Html.escape(task.getPriority()) %>"
                  ><%= Html.escape(task.getPriority()) %></span
                >
              </div>
              <div class="sf-task-actions">
                <details class="sf-edit-details">
                  <summary>Edit</summary>
                  <form
                    method="post"
                    action="<%= request.getContextPath() %>/TaskController"
                    class="sf-edit-form"
                  >
                    <input
                      type="hidden"
                      name="csrfToken"
                      value="<%= Html.csrfToken(request) %>"
                    /><input type="hidden" name="action" value="update" /><input
                      type="hidden"
                      name="taskId"
                      value="<%= task.getTaskId() %>"
                    /><label class="form-label"
                      >Title<input
                        class="form-control form-control-sm"
                        name="title"
                        maxlength="120"
                        required
                        value="<%= Html.escape(task.getTitle()) %>" /></label
                    ><label class="form-label"
                      >Course<select
                        class="form-select form-select-sm"
                        name="courseId"
                      >
                        <option value="">No course</option>
                        <% if(courses!=null) for(Study c:courses) { %>
                        <option value="<%= c.getCourseId() %>" <%= task.getCourseId()!=null && task.getCourseId()==c.getCourseId()?"selected":"" %>>
                          <%= Html.escape(c.getCourseName()) %>
                        </option>
                        <% } %>
                      </select></label
                    ><label class="form-label"
                      >Description<textarea
                        class="form-control form-control-sm"
                        name="description"
                        rows="2"
                        maxlength="1000"
                      >
<%= Html.escape(task.getDescription()) %></textarea
                      ></label
                    ><label class="form-label"
                      >Due date<input
                        class="form-control form-control-sm"
                        type="datetime-local"
                        name="dueAt"
                        value="<%= task.getDueAt()==null?"":task.getDueAt().toString().substring(0,16) %>" /></label
                    ><label class="form-label"
                      >Priority<select
                        class="form-select form-select-sm"
                        name="priority"
                      >
                        <option value="low" <%= "low".equals(task.getPriority())?"selected":"" %>>Low</option>
                        <option value="medium" <%= "medium".equals(task.getPriority())?"selected":"" %>>Medium</option>
                        <option value="high" <%= "high".equals(task.getPriority())?"selected":"" %>>High</option>
                      </select></label
                    ><label class="form-label"
                      >Status<select
                        class="form-select form-select-sm"
                        name="status"
                      >
                        <option value="todo" <%= "todo".equals(task.getStatus())?"selected":"" %>>To do</option>
                        <option value="in_progress" <%= "in_progress".equals(task.getStatus())?"selected":"" %>>
                          In progress
                        </option>
                        <option value="done" <%= "done".equals(task.getStatus())?"selected":"" %>>Done</option>
                      </select></label
                    ><button class="btn btn-sm btn-primary" type="submit">
                      Save changes
                    </button>
                  </form>
                </details>
                <form
                  method="post"
                  action="<%= request.getContextPath() %>/TaskController"
                  class="sf-inline-form"
                  onsubmit="return confirm('Delete this task?');"
                >
                  <input
                    type="hidden"
                    name="csrfToken"
                    value="<%= Html.csrfToken(request) %>"
                  /><input type="hidden" name="action" value="delete" /><input
                    type="hidden"
                    name="taskId"
                    value="<%= task.getTaskId() %>"
                  /><button
                    class="btn btn-sm btn-link text-danger"
                    type="submit"
                  >
                    Delete
                  </button>
                </form>
              </div>
            </article>
            <% } %>
          </div>
          <% } %>
        </div>
      </section>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
  </body>
</html>
