<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.Map,aiwa.entity.Study,aiwa.entity.Category,aiwa.entity.Quiz,aiwa.ulti.Html" %>
<% List<Study> courses=(List<Study>)request.getAttribute("courses"); Map<Integer,List<Quiz>> quizzesByCourse=(Map<Integer,List<Quiz>>)request.getAttribute("quizzesByCourse"); List<Category> categories=(List<Category>)request.getAttribute("categories"); Long currentUserId=(Long)request.getAttribute("currentUserId"); Object flashMessage=request.getAttribute("flashMessage"); %>
<!DOCTYPE html>
<html lang="en"><head><title>My Courses | StudyFlow</title><%@ include file="/WEB-INF/views/components/head.jspf" %></head><body>
<jsp:include page="/WEB-INF/views/components/header.jsp" />
<main class="container-xl sf-page py-4 py-lg-5">
    <header class="sf-page-heading"><div><span class="sf-eyebrow">MAKE SPACE TO LEARN</span><h1 class="fw-bold mt-2">My Courses</h1><p class="text-body-secondary">Organize your subjects and connect your assignments.</p></div><a class="btn btn-primary rounded-pill px-4" href="#add-course">+ Add course</a></header>
    <% if(flashMessage!=null) { %><div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div><% } %>
    <% if(request.getAttribute("pageError")!=null) { %><div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div><% } %>
    <section class="card sf-panel mb-4" id="add-course"><div class="card-body p-4"><h2 class="h5 fw-bold mb-3">Add a course</h2>
        <form method="post" action="<%= request.getContextPath() %>/CoursesController" class="row g-3"><input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>"><input type="hidden" name="action" value="create">
            <div class="col-12 col-md-5"><label class="form-label" for="courseName">Course name</label><input class="form-control" id="courseName" name="courseName" maxlength="100" required placeholder="e.g., Java Programming"></div>
            <div class="col-12 col-sm-6 col-md-3"><label class="form-label" for="categoryId">Category</label><select class="form-select" id="categoryId" name="categoryId" required><% if(categories!=null) for(Category c:categories) { %><option value="<%= c.getCategoryId() %>"><%= Html.escape(c.getCategoryName()) %></option><% } %></select></div>
            <div class="col-12 col-sm-6 col-md-2"><label class="form-label" for="courseLevel">Level</label><select class="form-select" id="courseLevel" name="level"><option>Beginner</option><option>Intermediate</option><option>Advanced</option></select></div>
            <div class="col-12 col-md-2 d-flex align-items-end"><button class="btn btn-primary rounded-pill px-4" type="submit">Save course</button></div>
            <div class="col-12"><label class="form-label" for="courseDescription">Description (optional)</label><textarea class="form-control" id="courseDescription" name="description" maxlength="500" rows="2"></textarea></div>
        </form>
    </div></section>
    <section><div class="d-flex justify-content-between align-items-center mb-3"><div><h2 class="h5 fw-bold mb-1">Courses</h2><p class="small text-body-secondary mb-0">Shared catalog courses are read-only; your courses can be edited.</p></div><span class="badge text-bg-light rounded-pill"><%= courses==null?0:courses.size() %> courses</span></div>
    <% if(courses==null || courses.isEmpty()) { %><div class="sf-empty-state"><span aria-hidden="true">📚</span><p class="mb-1">No courses yet.</p><small class="text-body-secondary">Add your first course above to start organizing your study.</small></div><% } else { %><div class="row g-3">
        <% for(Study course:courses) { boolean editable=course.getUserId()!=null && course.getUserId().equals(currentUserId); List<Quiz> relatedQuizzes=quizzesByCourse==null?null:quizzesByCourse.get(course.getCourseId()); %>
        <div class="col-12 col-md-6 col-xl-4"><article class="card sf-course-card h-100"><div class="card-body p-4">
            <div class="d-flex justify-content-between gap-2"><div><span class="sf-course-category"><%= Html.escape(course.getCategoryName()) %><%= editable?"":" · Shared catalog" %></span><h3 class="h5 fw-bold mt-2"><%= Html.escape(course.getCourseName()) %></h3></div><span class="sf-course-icon" aria-hidden="true">📖</span></div>
            <% if(course.getDescription()!=null && !course.getDescription().isBlank()) { %><p class="small text-body-secondary"><%= Html.escape(course.getDescription()) %></p><% } %>
            <div class="d-flex justify-content-between small text-body-secondary mb-2"><span><%= course.getCompletedTaskCount() %> of <%= course.getTaskCount() %> tasks</span><span><%= course.getProgressPercent() %>%</span></div><div class="progress mb-3" role="progressbar" aria-label="<%= Html.escape(course.getCourseName()) %> completion" aria-valuenow="<%= course.getProgressPercent() %>" aria-valuemin="0" aria-valuemax="100"><div class="progress-bar" style="width:<%= course.getProgressPercent() %>%"></div></div>
            <div class="d-flex flex-wrap gap-2">
                <% if(relatedQuizzes!=null && !relatedQuizzes.isEmpty()) { %>
                <details class="sf-course-quizzes">
                    <summary class="btn btn-sm btn-primary rounded-pill"><%= relatedQuizzes.size() %> related quizzes</summary>
                    <div class="sf-course-quiz-list mt-3">
                        <p class="small text-body-secondary mb-2">Choose a quiz from <%= Html.escape(course.getCourseName()) %>:</p>
                        <ul class="list-group list-group-flush">
                            <% for(Quiz quiz:relatedQuizzes) { %>
                            <li class="list-group-item px-0">
                                <a class="d-flex justify-content-between align-items-start gap-2" href="<%= request.getContextPath() %>/QuizController?quizId=<%= quiz.getQuizId() %>&amp;courseId=<%= course.getCourseId() %>">
                                    <span><strong><%= Html.escape(quiz.getQuizTitle()) %></strong><br><small class="text-body-secondary"><%= Html.escape(quiz.getLessonTitle()) %> · <%= quiz.getQuestionCount() %> questions</small></span>
                                    <span aria-hidden="true">→</span>
                                </a>
                            </li>
                            <% } %>
                        </ul>
                    </div>
                </details>
                <a class="btn btn-sm btn-outline-primary rounded-pill" href="<%= request.getContextPath() %>/QuizHistoryController?courseId=<%= course.getCourseId() %>">Quiz history</a>
                <% } else { %>
                <button class="btn btn-sm btn-outline-secondary rounded-pill" type="button" disabled>No quiz yet</button>
                <% } %>
                <% if(editable) { %>
                <details class="sf-edit-details"><summary class="btn btn-sm btn-link">Edit</summary><form method="post" action="<%= request.getContextPath() %>/CoursesController" class="sf-edit-form"><input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>"><input type="hidden" name="action" value="update"><input type="hidden" name="courseId" value="<%= course.getCourseId() %>"><label class="form-label">Course name<input class="form-control form-control-sm" name="courseName" maxlength="100" required value="<%= Html.escape(course.getCourseName()) %>"></label><label class="form-label">Category<select class="form-select form-select-sm" name="categoryId"><% if(categories!=null) for(Category c:categories) { %><option value="<%= c.getCategoryId() %>" <%= c.getCategoryId()==course.getCategoryId()?"selected":"" %>><%= Html.escape(c.getCategoryName()) %></option><% } %></select></label><label class="form-label">Level<select class="form-select form-select-sm" name="level"><option <%= "Beginner".equals(course.getLevel())?"selected":"" %>>Beginner</option><option <%= "Intermediate".equals(course.getLevel())?"selected":"" %>>Intermediate</option><option <%= "Advanced".equals(course.getLevel())?"selected":"" %>>Advanced</option></select></label><label class="form-label">Description<textarea class="form-control form-control-sm" name="description" rows="2" maxlength="500"><%= Html.escape(course.getDescription()) %></textarea></label><button class="btn btn-sm btn-primary" type="submit">Save changes</button></form></details>
                <form method="post" action="<%= request.getContextPath() %>/CoursesController" onsubmit="return confirm('Delete this course?');"><input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>"><input type="hidden" name="action" value="delete"><input type="hidden" name="courseId" value="<%= course.getCourseId() %>"><button class="btn btn-sm btn-link text-danger" type="submit">Delete</button></form>
                <% } %>
            </div>
        </div></article></div><% } %>
    </div><% } %>
    </section>
</main>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body></html>
