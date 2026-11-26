<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.Map,aiwa.entity.Study,aiwa.entity.Category,aiwa.entity.Quiz,aiwa.entity.Lesson,aiwa.ulti.Html" %>
<%
    List<Study> courses=(List<Study>)request.getAttribute("courses");
    Map<Integer,List<Quiz>> quizzesByCourse=(Map<Integer,List<Quiz>>)request.getAttribute("quizzesByCourse");
    List<Category> categories=(List<Category>)request.getAttribute("categories");
    Map<Integer,Integer> courseCounts=(Map<Integer,Integer>)request.getAttribute("courseCounts");
    List<Lesson> lessons=(List<Lesson>)request.getAttribute("lessons");
    Category selectedCategory=(Category)request.getAttribute("selectedCategory");
    Study selectedCourse=(Study)request.getAttribute("selectedCourse");
    Long currentUserId=(Long)request.getAttribute("currentUserId");
    Object flashMessage=request.getAttribute("flashMessage");

    String viewMode=(String)request.getAttribute("viewMode");
    if(viewMode==null) viewMode="categories";

    // page ရဲ့ ခေါင်းစဉ်ကို page အမျိုးအစားအလိုက် ပြောင်းမယ်
    String pageTitle="My Courses";
    String pageSubtitle="Choose a category to find your courses.";
    if("courses".equals(viewMode) && selectedCategory!=null) { pageTitle=selectedCategory.getCategoryName(); pageSubtitle="Choose a course to start learning."; }
    if("lessons".equals(viewMode) && selectedCourse!=null) { pageTitle=selectedCourse.getCourseName(); pageSubtitle=selectedCourse.getDescription()==null?"":selectedCourse.getDescription(); }
%>
<!DOCTYPE html>
<html lang="en"><head><title>My Courses | StudyFlow</title><%@ include file="/WEB-INF/views/components/head.jspf" %></head><body>
<jsp:include page="/WEB-INF/views/components/header.jsp" />
<main class="container-xl sf-page py-4 py-lg-5">

    <%-- ===== ခေါင်းစဉ် ===== --%>
    <header class="sf-page-heading"><div><span class="sf-eyebrow">MAKE SPACE TO LEARN</span><h1 class="fw-bold mt-2"><%= Html.escape(pageTitle) %></h1><p class="text-body-secondary"><%= Html.escape(pageSubtitle) %></p></div>
    <% if(!"lessons".equals(viewMode)) { %><a class="btn btn-primary rounded-pill px-4" href="#add-course" onclick="document.getElementById('add-course').open=true">+ Add course</a><% } %></header>

    <%-- ===== ဘယ်ကိုရောက်နေလဲ (breadcrumb) ===== --%>
    <% if(!"categories".equals(viewMode)) { %>
    <nav class="sf-breadcrumb" aria-label="Breadcrumb">
        <a href="<%= request.getContextPath() %>/CoursesController">My Courses</a>
        <% if(selectedCategory!=null) { %><span aria-hidden="true">›</span>
            <% if("lessons".equals(viewMode)) { %><a href="<%= request.getContextPath() %>/CoursesController?categoryId=<%= selectedCategory.getCategoryId() %>"><%= Html.escape(selectedCategory.getCategoryName()) %></a>
            <% } else { %><span><%= Html.escape(selectedCategory.getCategoryName()) %></span><% } %>
        <% } %>
        <% if("lessons".equals(viewMode) && selectedCourse!=null) { %><span aria-hidden="true">›</span><span><%= Html.escape(selectedCourse.getCourseName()) %></span><% } %>
    </nav>
    <% } %>

    <% if(flashMessage!=null) { %><div class="alert alert-<%= Html.escape(request.getAttribute("flashKind")) %>" role="status"><%= Html.escape(flashMessage) %></div><% } %>
    <% if(request.getAttribute("pageError")!=null) { %><div class="alert alert-warning" role="alert"><%= Html.escape(request.getAttribute("pageError")) %></div><% } %>

    <%-- ===== Add a course (ပထမ page နဲ့ category page မှာပဲ ပြမယ်) ===== --%>
    <% if(!"lessons".equals(viewMode)) { %>
    <details class="card sf-panel mb-4" id="add-course"><summary class="sf-add-summary">+ Add a personal course</summary><div class="card-body p-4">
        <form method="post" action="<%= request.getContextPath() %>/CoursesController" class="row g-3"><input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>"><input type="hidden" name="action" value="create">
            <div class="col-12 col-md-5"><label class="form-label" for="courseName">Course name</label><input class="form-control" id="courseName" name="courseName" maxlength="100" required placeholder="e.g., Java Programming"></div>
            <div class="col-12 col-sm-6 col-md-3"><label class="form-label" for="categoryId">Category</label><select class="form-select" id="categoryId" name="categoryId" required><% if(categories!=null) for(Category c:categories) { %><option value="<%= c.getCategoryId() %>" <%= (selectedCategory!=null && selectedCategory.getCategoryId()==c.getCategoryId())?"selected":"" %>><%= Html.escape(c.getCategoryName()) %></option><% } %></select></div>
            <div class="col-12 col-sm-6 col-md-2"><label class="form-label" for="courseLevel">Level</label><select class="form-select" id="courseLevel" name="level"><option>Beginner</option><option>Intermediate</option><option>Advanced</option></select></div>
            <div class="col-12 col-md-2 d-flex align-items-end"><button class="btn btn-primary rounded-pill px-4" type="submit">Save course</button></div>
            <div class="col-12"><label class="form-label" for="courseDescription">Description (optional)</label><textarea class="form-control" id="courseDescription" name="description" maxlength="500" rows="2"></textarea></div>
        </form>
    </div></details>
    <% } %>

    <%-- ===== PAGE 1 : category ကတ်များ ===== --%>
    <% if("categories".equals(viewMode)) { %>
    <section><div class="d-flex justify-content-between align-items-center mb-3"><div><h2 class="h5 fw-bold mb-1">Categories</h2><p class="small text-body-secondary mb-0">Open a category to see its courses.</p></div><span class="badge text-bg-light rounded-pill"><%= categories==null?0:categories.size() %> categories</span></div>
    <% if(categories==null || categories.isEmpty()) { %><div class="sf-empty-state"><span aria-hidden="true">📚</span><p class="mb-1">No categories yet.</p></div><% } else { %><div class="row g-3">
        <% for(Category c:categories) { Integer n=courseCounts==null?null:courseCounts.get(c.getCategoryId()); int courseCount=n==null?0:n; %>
        <div class="col-12 col-sm-6 col-lg-4">
            <a class="sf-category-link" href="<%= request.getContextPath() %>/CoursesController?categoryId=<%= c.getCategoryId() %>">
                <article class="card sf-course-card h-100"><div class="card-body p-4">
                    <div class="d-flex justify-content-between gap-2"><div><h3 class="h5 fw-bold mb-1"><%= Html.escape(c.getCategoryName()) %></h3><p class="small text-body-secondary mb-0"><%= courseCount %> courses</p></div><span class="sf-course-icon" aria-hidden="true">📖</span></div>
                </div></article>
            </a>
        </div>
        <% } %>
    </div><% } %>
    </section>
    <% } %>

    <%-- ===== PAGE 2 : category တစ်ခုထဲက course ကတ်များ ===== --%>
    <% if("courses".equals(viewMode)) { %>
    <section><div class="d-flex justify-content-between align-items-center mb-3"><div><h2 class="h5 fw-bold mb-1">Courses</h2><p class="small text-body-secondary mb-0">Shared catalog courses are read-only; your courses can be edited.</p></div><span class="badge text-bg-light rounded-pill"><%= courses==null?0:courses.size() %> courses</span></div>
    <% if(courses==null || courses.isEmpty()) { %><div class="sf-empty-state"><span aria-hidden="true">📚</span><p class="mb-1">No courses in this category yet.</p><small class="text-body-secondary">Add a course above to start.</small></div><% } else { %><div class="row g-3">
        <% for(Study course:courses) { boolean editable=course.getUserId()!=null && course.getUserId().equals(currentUserId); List<Quiz> relatedQuizzes=quizzesByCourse==null?null:quizzesByCourse.get(course.getCourseId()); %>
        <div class="col-12 col-md-6 col-xl-4"><article class="card sf-course-card h-100"><div class="card-body p-4">
            <div class="d-flex justify-content-between gap-2"><div><span class="sf-course-category"><%= Html.escape(course.getCategoryName()) %><%= editable?"":" · Shared catalog" %></span><h3 class="h5 fw-bold mt-2"><a class="sf-course-title-link" href="<%= request.getContextPath() %>/CoursesController?courseId=<%= course.getCourseId() %>"><%= Html.escape(course.getCourseName()) %></a></h3></div><span class="sf-course-icon" aria-hidden="true">📖</span></div>
            <% if(course.getDescription()!=null && !course.getDescription().isBlank()) { %><p class="small text-body-secondary"><%= Html.escape(course.getDescription()) %></p><% } %>
            <div class="d-flex justify-content-between small text-body-secondary mb-2"><span><%= course.getCompletedTaskCount() %> of <%= course.getTaskCount() %> tasks</span><span><%= course.getProgressPercent() %>%</span></div><div class="progress mb-3" role="progressbar" aria-label="<%= Html.escape(course.getCourseName()) %> completion" aria-valuenow="<%= course.getProgressPercent() %>" aria-valuemin="0" aria-valuemax="100"><div class="progress-bar" style="width:<%= course.getProgressPercent() %>%"></div></div>
            <div class="d-flex flex-wrap gap-2">
                <a class="btn btn-sm btn-primary rounded-pill" href="<%= request.getContextPath() %>/CoursesController?courseId=<%= course.getCourseId() %>">Open lessons</a>
                <% if(relatedQuizzes!=null && !relatedQuizzes.isEmpty()) { %>
                <details class="sf-course-quizzes">
                    <summary class="btn btn-sm btn-outline-primary rounded-pill"><%= relatedQuizzes.size() %> related quizzes</summary>
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
    <% } %>

    <%-- ===== PAGE 3 : course တစ်ခုထဲက chapter -> lesson ===== --%>
    <% if("lessons".equals(viewMode)) { %>
    <section>
    <% if(lessons==null || lessons.isEmpty()) { %><div class="sf-empty-state"><span aria-hidden="true">📚</span><p class="mb-1">No lessons in this course yet.</p></div><% } else {
        String lastChapter=null;
        for(Lesson lesson:lessons) {
            if(!lesson.getChapterName().equals(lastChapter)) { lastChapter=lesson.getChapterName(); %>
            <h2 class="h5 fw-bold mt-4 mb-3"><%= Html.escape(lesson.getChapterName()) %></h2>
            <% } %>
        <article class="card sf-lesson-card mb-3"><div class="card-body p-4">
            <span class="sf-course-category">Lesson <%= lesson.getLessonOrder() %></span>
            <h3 class="h5 fw-bold mt-1"><%= Html.escape(lesson.getLessonTitle()) %></h3>
            <p class="mb-3"><%= Html.escape(lesson.getContent()) %></p>
            <% if(lesson.getExampleCode()!=null && !lesson.getExampleCode().isBlank()) { %><pre class="sf-code"><code><%= Html.escape(lesson.getExampleCode()) %></code></pre><% } %>
            <% if(lesson.getQuizId()>0 && lesson.getQuestionCount()>0) { %><a class="btn btn-sm btn-primary rounded-pill" href="<%= request.getContextPath() %>/QuizController?quizId=<%= lesson.getQuizId() %>&amp;courseId=<%= selectedCourse.getCourseId() %>">Take quiz · <%= lesson.getQuestionCount() %> questions</a><% } %>
        </div></article>
    <%  } } %>
    </section>
    <% } %>

</main>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body></html>

