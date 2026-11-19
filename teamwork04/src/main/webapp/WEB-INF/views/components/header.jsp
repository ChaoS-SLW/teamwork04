<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    final String contextPath = request.getContextPath();
    Object forwardedUri = request.getAttribute("javax.servlet.forward.request_uri");
    String requestUri = forwardedUri instanceof String ? (String) forwardedUri : request.getRequestURI();
    String currentPath = requestUri.startsWith(contextPath) ? requestUri.substring(contextPath.length()) : requestUri;
    boolean isHome = currentPath.equals("/") || currentPath.equals("/index.jsp") || currentPath.equals("/StudyController");
    boolean isDashboard = currentPath.equals("/DashboardController");
    boolean isCourses = currentPath.equals("/CoursesController");
    boolean isTasks = currentPath.equals("/TaskController");
    boolean isAnalytics = currentPath.equals("/ActivityController");
%>
<header class="sf-header">
    <nav class="navbar lg:navbar-expand sf-navbar" aria-label="Main navigation">
        <div class="container">
            <a class="navbar-brand sf-brand" href="<%= contextPath %>/StudyController"><span class="sf-brand-icon" aria-hidden="true">✳</span><span class="sf-brand-text">studyflow<span class="sf-brand-dot">.</span></span></a>
            <span class="sf-demo-badge" title="Changes are visible to everyone using this demo">Shared demo</span>
            <button class="btn-icon navbar-toggler" type="button" data-bs-toggle="drawer" data-bs-target="#studyflowNav" aria-controls="studyflowNav" aria-expanded="false" aria-label="Open navigation menu"><span class="navbar-toggler-icon" aria-hidden="true"></span></button>
            <dialog class="drawer drawer-end" tabindex="-1" id="studyflowNav" aria-labelledby="studyflowNavLabel">
                <div class="drawer-header"><h2 class="drawer-title h5" id="studyflowNavLabel">Navigation</h2><button type="button" class="btn-close" data-bs-dismiss="drawer" aria-label="Close navigation"></button></div>
                <div class="drawer-body"><ul class="navbar-nav sf-nav-list">
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isHome?"active":"" %>" href="<%= contextPath %>/StudyController" <%= isHome?"aria-current=\"page\"":"" %>>Home</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isDashboard?"active":"" %>" href="<%= contextPath %>/DashboardController" <%= isDashboard?"aria-current=\"page\"":"" %>>Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isCourses?"active":"" %>" href="<%= contextPath %>/CoursesController" <%= isCourses?"aria-current=\"page\"":"" %>>My Courses</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isTasks?"active":"" %>" href="<%= contextPath %>/TaskController" <%= isTasks?"aria-current=\"page\"":"" %>>My Tasks</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isAnalytics?"active":"" %>" href="<%= contextPath %>/ActivityController" <%= isAnalytics?"aria-current=\"page\"":"" %>>Analytics</a></li>
                </ul></div>
            </dialog>
        </div>
    </nav>
</header>
