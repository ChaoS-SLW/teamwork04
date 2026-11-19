# StudyFlow — AI Development Guide & Project README

**Repository:** https://github.com/ChaoS-SLW/teamwork04  
**Project:** \`teamwork04\` — school team web application  
**Status:** Work in progress  
**Team:** 3 students  
**Purpose of this README:** Give human teammates and AI coding assistants a precise specification for developing StudyFlow **without accidentally replacing existing code or changing the tech stack**.

> **Important for AI assistants:** Read the real repository files before making suggestions. The planned features below are **requirements**, not evidence that they are implemented. Change only the files required by the user's current request, and preserve working code, file paths, and Git history. Never treat the school's Bitbucket repository as the GitHub destination.

---

## 1. Product vision

**StudyFlow — “Small steps. Big progress.”**

A responsive, student-friendly web application that lets students organize their own courses, manage assignments, record study time, and understand their learning habits through visual progress indicators.

**Primary audience:** Students using the application on laptops or smartphones.  
**Demo environment:** Java web application running locally on Tomcat 9; classmates may access it on the same trusted network during a presentation.

### Main user journey

1. Open **Home**.
2. Choose **Get Started** to open the **Dashboard**.
3. Create a course (e.g., Java Programming).
4. Add assignments to that course.
5. Mark assignments completed.
6. Log study time.
7. See updated completion progress, upcoming tasks, and study activity.
8. Refresh the page and retain data via SQLite once persistence is implemented.

For the minimum viable demo, prioritize **create course → create task → complete task → refresh → see saved data**.

---

## 2. Non-negotiable technology constraints

| Area | Required technology |
| --- | --- |
| IDE / project | Eclipse Dynamic Web Project |
| Java | Java 21 |
| Application server | Apache Tomcat 9 |
| Web backend | Java Servlets using \`javax.servlet.*\` |
| Server-rendered pages | JSP and JSP fragments |
| Frontend | HTML, CSS, JavaScript |
| CSS framework | Existing Bootstrap \`6.0.0-alpha.1\` CDN |
| Persistence | SQLite and JDBC |
| Version control | GitHub repository \`ChaoS-SLW/teamwork04\` |

**Do not migrate to** Spring Boot, Javalin, React, Next.js, Node/Express, or \`jakarta.servlet\` without explicit approval. Tomcat 9 uses \`javax.servlet\`; Tomcat 10+ is not a drop-in replacement.

**Do not** create a second competing project, replace the existing layout with a generated template, add unrelated frameworks, or replace the current manually installed JDBC driver without discussing the migration.

---

## 3. Existing source structure — preserve these paths

\`\`\`text
teamwork04/                            # GitHub repository root
├── README.md
├── .gitignore
├── studyflow.db                       # Currently tracked; not ideal for long-term sharing
└── teamwork04/                        # Eclipse project folder
    └── src/main/
        ├── java/aiwa/
        │   ├── controller/
        │   │   ├── StudyController.java
        │   │   ├── DashboardController.java
        │   │   ├── CoursesController.java
        │   │   ├── TaskController.java
        │   │   └── ActivityController.java
        │   ├── dao/
        │   ├── entity/
        │   ├── model/
        │   ├── service/
        │   └── ulti/                   # Current package name: do not rename casually
        └── webapp/
            ├── index.jsp
            ├── assets/
            │   ├── css/style.css
            │   └── js/
            │       ├── api.js
            │       ├── app.js
            │       ├── tasks.js
            │       ├── dashboard.js
            │       └── analytics.js
            └── WEB-INF/
                ├── lib/sqlite-jdbc-3.36.0.3.jar
                └── views/
                    ├── dashboard.jsp
                    ├── coursesjsp.jsp   # Existing name; don't silently rename
                    ├── tasks.jsp
                    ├── analytics.jsp
                    └── components/
                        ├── head.jspf
                        ├── header.jsp
                        └── footer.jsp
\`\`\`

Java package names begin with \`aiwa\`. Frontend assets remain outside \`WEB-INF\`, while protected JSP views stay inside \`WEB-INF/views\` and are accessed through Servlets.

---

## 4. Page responsibilities and controller mappings

**This division is intentional and must be preserved:**

| Page | Existing Servlet URL | JSP | Intended responsibility |
| --- | --- | --- | --- |
| **Home** | \`/StudyController\` | \`/index.jsp\` | Public landing page: product introduction, benefits, Get Started |
| **Dashboard** | \`/DashboardController\` | \`/WEB-INF/views/dashboard.jsp\` | Study overview, progress, quick actions |
| **My Courses** | \`/CoursesController\` | \`/WEB-INF/views/coursesjsp.jsp\` | Create, list, edit, delete courses |
| **My Tasks** | \`/TaskController\` | \`/WEB-INF/views/tasks.jsp\` | Assignments and deadlines |
| **Analytics** | \`/ActivityController\` | \`/WEB-INF/views/analytics.jsp\` | Study sessions, activity heatmap, trends |

Current controller names and URL mappings are **real existing code**. Use them unless a specific change has been approved.

### Existing implementation mismatch to resolve deliberately

The current \`index.jsp\` already contains a sample dashboard-style layout (summary cards, activity and progress sections), while \`dashboard.jsp\` contains little page content. **Target behavior is still Home in \`index.jsp\`, Dashboard in \`dashboard.jsp\`.** Do not move or delete that content without first checking which sections should be reused.

Some active-navigation logic in \`header.jsp\` does not recognize the Servlet URLs consistently. Test links and active states when developing navigation.

The brand/logo should lead to \`/StudyController\`. The Home page’s Get Started action should lead to \`/DashboardController\`.

Use \`request.getContextPath()\` in JSP links; never hardcode the deployed context path.

---

## 5. Reusable UI and visual design

The following reusable components **already exist**:

- \`head.jspf\` — metadata, Bootstrap CDN, and \`assets/css/style.css\`
- \`header.jsp\` — responsive site navigation and brand
- \`footer.jsp\` — shared footer

**Keep these components reusable.** Avoid duplicating their HTML or loading the Bootstrap script twice. Complete JSP pages own one \`<html>\`, \`<head>\`, and \`<body>\`; included header/footer files are fragments.

### UI direction

- Brand name: **studyflow.**
- Tagline: **Small steps. Big progress.**
- Modern, friendly, clean, student-oriented design
- Current primary accent: purple (\`#8b5cf6\`)
- Rounded cards, readable typography, spacious layouts
- Mobile-first responsive behavior, including narrow screens around 320px
- Accessible labels, semantic HTML, keyboard focus states, reduced-motion support
- Use Bootstrap 6 alpha syntax already present in the source; verify components against the installed version before changing markup
- Clearly distinguish **sample/demo data** from real stored user data

### Home page requirements

- Hero title and concise StudyFlow explanation
- **Get Started** action leading to DashboardController
- Short explanation of course tracking, assignments, and progress
- Shared header and footer
- Not the permanent location for the logged-in study dashboard

### Dashboard requirements

- Greeting and “Small steps. Big progress.” message
- **+ Add Course** and **+ New Task** actions
- Course count, completed tasks, study streak
- GitHub-style study activity heatmap
- Weekly study progress bar/line chart
- Upcoming assignments and deadlines
- Empty states when no courses or tasks exist

---

## 6. Feature specification and priorities

### P0 — Essential MVP

**A. Course management**
- Add a custom course (name required)
- Optional description and color
- List the student's courses as cards
- Edit and delete a course, with confirmation for destructive actions
- Open course details and show related tasks
- Show completion percentage from tasks

**B. Task management**
- Add/edit/delete tasks
- Associate each task with a course
- Optional due date and priority (high, medium, low)
- Toggle completed / incomplete state
- Filter tasks by course, status, and priority
- Highlight overdue incomplete tasks
- Persist changes to SQLite

**C. Navigation and persistence**
- Correct Home / Dashboard / Courses / Tasks / Analytics navigation
- Consistent controller-to-JSP forwarding
- JDBC-backed data storage with parameterized SQL
- Useful validation and error messages
- Data survives refresh and a Tomcat restart

### P1 — Important enhancements

**D. Study-session tracking**
- Select course, date, and minutes studied
- View study history and total minutes/hours
- Prevent zero/negative or invalid durations

**E. Progress and analytics**
- Course progress = completed tasks / total tasks × 100; show 0% or “No tasks yet” if no tasks exist
- Activity heatmap based on actual saved sessions/completions, not random generated values
- Daily/weekly activity and 7-/30-day trends
- Clearly label any temporary mock data

### P2 — Optional after MVP

- XP, levels, streak badges
- Dark mode
- Calendar view
- Pomodoro timer
- More detailed course goals and comparisons

**Do not implement P2 features while P0 data creation and persistence are broken.**

---

## 7. Proposed data model — design first, do not assume it exists

The following is a **target model**, not a description of the present database. The database developer should inspect the actual SQLite schema before changing anything.

**Course**
- \`id\`, \`owner_id\` (or agreed guest key), \`name\`, \`description\`, \`color\`, \`created_at\`

**Task**
- \`id\`, \`course_id\`, \`owner_id\`, \`title\`, \`description\`, \`priority\`, \`due_date\`, \`completed\`, \`completed_at\`

**StudySession**
- \`id\`, \`course_id\`, \`owner_id\`, \`study_date\`, \`minutes\`, \`created_at\`

Requirements:
- Define an agreed \`schema.sql\` and initialization process.
- Use SQLite foreign keys where relationships are required.
- Use prepared statements and close JDBC resources reliably.
- Never trust client-submitted ownership IDs for authorization.
- Do not commit live student data, passwords, or secrets.
- The repository currently tracks \`studyflow.db\`; preserve and back up any useful demo data until a reproducible schema/seed process is agreed.

### Multiple-user classroom presentation

If classmates will access one running Tomcat instance, implement separate **server-side HTTP sessions** and check record ownership for all reads and writes. \`localStorage\` is only for harmless UI preferences, not identity or access control. If per-user sessions are out of scope for the one-week MVP, use **fictional shared demo data** and explicitly tell the audience that the demo is shared.

---

## 8. MVC and coding expectations

Follow this flow for real features:

\`\`\`text
Browser UI (JSP + JS)
    -> Java Servlet (controller / input validation)
    -> Service or DAO
    -> SQLite JDBC
    -> Result / request attributes
    -> JSP rendering or JSON response
\`\`\`

- Controller: routing, input validation, HTTP status/redirect/forward.
- Entity/model: typed data; keep business state out of JSP markup.
- DAO: SQL queries and database access.
- Service: computation/business logic where it makes the code clearer.
- JSP: presentation and safe output; avoid large SQL/Java logic blocks in JSP.
- JavaScript: interactivity, form handling, charts, and optional fetch-based API calls.

If using \`fetch()\`, define explicit request/response contracts and handle failed requests and empty results. Don't invent a functioning \`/api/...\` endpoint unless the corresponding Servlet is implemented.

### Code quality standards

1. Prefer minimal, focused changes over rewrites.
2. Match existing Java packages and project layout.
3. Validate on the server even if JavaScript validation exists.
4. Escape user-supplied text in HTML; protect against XSS.
5. Use prepared statements, not SQL string concatenation.
6. Implement clear success, loading, empty, and error states.
7. Avoid adding heavy dependencies without team agreement.
8. Preserve existing comments, component contracts, and routes where possible.
9. Do not delete or rename files merely to make the structure “cleaner.”
10. Do not claim features are tested when they have only been drafted.

---

## 9. Three-person team ownership

| Person | Primary focus | Suggested branch |
| --- | --- | --- |
| Person 1 | Frontend: JSP, CSS, JavaScript, responsive components | \`feature/frontend\` |
| Person 2 | Backend: Servlets, validation, task/course actions | \`feature/backend\` |
| Person 3 | Database: SQLite schema, JDBC, DAOs, analytics queries | \`feature/database\` |

Agree on URL mappings, data fields and responsibilities **before** editing shared contracts. Use small pull requests and test integration together.

### Git safety

- The StudyFlow GitHub repository is \`ChaoS-SLW/teamwork04\`.
- The school's **Bitbucket repository is separate**. Do not change its \`origin\`, push StudyFlow to it, or interfere with school project files.
- Do not commit generated \`.class\` files, database journals, IDE caches or credentials.
- Never force-push to \`main\` or remove existing history without explicit authorization.

---

## 10. Work instructions for AI coding assistants

When asked to build or improve a StudyFlow feature:

1. **Inspect first.** Read the relevant current controller, JSP, JavaScript, CSS, and DAO files (and database schema, if relevant).
2. **Restate the task.** Identify the exact behavior being added and distinguish existing behavior from proposed changes.
3. **Preserve architecture.** Keep Home = \`StudyController\` + \`index.jsp\`, Dashboard = \`DashboardController\` + \`dashboard.jsp\`; keep Tomcat 9 + \`javax.servlet\` + Bootstrap 6 alpha.
4. **Change only what's necessary.** Do not overwrite unrelated source code or redesign the entire app. If a large refactor seems necessary, explain it and obtain approval first.
5. **Implement one vertical slice.** Example: course form → Servlet → DAO → SQLite → updated course list.
6. **Avoid fake completion.** Clearly mark mock UI data and unfinished code. Don't claim database persistence if only hardcoded Java or JavaScript data exists.
7. **Test honestly.** State what was actually checked, what wasn't run, how to test it in Eclipse/Tomcat, and the expected URL.
8. **Report changes.** List modified files, explain the reason for each change, and note compatibility/security concerns.
9. **Respect Git scope.** Never modify the school's Bitbucket repository. For GitHub, work in a reviewable branch when changing multiple files or implementing a feature.
10. **Follow the user's approval.** If asked only for advice, documentation, or code examples, do not modify application files. When authorized to change files, do not broaden the scope.

### Suggested AI task request

> Read the current \`teamwork04\` source and this README. Implement **[one named feature]** using the existing Java 21 + Tomcat 9 + JSP + Bootstrap 6 + SQLite structure. Preserve the existing code, especially \`StudyController\` → \`index.jsp\` (Home) and \`DashboardController\` → \`dashboard.jsp\` (Dashboard). Before changing anything, identify the exact files to edit and the current limitations. Make only focused changes. Tell me how to test in Eclipse and which parts remain unimplemented.

---

## 11. Definition of done

A feature is complete only when:
- The intended page opens through the correct Servlet URL.
- The UI supports the required interaction and handles empty/invalid input.
- Backend behavior is implemented, not just a clickable button.
- Database-backed changes persist after refresh if persistence is part of the feature.
- Shared components, navigation and mobile layout are not broken.
- The changes are reviewable, and limitations are documented.

**Next recommended implementation target:** correct and finish the separate Home and Dashboard views, then implement Course CRUD as the first full front-to-back feature.
