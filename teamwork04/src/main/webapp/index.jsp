<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <title>StudyFlow | Dashboard</title>

    <%-- Shared Bootstrap and CSS --%>
    <%@ include file="/WEB-INF/views/components/head.jspf" %>

</head>

<body>

    <%-- Shared Header --%>
    <jsp:include
        page="/WEB-INF/views/components/header.jsp" />


    <%-- Page Content --%>
        <main class="container py-5">

        <div class="alert alert-success" role="alert">
            <h1 class="h4">✅ index.jsp is working!</h1>

            <p class="mb-0">
                JSP, Tomcat 9, and Bootstrap 6 are connected.
            </p>
        </div>

        <p>
            Current server time:
            <strong><%= new java.util.Date() %></strong>
        </p>

        <a href="<%= request.getContextPath() %>/StudyController"
           class="btn btn-primary">

            Test StudyController →

        </a>

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