# StudyFlow — teamwork04

**StudyFlow** is a school team project for organizing courses, assignments, and study activity. The goal is a responsive study dashboard backed by Java Servlets, JSP, and SQLite.

> **Project status: In development.** This README distinguishes implemented scaffolding from planned features. The repository has not been verified by running Eclipse/Tomcat in this review.

## Tech stack

| Layer | Technology |
| --- | --- |
| Backend | Java 21, Java Servlets (`javax.servlet`) |
| Views | JSP, HTML, CSS, JavaScript |
| UI | Bootstrap `6.0.0-alpha.1` via CDN, custom CSS |
| Server | Apache Tomcat 9 |
| Database | SQLite through JDBC |
| IDE / version control | Eclipse Dynamic Web Project, Git and GitHub |

**Compatibility:** This source uses `javax.servlet`, which is compatible with Tomcat 9. Tomcat 10+ uses `jakarta.servlet` and would require migration.

## What's currently in the repository

- Separate Home (`StudyController` → `index.jsp`) and Dashboard (`DashboardController` → `dashboard.jsp`) pages.
- SQLite-backed course listing and course create, edit, and delete actions.
- SQLite-backed task creation, editing, completion, deletion, and course/status/priority filters.
- Dashboard totals, course task progress, upcoming tasks, weekly study minutes, streak, and a daily activity heatmap.
- Study-session logging, recent session history, weekly and 30-day charts, and a yearly activity heatmap.
- A versioned, idempotent `WEB-INF/schema.sql` bootstrap and compatibility migration for older progress/result tables.
- Shared JSP components, server-side validation, prepared SQL statements, and escaped user-provided text.

**Demo account:** The app creates one fictional shared demo identity because sign-in is not part of this implementation. All visitors to one Tomcat instance see the same tasks and study activity. Do not put personal or sensitive records into the public demo.

## Repository structure

The Eclipse web project is nested inside the GitHub repository:

```text
teamwork04/                       # GitHub repository
├── README.md
├── .gitignore
├── studyflow.db                  # Existing tracked DB; avoid real personal data
└── teamwork04/                   # Eclipse project source
    └── src/main/
        ├── java/aiwa/
        │   ├── controller/       # Servlet endpoints
        │   ├── dao/              # Database access (placeholders)
        │   ├── entity/           # Domain entities (placeholders)
        │   ├── model/
        │   ├── service/
        │   └── ulti/
        └── webapp/
            ├── index.jsp
            ├── assets/
            │   ├── css/style.css
            │   └── js/
            └── WEB-INF/
                ├── lib/sqlite-jdbc-3.36.0.3.jar
                └── views/
                    ├── dashboard.jsp
                    ├── coursesjsp.jsp
                    ├── tasks.jsp
                    ├── analytics.jsp
                    └── components/
                        ├── head.jspf
                        ├── header.jsp
                        └── footer.jsp
```

## Running locally

### Prerequisites

- JDK 21
- Eclipse IDE with Java web development support
- Apache Tomcat 9
- Git / GitHub Desktop (recommended for collaborating)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/ChaoS-SLW/teamwork04.git
   ```
2. Open Eclipse and configure a **Dynamic Web Project** using the nested `teamwork04/` source directory. Set Java 21, Tomcat 9, `src/main/java` as the Java source path, and `src/main/webapp` as the web root.
3. Verify the Tomcat runtime supplies the Servlet API and that the SQLite JDBC JAR in `WEB-INF/lib/` is deployed.
4. Run the project using **Run As → Run on Server**.
5. If the Eclipse context path is `teamwork04` and Tomcat runs on port `8080`, try:
   - `http://localhost:8080/teamwork04/index.jsp` — existing JSP test page
   - `http://localhost:8080/teamwork04/DashboardController` — dashboard JSP route
   - `http://localhost:8080/teamwork04/StudyController` — forwards to `index.jsp`

**Fresh-clone limitation:** As currently committed, the repository has neither portable Eclipse `.project`/`.classpath` files nor a `pom.xml`. A simple **Import → Existing Projects into Workspace** may not detect a configured Eclipse project. Teammates may need to configure the Dynamic Web Project manually. This is a setup issue to solve together, not a reason to overwrite the existing source.

## Known issues / technical debt

1. The demo is shared and has no authentication; add server-side login and record ownership before using it for private student data.
2. The runtime database is `WEB-INF/studyflow.db`; the repository-root `studyflow.db` remains a historical snapshot and is not used by the web app.
3. `teamwork04/build/classes/` and the root database are already tracked. This change leaves those existing tracked artifacts untouched.
4. The app initializes schema and a fictional demo user at Tomcat startup. For durable deployment, configure the database outside the deployed webapp and add a backup plan.
5. Automated tests and a Tomcat UI run have not been added or performed.

## Planned features (development goals)

- **Courses:** create, rename, delete, categorize, and view personal study courses.
- **Tasks:** add/edit/delete tasks, deadlines, priorities, course assignments, and completion.
- **Dashboard:** course/task summary, upcoming deadlines, course-completion progress.
- **Study sessions:** record study dates and minutes.
- **Activity analytics:** GitHub-style heatmap, 7-/30-day activity charts, optional streak and XP.
- **Guest access:** if multiple classmates use the application, isolate users through server-side sessions and ownership checks; browser localStorage alone is not an access boundary.

## Suggested build order

1. Run the project on Tomcat 9 and confirm schema initialization against a backup of the intended SQLite file.
2. Walk through the minimum demo: create a course → create a task → mark it complete → refresh and verify persistence.
3. Log a study session and verify the charts and heatmap update.
4. Test mobile layout, invalid input, empty states, duplicate course names, and course deletion when lessons exist.
5. Add authentication and per-user course ownership before presenting this as a private multi-user application.

## Three-person collaboration

| Role | Primary responsibility | Suggested branch |
| --- | --- | --- |
| Frontend | JSP views, components, responsive CSS, JavaScript | `feature/frontend` |
| Backend | Servlets, request validation, models, route consistency | `feature/backend` |
| Database | SQLite schema, JDBC, DAOs, activity queries | `feature/database` |

Use GitHub Desktop to pull updates, work on feature branches, commit small changes, and open pull requests. Coordinate shared files and data contracts before editing them in parallel. Avoid committing generated binaries or database contents.

## Testing / completion criteria

Before saying a feature is done, verify its route in Tomcat 9, server-side validation, behavior after a refresh, and any affected navigation links. For database features, also check persistence across a restart. There is currently **no automated test or CI configuration** in the repository.

---

Built as a learning project. Keep the scope small, the code reviewable, and the README aligned with what the source actually does.
