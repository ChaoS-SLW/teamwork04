<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%
    /*
     * StudyFlow - Reusable Footer Component
     *
     * Shared across all JSP pages.
     * Uses contextPath for safe navigation.
     */

    final String contextPath = request.getContextPath();
    final int currentYear = java.time.Year.now().getValue();
%>

<footer class="sf-footer">
  <div class="container">
    <%-- ==============================
             FOOTER MAIN CONTENT
        =============================== --%>

    <div class="row align-items-center gy-4">
      <%-- Brand Section --%>

      <div class="col-12 col-md-6">
        <a href="<%= request.getContextPath() %>/home" class="sf-footer-brand">
          <img
            class="sf-footer-logo"
            src="<%= request.getContextPath() %>/assets/images/StudyFlowLogo.png"
            alt=""
            aria-hidden="true"
          />

          <span class="sf-footer-title">
            studyflow<span class="sf-brand-dot">.</span>
          </span>
        </a>

        <p class="sf-footer-description">
          七転び八起き。
          <br />
          Make every study day count.
        </p>
      </div>

      <%-- Navigation Section --%>

      <div class="col-12 col-md-6">
        <nav aria-label="Footer navigation">
          <ul class="sf-footer-nav">
            <li>
              <a href="<%= request.getContextPath() %>/DashboardController"> Dashboard </a>
            </li>

            <li>
              <a href="<%= contextPath %>/CoursesController"> My Courses </a>
            </li>

            <li>
              <a href="<%= contextPath %>/TaskController"> My Tasks </a>
            </li>

            <li>
              <a href="<%= contextPath %>/ActivityController"> Analytics </a>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <%-- ==============================
             FOOTER BOTTOM
        =============================== --%>

    <div class="sf-footer-bottom">
      <p class="mb-0">&copy; <%= currentYear %> StudyFlow. All rights reserved.</p>

      <p class="mb-0">Made with ヨツバ🍀 for students.</p>
    </div>
  </div>
</footer>
