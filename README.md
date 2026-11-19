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

- A JSP test page in `index.jsp`, including a server-time display.
- Reusable `head.jspf`, `header.jsp`, and `footer.jsp` components.
- Shared styles in `assets/css/style.css`.
- `DashboardController`, which forwards requests to a dashboard JSP placeholder.
- Placeholder controllers, entities, DAOs, services, JSP screens, and JavaScript modules for the remaining features.
- An SQLite JDBC driver JAR and a committed `studyflow.db` file.

**Not yet confirmed implemented:** course or task CRUD, persisted study sessions, progress calculations, contribution heatmap, analytics, authentication/guest isolation, or automated tests. Do not describe these features as working until tested.

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

1. **Navigation mismatch:** The header/footer use paths such as `/dashboard`, `/tasks`, and `/analytics`, while actual Servlet annotations include `/DashboardController`, `/TaskController`, and `/ActivityController`. Establish one consistent routing convention and verify each link.
2. **Incomplete dashboard:** `dashboard.jsp` currently contains the shared head/header but no dashboard main content.
3. **Unimplemented persistence:** Several entity/DAO/controller classes are placeholders. Define the schema and API contract before integrating frontend and backend.
4. **Tracked generated files:** The repository currently includes `studyflow.db` and `teamwork04/build/classes/`. The `.gitignore` prevents *new untracked* files of these types, but already-tracked files need a separate, coordinated cleanup. Back up anything useful first.
5. **No reproducible DB setup:** There is no version-controlled `schema.sql`/initialization guide yet. Prefer schema + optional fictional seed data over committing a mutable database.
6. **Public repository:** Never use real student records, credentials, secrets, or sensitive data in commits.

## Planned features (development goals)

- **Courses:** create, rename, delete, categorize, and view personal study courses.
- **Tasks:** add/edit/delete tasks, deadlines, priorities, course assignments, and completion.
- **Dashboard:** course/task summary, upcoming deadlines, course-completion progress.
- **Study sessions:** record study dates and minutes.
- **Activity analytics:** GitHub-style heatmap, 7-/30-day activity charts, optional streak and XP.
- **Guest access:** if multiple classmates use the application, isolate users through server-side sessions and ownership checks; browser localStorage alone is not an access boundary.

## Suggested build order

1. Fix and verify navigation between `index.jsp`, `DashboardController`, and the shared header/footer.
2. Finish the dashboard layout using clearly marked **mock data**.
3. Define a SQLite schema, entities, and the servlet/API contract with all teammates.
4. Implement courses and tasks CRUD and connect the forms.
5. Verify that changes survive refresh and Tomcat restart.
6. Add study-session logging, heatmap, and analytics after persistence works.
7. Test mobile layout, empty states, invalid input, and demo scenarios.

**Minimum demo target:** create a course → add a task → mark it completed → refresh the page → see the saved progress.

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
