<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="aiwa.ulti.Html" %>
<!DOCTYPE html>
<html lang="en">
<head><title>Create account | StudyFlow</title><%@ include file="/WEB-INF/views/components/head.jspf" %></head>
<body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />
    <main class="container sf-auth-page py-5">
        <section class="card sf-auth-card border-0 shadow-sm mx-auto"><div class="card-body p-4 p-md-5">
            <div class="text-center mb-4"><span class="sf-auth-icon" aria-hidden="true">🌱</span><span class="sf-eyebrow d-block mt-3">START SMALL, GROW STEADY</span><h1 class="h2 fw-bold mt-2">Create your account</h1><p class="text-body-secondary">Your courses, tasks, and study sessions are saved to your account.</p></div>
            <% if(request.getAttribute("registerError")!=null) { %><div class="alert alert-danger" role="alert"><%= Html.escape(request.getAttribute("registerError")) %></div><% } %>
            <form method="post" action="<%= request.getContextPath() %>/RegisterController">
                <input type="hidden" name="csrfToken" value="<%= Html.csrfToken(request) %>">
                <div class="mb-3"><label class="form-label" for="username">Name</label><input class="form-control" id="username" name="username" autocomplete="name" minlength="2" maxlength="60" required value="<%= Html.escape(request.getParameter("username")) %>"></div>
                <div class="mb-3"><label class="form-label" for="email">Email</label><input class="form-control" id="email" name="email" type="email" autocomplete="email" maxlength="254" required value="<%= Html.escape(request.getParameter("email")) %>"></div>
                <div class="mb-3"><label class="form-label" for="password">Password</label><input class="form-control" id="password" name="password" type="password" autocomplete="new-password" minlength="10" maxlength="128" required><div class="form-text">Use 10 to 128 characters.</div></div>
                <div class="mb-4"><label class="form-label" for="confirmPassword">Confirm password</label><input class="form-control" id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password" minlength="10" maxlength="128" required></div>
                <button class="btn btn-primary btn-lg rounded-pill w-100" type="submit">Create account</button>
            </form>
            <p class="text-center text-body-secondary mt-4 mb-0">Already have an account? <a href="<%= request.getContextPath() %>/LoginController">Sign in</a></p>
            <p class="text-center mt-2 mb-0"><a href="<%= request.getContextPath() %>/home">Back to home</a></p>
        </div></section>
    </main>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />
</body>
</html>
