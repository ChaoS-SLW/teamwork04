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
    boolean isPlanner = currentPath.equals("/PlannerController");
    boolean isPlan = isTasks || isPlanner;
    javax.servlet.http.HttpSession userSession = request.getSession(false);
    boolean signedIn = userSession != null && userSession.getAttribute("userId") instanceof Number;
    String username = signedIn ? String.valueOf(userSession.getAttribute("username")) : "";
%>
<header class="sf-header">
  <nav class="navbar lg:navbar-expand sf-navbar" aria-label="Main navigation">
    <div class="container">
      <a class="navbar-brand sf-brand" href="<%= request.getContextPath() %>/home"
        ><img
          class="sf-brand-icon"
          src="<%= request.getContextPath() %>/assets/images/StudyFlowLogo.png"
          alt=""
          aria-hidden="true"
        /><span class="sf-brand-text"
          >studyflow<span class="sf-brand-dot">.</span></span
        ></a
      >
      <% if (signedIn) { %><span class="sf-demo-badge">Hi, <%= aiwa.ulti.Html.escape(username) %></span
      ><% } %>
      <button
        class="btn-icon navbar-toggler"
        type="button"
        data-bs-toggle="drawer"
        data-bs-target="#studyflowNav"
        aria-controls="studyflowNav"
        aria-expanded="false"
        aria-label="Open navigation menu"
      >
        <span class="navbar-toggler-icon" aria-hidden="true"></span>
      </button>
      <dialog
        class="drawer drawer-end"
        tabindex="-1"
        id="studyflowNav"
        aria-labelledby="studyflowNavLabel"
      >
        <div class="drawer-header">
          <h2 class="drawer-title h5" id="studyflowNavLabel">Navigation</h2>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="drawer"
            aria-label="Close navigation"
          ></button>
        </div>
        <div class="drawer-body">
          <ul class="navbar-nav sf-nav-list">
            <% if (!signedIn) { %>
            <li class="nav-item">
              <a
                class="nav-link sf-nav-link <%= isHome?"active":"" %>"
                href="<%= request.getContextPath() %>/home"
                <%= isHome?"aria-current=\"page\"":"" %>
                >Home</a
              >
            </li>
            <% } %>
            <% if (signedIn) { %>
            <li class="nav-item">
              <a
                class="nav-link sf-nav-link <%= isDashboard?"active":"" %>"
                href="<%= request.getContextPath() %>/DashboardController"
                <%= isDashboard?"aria-current=\"page\"":"" %>
                >Dashboard</a
              >
            </li>
            <li class="nav-item">
              <a
                class="nav-link sf-nav-link <%= isCourses?"active":"" %>"
                href="<%= request.getContextPath() %>/CoursesController"
                <%= isCourses?"aria-current=\"page\"":"" %>
                >My Courses</a
              >
            </li>
            <li class="nav-item">
              <a
                class="nav-link sf-nav-link <%= isPlan?"active":"" %>"
                href="<%= request.getContextPath() %>/TaskController"
                <%= isPlan?"aria-current=\"page\"":"" %>
                >Plan</a
              >
            </li>
            <li class="nav-item">
              <a
                class="nav-link sf-nav-link <%= isAnalytics?"active":"" %>"
                href="<%= request.getContextPath() %>/ActivityController"
                <%= isAnalytics?"aria-current=\"page\"":"" %>
                >Analytics</a
              >
            </li>
            <li class="nav-item">
              <div class="nav-more">
                <button
                  class="nav-btn nav-btn--submenu nav-more__trigger"
                  type="button"
                  aria-haspopup="menu"
                  aria-expanded="false"
                  aria-label="More navigation options"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2.2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    aria-hidden="true"
                  >
                    <path d="M4 5h16"></path>
                    <path d="M4 12h16"></path>
                    <path d="M4 19h16"></path>
                  </svg>
                </button>
                <div
                  class="action-menu dropdown dropdown--floating dropdown--panel nav-more__menu"
                  role="menu"
                  aria-label="More navigation options"
                  hidden
                >
                  <div class="dropdown__section">
                    <form method="post" action="<%= request.getContextPath() %>/LogoutController">
                      <input
                        type="hidden"
                        name="csrfToken"
                        value="<%= aiwa.ulti.Html.csrfToken(request) %>"
                      />
                      <button
                        class="dropdown__item nav-more__menu-item"
                        type="submit"
                        role="menuitem"
                      >
                        <span class="dropdown__label">Sign out</span>
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            </li>
            <% } else { %>
            <li class="nav-item">
              <a
                class="nav-link sf-nav-link"
                href="<%= request.getContextPath() %>/LoginController"
                >Sign in</a
              >
            </li>
            <li class="nav-item">
              <a
                class="nav-link sf-nav-link"
                href="<%= request.getContextPath() %>/RegisterController"
                >Create account</a
              >
            </li>
            <% } %>
          </ul>
        </div>
      </dialog>
    </div>
  </nav>
</header>
<script>
  (() => {
    document.querySelectorAll(".sf-nav-list .nav-more").forEach((container) => {
      const trigger = container.querySelector(".nav-more__trigger");
      const menu = container.querySelector(".nav-more__menu");
      if (!trigger || !menu) return;

      const closeMenu = () => {
        menu.hidden = true;
        trigger.setAttribute("aria-expanded", "false");
      };

      trigger.addEventListener("click", (event) => {
        event.stopPropagation();
        const shouldOpen = menu.hidden;
        menu.hidden = !shouldOpen;
        trigger.setAttribute("aria-expanded", String(shouldOpen));
      });

      container.addEventListener("click", (event) => event.stopPropagation());
      document.addEventListener("click", (event) => {
        if (!container.contains(event.target)) closeMenu();
      });
      document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && !menu.hidden) {
          closeMenu();
          trigger.focus();
        }
      });
    });
  })();
</script>
