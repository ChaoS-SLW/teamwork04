<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    final String contextPath = request.getContextPath();
    Object forwardedUri = request.getAttribute("javax.servlet.forward.request_uri");
    String requestUri = forwardedUri instanceof String ? (String) forwardedUri : request.getRequestURI();
    String currentPath = requestUri.startsWith(contextPath) ? requestUri.substring(contextPath.length()) : requestUri;
    boolean isHome = currentPath.equals("/") || currentPath.equals("/index.jsp") || currentPath.equals("/StudyController") || currentPath.equals("/home");
    boolean isDashboard = currentPath.equals("/DashboardController");
    boolean isCourses = currentPath.equals("/CoursesController");
    boolean isTasks = currentPath.equals("/TaskController");
    boolean isAnalytics = currentPath.equals("/ActivityController");
    javax.servlet.http.HttpSession userSession = request.getSession(false);
    boolean signedIn = userSession != null && userSession.getAttribute("userId") instanceof Number;
    String username = signedIn ? String.valueOf(userSession.getAttribute("username")) : "";
%>
<header class="sf-header">
    <nav class="navbar sf-navbar" aria-label="Main navigation">
        <div class="container">
            <a class="navbar-brand sf-brand" href="<%= request.getContextPath() %>/home"><span class="sf-brand-icon" aria-hidden="true">✳</span><span class="sf-brand-text">studyflow<span class="sf-brand-dot">.</span></span></a>
            <% if (signedIn) { %><span class="sf-demo-badge">Hi, <%= aiwa.ulti.Html.escape(username) %></span><% } %>
            
            <div class="sf-nav-menu">
                
                <div class="drawer-body"><ul class="navbar-nav sf-nav-list">
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isHome?"active":"" %>" href="<%= request.getContextPath() %>/home" <%= isHome?"aria-current=\"page\"":"" %>>Home</a></li>
                    <% if (signedIn) { %>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isDashboard?"active":"" %>" href="<%= request.getContextPath() %>/DashboardController" <%= isDashboard?"aria-current=\"page\"":"" %>>Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isCourses?"active":"" %>" href="<%= request.getContextPath() %>/CoursesController" <%= isCourses?"aria-current=\"page\"":"" %>>My Courses</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isTasks?"active":"" %>" href="<%= request.getContextPath() %>/TaskController" <%= isTasks?"aria-current=\"page\"":"" %>>My Tasks</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link <%= isAnalytics?"active":"" %>" href="<%= request.getContextPath() %>/ActivityController" <%= isAnalytics?"aria-current=\"page\"":"" %>>Analytics</a></li>
                    <li class="nav-item"><form method="post" action="<%= request.getContextPath() %>/LogoutController"><input type="hidden" name="csrfToken" value="<%= aiwa.ulti.Html.csrfToken(request) %>"><button class="nav-link sf-nav-link" type="submit">Sign out</button></form></li>
                    <% } else { %>
                    <li class="nav-item"><a class="nav-link sf-nav-link" href="<%= request.getContextPath() %>/LoginController">Sign in</a></li>
                    <li class="nav-item"><a class="nav-link sf-nav-link" href="<%= request.getContextPath() %>/RegisterController">Create account</a></li>
                    <% } %>
                </ul></div>
            </div>
        </div>
    </nav>
</header>
