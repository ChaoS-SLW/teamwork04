<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    javax.servlet.http.HttpSession userSession = request.getSession(false);
    boolean signedIn = userSession != null && userSession.getAttribute("userId") instanceof Number;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>StudyFlow | Home</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container-xl sf-page py-5">
        <section class="sf-home-hero">
            <div class="row align-items-center g-5">
                <div class="col-12 col-lg-7">
                    <span class="sf-eyebrow">YOUR STUDY SPACE ✨</span>
                    <h1 class="sf-home-title">Small steps.<br><span>Big progress.</span></h1>
                    <p class="sf-lead">Bring your courses, assignments, and study time together. Make a plan that feels manageable, then watch your progress grow.</p>
                    <div class="d-flex flex-wrap gap-3 mt-4">
                        <% if (signedIn) { %>
                        <a class="btn btn-primary rounded-pill px-4 py-2" href="<%= request.getContextPath() %>/DashboardController">Open my dashboard <span aria-hidden="true">→</span></a>
                        <a class="btn btn-outline-primary rounded-pill px-4 py-2" href="<%= request.getContextPath() %>/CoursesController">My courses</a>
                        <% } else { %>
                        <a class="btn btn-primary rounded-pill px-4 py-2" href="<%= request.getContextPath() %>/RegisterController">Create your account <span aria-hidden="true">→</span></a>
                        <a class="btn btn-outline-primary rounded-pill px-4 py-2" href="<%= request.getContextPath() %>/LoginController">Sign in</a>
                        <% } %>
                    </div>
                    <p class="small text-body-secondary mt-4 mb-0"><%= signedIn ? "Your study space is ready whenever you are." : "Create an account to keep your learning data with you." %></p>
                </div>
                <div class="col-12 col-lg-5">
                    <div class="sf-home-card card border-0 shadow-sm">
                        <div class="card-body p-4 p-md-5">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <div><span class="sf-eyebrow">A LITTLE EVERY DAY</span><h2 class="h4 fw-bold mt-2 mb-0">Your learning, in rhythm</h2></div>
                                <span class="sf-home-mark" aria-hidden="true">✳</span>
                            </div>
                            <div class="sf-home-step"><span>01</span><div><strong>Organize courses</strong><small>Keep each subject in one place</small></div><span aria-hidden="true">📚</span></div>
                            <div class="sf-home-step"><span>02</span><div><strong>Plan assignments</strong><small>Turn deadlines into small steps</small></div><span aria-hidden="true">✅</span></div>
                            <div class="sf-home-step"><span>03</span><div><strong>Track study time</strong><small>Notice the effort you put in</small></div><span aria-hidden="true">🌱</span></div>
                            <div class="sf-home-note mt-4">Progress is built one focused session at a time.</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section class="row g-3 mt-5" aria-label="StudyFlow features">
            <div class="col-12 col-md-4"><article class="sf-feature-card"><span aria-hidden="true">🗂️</span><h2 class="h5">Courses that feel clear</h2><p>Group work by subject and connect assignments to the class they belong to.</p></article></div>
            <div class="col-12 col-md-4"><article class="sf-feature-card"><span aria-hidden="true">📝</span><h2 class="h5">A plan you can act on</h2><p>See what is due, set priorities, and mark work complete as you go.</p></article></div>
            <div class="col-12 col-md-4"><article class="sf-feature-card"><span aria-hidden="true">📈</span><h2 class="h5">Progress you can see</h2><p>Log study sessions and review your activity over time.</p></article></div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body>
</html>
