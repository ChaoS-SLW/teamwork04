<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%
    /*
     * StudyFlow - Reusable Header Component
     *
     * Automatically detects the current page.
     * Supports Java Servlet forwarding.
     */

    final String contextPath = request.getContextPath();

    // Get the original URL when a Servlet forwards to a JSP.
    Object forwardedUri =
            request.getAttribute("javax.servlet.forward.request_uri");

    String requestUri = forwardedUri instanceof String
            ? (String) forwardedUri
            : request.getRequestURI();

    String currentPath = requestUri.startsWith(contextPath)
            ? requestUri.substring(contextPath.length())
            : requestUri;

    // Identify the active navigation item.
    boolean isDashboard =
            currentPath.equals("/") ||
            currentPath.equals("/index.jsp") ||
            currentPath.equals("/dashboard.jsp");

    boolean isTasks = currentPath.equals("/tasks");

    boolean isAnalytics = currentPath.equals("/analytics");

    boolean isQuizHistory = currentPath.equals("/activity")
            && request.getParameter("quizId") == null;
%>

<header class="sf-header">

    <nav class="navbar lg:navbar-expand sf-navbar"
         aria-label="Main navigation">

        <div class="container">

            <%-- =========================
                 BRAND / LOGO
            ========================== --%>

            <a class="navbar-brand sf-brand"
               href="<%= request.getContextPath() %>/StudyController">

                <span class="sf-brand-icon" aria-hidden="true">
                    ✳
                </span>

                <span class="sf-brand-text">
                    studyflow<span class="sf-brand-dot">.</span>
                </span>

            </a>


            <%-- =========================
                 MOBILE MENU BUTTON
            ========================== --%>

            <button
                class="btn-icon navbar-toggler"
                type="button"
                data-bs-toggle="drawer"
                data-bs-target="#studyflowNav"
                aria-controls="studyflowNav"
                aria-expanded="false"
                aria-label="Open navigation menu">

                <span class="navbar-toggler-icon"
                      aria-hidden="true"></span>

            </button>


            <%-- =========================
                 RESPONSIVE NAVIGATION
            ========================== --%>

            <dialog class="drawer drawer-end"
                    tabindex="-1"
                    id="studyflowNav"
                    aria-labelledby="studyflowNavLabel">

                <%-- Mobile drawer header --%>

                <div class="drawer-header">

                    <h5 class="drawer-title"
                        id="studyflowNavLabel">
                        Navigation
                    </h5>

                    <button
                        type="button"
                        class="btn-close"
                        data-bs-dismiss="drawer"
                        aria-label="Close navigation">
                    </button>

                </div>


                <%-- Navigation items --%>

                <div class="drawer-body">

                    <ul class="navbar-nav sf-nav-list">

                        <%-- Dashboard --%>

                        <li class="nav-item">

                            <a
                                class="nav-link sf-nav-link
                                <%= isDashboard ? "active" : "" %>"

                                href="<%= request.getContextPath() %>/DashboardController"

                                <%= isDashboard
                                    ? "aria-current=\"page\""
                                    : "" %>>

                                <span>Dashboard</span>

                            </a>

                        </li>


                        <%-- My Tasks --%>

                        <li class="nav-item">

                            <a
                                class="nav-link sf-nav-link
                                <%= isTasks ? "active" : "" %>"

                                href="<%= request.getContextPath() %>/TaskController"

                                <%= isTasks
                                    ? "aria-current=\"page\""
                                    : "" %>>

                                <span>My Tasks</span>

                            </a>

                        </li>


                        <%-- Analytics --%>

                        <li class="nav-item">

                            <a
                                class="nav-link sf-nav-link
                                <%= isAnalytics ? "active" : "" %>"

                                href="<%= request.getContextPath() %>/ActivityController"

                                <%= isAnalytics
                                    ? "aria-current=\"page\""
                                    : "" %>>

                                <span>Analytics</span>

                            </a>

                        </li>


                        <%-- Quiz History --%>
                        <li class="nav-item">
                            <a
                                class="nav-link sf-nav-link
                                <%= isQuizHistory ? "active" : "" %>"
                                href="<%= request.getContextPath() %>/activity"
                                <%= isQuizHistory ? "aria-current=\\"page\\"" : "" %>>
                                <span>Quiz History</span>
                            </a>
                        </li>

                    </ul>

                </div>

            </dialog>

        </div>

    </nav>

</header>