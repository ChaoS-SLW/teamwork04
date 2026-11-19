# Local Eclipse / Tomcat Setup

## Prerequisites

- Eclipse IDE with Java web development support
- JDK 21
- Apache Tomcat 9
- GitHub Desktop or Git

The source uses `javax.servlet.*`; **Tomcat 9** is the intended server. It is not currently prepared for Tomcat 10+ (`jakarta.servlet.*`).

## Clone

Clone `https://github.com/ChaoS-SLW/teamwork04.git` in GitHub Desktop.

The Eclipse project source is nested one directory down:

```text
<clone-directory>/teamwork04/src/main/java/
<clone-directory>/teamwork04/src/main/webapp/
```

## Eclipse project configuration

**Current limitation:** This repository does not contain committed Eclipse `.project` or `.classpath` metadata or a `pom.xml`. Therefore, importing it via *Existing Projects into Workspace* may not work directly.

If your local Eclipse project already exists, keep using it and connect its source directory to Git. For a fresh clone, create/configure a **Dynamic Web Project** named `teamwork04` with:

- Source folder: `src/main/java`
- Web content folder: `src/main/webapp`
- Target runtime: Apache Tomcat 9
- Java compiler: 21
- Servlet API provided by the Tomcat runtime (do not bundle a second Servlet API JAR)

Import the cloned source into that project as needed and check *Properties → Deployment Assembly*. The JDBC JAR is currently at `src/main/webapp/WEB-INF/lib/sqlite-jdbc-3.36.0.3.jar`; check that Eclipse/Tomcat loads it.

One teammate should later commit portable Eclipse project metadata or add a documented Maven build to make fresh clones easier. Do not assume either is present today.

## Start and verify

1. Start the project with *Run As → Run on Server* using Tomcat 9.
2. Check the Eclipse Console for compilation and servlet errors.
3. Open `http://localhost:8080/teamwork04/index.jsp` (adjust port and context path if different).
4. Verify the JSP test page and the shared header/footer.
5. Visit `http://localhost:8080/teamwork04/DashboardController` to test routing to `dashboard.jsp`.

The dashboard is currently a placeholder. The other routes are not ready, so a 404 for `/tasks` does not necessarily indicate a Tomcat problem.

## SQLite

A database file is currently tracked in the public repository, but there is **no versioned `schema.sql` or initialization process** in the committed source. Do not add real users' data to the tracked database. The database developer should introduce a schema and local initialization instructions before the team removes the tracked DB from a shared working baseline.

## Testing

No automated tests or CI are configured. Record manual test steps in each PR, including the URL used, the expected result, and any known failure.
