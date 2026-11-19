<%@page import="aiwa.ulti.Ulti"%>
<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <title>StudyFlow</title>

    <%-- Shared Bootstrap and CSS --%>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>

</head>

<body>

    <%-- Shared Header --%>
    <jsp:include
        page="/WEB-INF/views/components/header.jsp" />


   
<main class="container-xl sf-dashboard py-4 py-lg-5">

    <!-- =========================
         WELCOME & QUICK ACTIONS
    ========================== -->
    <section class="sf-welcome mb-4">

        <div class="row align-items-center g-3">

            <div class="col-12 col-lg-7">
                <span class="sf-eyebrow">YOUR STUDY SPACE ✨</span>

                <h1 class="fw-bold mt-2 mb-2">
                    <%= Ulti.showGreeting() %>
                </h1>

                <p class="text-body-secondary mb-0">
                    Small steps. Big progress.
                    Let's make today count!
                </p>
            </div>

            <div class="col-12 col-lg-5">
                <div class="d-flex flex-wrap gap-2
                            justify-content-lg-end">

                    <a href="<%= request.getContextPath() %>/CoursesController"
                       class="btn btn-primary rounded-pill px-4">
                        + Add Course
                    </a>

                    <a href="<%= request.getContextPath() %>/TaskController"
                       class="btn btn-outline-primary rounded-pill px-4">
                        + New Task
                    </a>

                </div>
            </div>

        </div>

    </section>


    <!-- =========================
         STATISTICS CARDS
    ========================== -->
    <section class="mb-4" aria-label="Study overview">

        <div class="row g-3">

            <!-- COURSES -->
            <div class="col-12 col-sm-6 col-lg-4">
                <div class="card sf-stat-card h-100">
                    <div class="card-body p-4">

                        <div class="sf-stat-icon sf-purple">
                            📚
                        </div>

                        <p class="text-body-secondary mt-3 mb-1">
                            My Courses
                        </p>

                        <h2 class="display-6 fw-bold mb-1">
                            4
                        </h2>

                        <small class="text-body-secondary">
                            Active courses
                        </small>

                    </div>
                </div>
            </div>

            <!-- TASKS -->
            <div class="col-12 col-sm-6 col-lg-4">
                <div class="card sf-stat-card h-100">
                    <div class="card-body p-4">

                        <div class="sf-stat-icon sf-green">
                            ✅
                        </div>

                        <p class="text-body-secondary mt-3 mb-1">
                            Completed Tasks
                        </p>

                        <h2 class="display-6 fw-bold mb-1">
                            8<span class="fs-5 text-body-secondary">/12</span>
                        </h2>

                        <div class="progress mt-3"
                             role="progressbar"
                             aria-label="Task completion"
                             aria-valuenow="67"
                             aria-valuemin="0"
                             aria-valuemax="100"
                             style="height: 7px;">
                            <div class="progress-bar"
                                 style="width: 66.7%;"></div>
                        </div>

                    </div>
                </div>
            </div>

            <!-- STREAK -->
            <div class="col-12 col-sm-12 col-lg-4">
                <div class="card sf-stat-card h-100">
                    <div class="card-body p-4">

                        <div class="sf-stat-icon sf-orange">
                            🔥
                        </div>

                        <p class="text-body-secondary mt-3 mb-1">
                            Study Streak
                        </p>

                        <h2 class="display-6 fw-bold mb-1">
                            5 <span class="fs-5">Days</span>
                        </h2>

                        <small class="text-body-secondary">
                            Keep your momentum going!
                        </small>

                    </div>
                </div>
            </div>

        </div>

    </section>


    <!-- =========================
         CONTRIBUTION HEATMAP
    ========================== -->
    <section class="mb-4">

        <div class="card sf-panel">
            <div class="card-body p-4">

                <div class="d-flex align-items-start
                            justify-content-between gap-3 mb-4">

                    <div>
                        <h2 class="h5 fw-bold mb-1">
                            Your Study Activity 🌱
                        </h2>

                        <p class="small text-body-secondary mb-0">
                            Your learning journey, one day at a time.
                        </p>
                    </div>

                    <span class="badge text-bg-light rounded-pill">
                        Demo
                    </span>

                </div>

                <!-- Generated by dashboard.js -->
                <div class="sf-heatmap-scroll">
                    <div id="sfHeatmap"
                         class="sf-heatmap"
                         aria-label="Sample daily study activity">
                    </div>
                </div>

                <div class="d-flex align-items-center
                            justify-content-end gap-2 mt-3">

                    <small class="text-body-secondary">Less</small>

                    <span class="sf-heat-cell" data-level="0"></span>
                    <span class="sf-heat-cell" data-level="1"></span>
                    <span class="sf-heat-cell" data-level="2"></span>
                    <span class="sf-heat-cell" data-level="3"></span>
                    <span class="sf-heat-cell" data-level="4"></span>

                    <small class="text-body-secondary">More</small>

                </div>

            </div>
        </div>

    </section>


    <!-- =========================
         CHART & UPCOMING TASKS
    ========================== -->
    <section class="row g-4">

        <!-- STUDY PROGRESS -->
        <div class="col-12 col-lg-7">

            <div class="card sf-panel h-100">
                <div class="card-body p-4">

                    <div class="mb-4">
                        <h2 class="h5 fw-bold mb-1">
                            Study Progress 📈
                        </h2>

                        <p class="small text-body-secondary mb-0">
                            Your study time over the last 7 days
                        </p>
                    </div>

                    <!-- Sample Bar Chart -->
                    <div class="sf-weekly-chart">

                        <div class="sf-bar-item">
                            <div class="sf-bar" style="height: 45%;"
                                 title="Monday: 45 minutes"></div>
                            <span>Mon</span>
                        </div>

                        <div class="sf-bar-item">
                            <div class="sf-bar" style="height: 65%;"
                                 title="Tuesday: 65 minutes"></div>
                            <span>Tue</span>
                        </div>

                        <div class="sf-bar-item">
                            <div class="sf-bar" style="height: 35%;"
                                 title="Wednesday: 35 minutes"></div>
                            <span>Wed</span>
                        </div>

                        <div class="sf-bar-item">
                            <div class="sf-bar" style="height: 85%;"
                                 title="Thursday: 85 minutes"></div>
                            <span>Thu</span>
                        </div>

                        <div class="sf-bar-item">
                            <div class="sf-bar" style="height: 55%;"
                                 title="Friday: 55 minutes"></div>
                            <span>Fri</span>
                        </div>

                        <div class="sf-bar-item">
                            <div class="sf-bar" style="height: 100%;"
                                 title="Saturday: 100 minutes"></div>
                            <span>Sat</span>
                        </div>

                        <div class="sf-bar-item">
                            <div class="sf-bar" style="height: 70%;"
                                 title="Sunday: 70 minutes"></div>
                            <span>Sun</span>
                        </div>

                    </div>

                    <p class="small text-body-secondary mt-3 mb-0">
                        Demo data · Study duration in minutes
                    </p>

                </div>
            </div>

        </div>


        <!-- UPCOMING TASKS -->
        <div class="col-12 col-lg-5">

            <div class="card sf-panel h-100">
                <div class="card-body p-4">

                    <div class="d-flex justify-content-between
                                align-items-center mb-4">

                        <h2 class="h5 fw-bold mb-0">
                            Upcoming Tasks 📝
                        </h2>

                        <a href="<%= request.getContextPath() %>/tasks"
                           class="small text-decoration-none">
                            View All
                        </a>

                    </div>

                    <!-- Task 1 -->
                    <div class="sf-task-row">

                        <div>
                            <h3 class="fs-6 fw-semibold mb-1">
                                Java Homework
                            </h3>
                            <small class="text-body-secondary">
                                Java Programming
                            </small>
                        </div>

                        <span class="badge rounded-pill text-bg-danger">
                            High
                        </span>

                    </div>

                    <!-- Task 2 -->
                    <div class="sf-task-row">

                        <div>
                            <h3 class="fs-6 fw-semibold mb-1">
                                SQLite Assignment
                            </h3>
                            <small class="text-body-secondary">
                                Database
                            </small>
                        </div>

                        <span class="badge rounded-pill text-bg-warning">
                            Medium
                        </span>

                    </div>

                    <!-- Task 3 -->
                    <div class="sf-task-row">

                        <div>
                            <h3 class="fs-6 fw-semibold mb-1">
                                Responsive Website
                            </h3>
                            <small class="text-body-secondary">
                                HTML & CSS
                            </small>
                        </div>

                        <span class="badge rounded-pill text-bg-success">
                            Low
                        </span>

                    </div>

                </div>
            </div>

        </div>

    </section>

</main>

   



    <%-- Shared Footer --%>
    <jsp:include
        page="/WEB-INF/views/components/footer.jsp" />


    <%-- Application JavaScript --%>
    <script
        type="module"
        src="<%= request.getContextPath() %>/assets/js/app.js">
    </script>

</body>
</html>