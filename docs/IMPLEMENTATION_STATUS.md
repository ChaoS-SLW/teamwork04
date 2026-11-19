# Implementation Status and Next Steps

Snapshot from the `main` branch reviewed 2026-11-20. This document is a checklist, not a claim that the features already work.

## Verified source state

| Area | Current state | Next step |
| --- | --- | --- |
| `index.jsp` | Displays a JSP/Tomcat connection test | Replace with the decided landing/redirect flow |
| `DashboardController` | Mapped at `/DashboardController`, forwards to dashboard JSP | Populate the dashboard model |
| `dashboard.jsp` | Includes shared head and header, with no dashboard content yet | Add the requested dashboard main content and footer |
| `StudyController` | Mapped at `/StudyController`, forwards to `index.jsp` | Clarify whether this route remains needed |
| `CoursesController` | Placeholder GET/POST | Implement course list/create/update/delete |
| `TaskController` | Placeholder GET/POST | Implement task CRUD |
| `ActivityController` | Placeholder GET/POST | Implement study sessions/activity endpoints |
| DAOs/entities/services | Mostly empty Java classes | Define contracts and implement persistence |
| `coursesjsp.jsp`, `tasks.jsp`, `analytics.jsp` | Placeholder JSPs | Implement screens, review course file naming |
| `api.js`, `app.js`, `tasks.js`, `dashboard.js`, `analytics.js` | Placeholder JS files | Build UI and connect real Servlet responses |
| SQLite | `studyflow.db` tracked; SQLite driver JAR present | Provide `schema.sql` and local setup |
| Build | Compiled classes tracked in `teamwork04/build/` | Stop tracking generated output; add reproducible setup |

## Confirmed navigation mismatch

- Header logo points to `/DashboardController`.
- Header/footer navigation point to `/dashboard`, `/tasks`, `/analytics`.
- Existing Servlet annotations map `/DashboardController`, `/TaskController`, `/ActivityController`.

Either update links to the current controller URLs or add explicit Servlet mappings for the clean URLs. Avoid mixing both patterns accidentally.

## One-week priorities

### P0 — Minimum usable demo
- [ ] Make landing and Dashboard route consistent
- [ ] Complete dashboard layout (with clearly marked mock data until backend is ready)
- [ ] Implement course CRUD
- [ ] Implement task CRUD with course relationship and completion state
- [ ] Create schema / initialization instructions and use SQLite via JDBC
- [ ] Verify a page refresh preserves real task/course changes

### P1 — Study features
- [ ] Log study sessions
- [ ] Track total study minutes and completed tasks
- [ ] Render activity heatmap from saved activity
- [ ] Show a 7-day trend chart

### P2 — If time remains
- [ ] 30-day comparisons, XP and streaks
- [ ] Mobile polish and dark mode
- [ ] Reusable validation/error/empty states

## Presentation check

Demonstrate `create course → add task → complete task → refresh → see saved progress`. If multiple classmates use the same server, plan session-based ownership or clearly communicate that the demo uses one shared fictional profile; do not claim isolation before it is implemented.
