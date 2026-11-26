<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="aiwa.ulti.Html" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Sign in | StudyFlow</title>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>
  </head>
  <body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container sf-auth-page py-5">
      <section class="card sf-auth-card border-0 shadow-sm mx-auto">
        <div class="card-body p-4 p-md-5">
          <div class="text-center mb-4">
            <span class="sf-auth-icon" aria-hidden="true">✳</span
            ><span class="sf-eyebrow d-block mt-3">WELCOME BACK</span>
            <h1 class="h2 fw-bold mt-2">Sign in to StudyFlow</h1>
            <p class="text-body-secondary">Pick up where you left off.</p>
          </div>
          <% if("1".equals(request.getParameter("logout"))) { %>
          <div class="alert alert-success" role="status">
            You’ve signed out.
          </div>
          <% } %>
<% if(request.getAttribute("loginError")!=null) { %>
          <div class="alert alert-danger" role="alert"><%= Html.escape(request.getAttribute("loginError")) %></div>
          <% } %>
          <form method="post" action="<%= request.getContextPath() %>/LoginController">
            <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>" />
            <input type="hidden" name="next" value="<%= Html.escape(request.getAttribute("next")) %>" />
            <div class="mb-3">
              <label class="form-label" for="username">Name</label
              ><input
                class="form-control form-control-lg"
                id="username"
                name="username"
                type="text"
                autocomplete="username"
                maxlength="60"
                required
                autofocus
                value="<%= Html.escape(request.getAttribute("loginUsername")) %>"
              />
            </div>
            <div class="mb-4">
              <label class="form-label" for="password">Password</label
              ><input
                class="form-control form-control-lg"
                id="password"
                name="password"
                type="password"
                autocomplete="current-password"
                required
              />
            </div>
            <button
              class="btn btn-primary btn-lg rounded-pill w-100"
              type="submit"
            >
              Sign in
            </button>
          </form>
          <p class="text-center text-body-secondary mt-4 mb-0">
            New to StudyFlow?
            <a href="<%= request.getContextPath() %>/RegisterController">Create an account</a>
          </p>
          <p class="text-center mt-2 mb-0">
            <a href="<%= request.getContextPath() %>/home">Back to home</a>
          </p>
        </div>
      </section>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
  </body>
</html>
