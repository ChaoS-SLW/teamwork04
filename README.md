# StudyFlow — teamwork04

A three-person school project for planning courses, assignments, study sessions, and learning progress.

> **Status: work in progress.** The current repository contains a working JSP test page, shared layout fragments, and Servlet scaffolding. Course/task persistence, charts, and the complete dashboard are **not yet implemented**.

## Technology

- Java 21, Java Servlets (`javax.servlet`), JSP
- Apache Tomcat 9 and Eclipse Dynamic Web Project
- HTML, CSS, JavaScript, Bootstrap `6.0.0-alpha.1` (CDN)
- SQLite via JDBC (driver JAR currently under `WEB-INF/lib/`)
- GitHub + GitHub Desktop for collaboration

Do not switch this project to Spring Boot, Jakarta Servlet (`jakarta.servlet`), or another framework without a team decision.

## Source layout

```text
teamwork04/                        # GitHub repository root
├── README.md
├── .gitignore
├── teamwork04/                    # Eclipse web project source
│   └── src/main/
│       ├── java/aiwa/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── entity/
│       │   ├── model/
│       │   ├── service/
│       │   └── ulti/
│       └── webapp/
│           ├── index.jsp
│           ├── assets/css/style.css
│           ├── assets/js/
│           └── WEB-INF/
│               ├── lib/sqlite-jdbc-3.36.0.3.jar
│               └── views/
│                   ├── dashboard.jsp
│                   ├── tasks.jsp
│                   ├── analytics.jsp
│                   ├── coursesjsp.jsp
│                   └── components/
│                       ├── head.jspf
│                       ├── header.jsp
│                       └── footer.jsp
```

## Local development

See [Local setup](docs/SETUP.md) for prerequisites, Eclipse import caveats, deployment, and browser URLs.

Once deployed with a context path of `teamwork04`:

- `http://localhost:8080/teamwork04/index.jsp` — current JSP connection test
- `http://localhost:8080/teamwork04/DashboardController` — forwards to the still-incomplete dashboard
- `http://localhost:8080/teamwork04/StudyController` — currently forwards to `index.jsp`

The navbar URLs `/dashboard`, `/tasks`, and `/analytics` do **not** match the current Servlet mappings yet.

## Planned capabilities (not all implemented)

Course creation and management, task CRUD, due dates and priorities, study-session logging, completion progress, streaks, contribution heatmap, and 7-/30-day activity analytics.

See [Implementation status and next steps](docs/IMPLEMENTATION_STATUS.md) for the real current state.

## Team workflow

- Frontend: JSP, shared components, CSS and browser-side JavaScript — `feature/frontend`
- Backend: Servlet endpoints, request validation, models and task operations — `feature/backend`
- Database: SQLite schema, JDBC DAO, study sessions and analytics queries — `feature/database`

Start each change from the latest `main`, work on a feature branch, and open a pull request. See [CONTRIBUTING.md](CONTRIBUTING.md).

## Data and security

The repository is **public**. Never commit credentials, personal student records, or production data. `.gitignore` now excludes `*.db` and compiled files, but a `studyflow.db` file and compiled classes were committed **before** those rules were added. Removing them from a later commit does not erase Git history. Add a reproducible `schema.sql` before relying on a database-free checkout.

## Verification status

There is currently no automated build or test pipeline in this repository. A successful deployment in Eclipse/Tomcat must be tested manually; do not treat this README as proof that all pages and APIs work.
