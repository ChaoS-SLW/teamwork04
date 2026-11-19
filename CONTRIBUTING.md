# Contributing to StudyFlow

This is a small, three-person student project. Prefer short, reviewable pull requests over large unreviewed changes.

## Branches and ownership

| Workstream | Suggested branch | Main paths |
| --- | --- | --- |
| Frontend | `feature/frontend` | `teamwork04/src/main/webapp/` |
| Java backend | `feature/backend` | `teamwork04/src/main/java/aiwa/controller/`, `entity/` |
| Database and analytics | `feature/database` | `teamwork04/src/main/java/aiwa/dao/`, `service/`, `schema.sql` when introduced |

Coordinate before editing shared files such as `header.jsp`, route mappings, database contracts, or `index.jsp`.

## Everyday workflow

1. Fetch and pull the latest `main` in GitHub Desktop.
2. Create a branch for a specific feature or fix (e.g. `feature/course-create`).
3. Make one focused change. Keep JSP views in `WEB-INF/views`, reusable fragments in `WEB-INF/views/components`, public assets in `assets/`.
4. Run or manually test the affected page on Tomcat 9. If it could not be tested, say why.
5. Commit with a clear message (e.g. `feat: add course creation form` or `fix: correct dashboard navigation`).
6. Push the branch and open a PR against `main`.
7. Have a teammate review it and test the merged `main` after integration.

## Review checklist

- Does the change preserve Java 21 + Tomcat 9 + `javax.servlet` compatibility?
- Are browser links prefixed by `request.getContextPath()` where appropriate?
- Does the Servlet mapping agree with the URL in the JSP?
- Are all user inputs validated on the server before database writes?
- Is SQL executed with prepared statements, and is guest ownership validated where applicable?
- Are generated build files, local databases, credentials and unrelated formatting changes excluded?
- Is the PR description explicit about what is complete and what is mock data?

## Database contract

The team should agree on schema and API request/response fields before parallel implementation. Do not use committed `studyflow.db` as the long-term data-delivery mechanism. Prefer a versioned SQL schema plus local initialization instructions.

## Merge discipline

Keep `main` usable. Review before merging; avoid force-pushes to shared branches. No need for elaborate release processes for a one-week school project.

## Local setup

See [docs/SETUP.md](docs/SETUP.md).
