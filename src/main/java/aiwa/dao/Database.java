package aiwa.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/** SQLite connection and idempotent schema bootstrap. */
public final class Database {

	private static final String LEGACY_EMAIL = "demo@studyflow.local";
	private static volatile String jdbcUrl;

	private Database() { }

	public static synchronized void initialize(ServletContext context) throws ServletException {
		String databasePath = context.getRealPath("/WEB-INF/studyflow.db");
		if (databasePath == null) {
			throw new ServletException("Tomcat must provide a writable application path for WEB-INF/studyflow.db");
		}
		try {
			Class.forName("org.sqlite.JDBC");
			Path path = Path.of(databasePath);
			Files.createDirectories(path.getParent());
			jdbcUrl = "jdbc:sqlite:" + path.toAbsolutePath();
			try (Connection connection = openConnection()) {
				executeSchema(context, connection);
				long legacyUserId = ensureLegacyUser(connection);
				migrateOwnership(connection, "lesson_progress", legacyUserId);
				migrateOwnership(connection, "quiz_results", legacyUserId);
				migrateCourseOwnership(connection);
				createIndexes(connection);
				try (Statement statement = connection.createStatement()) {
					statement.executeUpdate("INSERT OR IGNORE INTO categories(category_name, description) VALUES ('General', 'Your personal study courses')");
				}
			}
		} catch (Exception e) {
			throw new ServletException("Could not initialize the StudyFlow SQLite database", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		if (jdbcUrl == null) {
			throw new SQLException("StudyFlow database has not been initialized by the application listener");
		}
		return openConnection();
	}

	private static Connection openConnection() throws SQLException {
		Connection connection = DriverManager.getConnection(jdbcUrl);
		try (Statement statement = connection.createStatement()) {
			statement.execute("PRAGMA foreign_keys = ON");
			statement.execute("PRAGMA busy_timeout = 5000");
		}
		return connection;
	}

	private static void executeSchema(ServletContext context, Connection connection) throws Exception {
		try (InputStream input = context.getResourceAsStream("/WEB-INF/schema.sql")) {
			if (input == null) {
				throw new ServletException("Missing /WEB-INF/schema.sql");
			}
			String sql;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
				sql = reader.lines().collect(Collectors.joining("\n"));
			}
			try (Statement statement = connection.createStatement()) {
				for (String command : sql.split(";")) {
					if (!command.isBlank()) statement.execute(command);
				}
			}
		}
	}

	private static long ensureLegacyUser(Connection connection) throws SQLException {
		String insert = "INSERT OR IGNORE INTO users(username, email, password_hash) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(insert)) {
			statement.setString(1, "Legacy StudyFlow Data");
			statement.setString(2, LEGACY_EMAIL);
			// Prevent the imported legacy identity from being used as a login account.
			statement.setString(3, "!legacy-data-account-disabled!");
			statement.executeUpdate();
		}
		try (PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM users WHERE email = ?")) {
			statement.setString(1, LEGACY_EMAIL);
			try (ResultSet result = statement.executeQuery()) {
				if (!result.next()) throw new SQLException("Could not preserve the legacy database owner");
				return result.getLong(1);
			}
		}
	}

	private static void migrateCourseOwnership(Connection connection) throws SQLException {
		if (!hasColumn(connection, "courses", "user_id")) {
			try (Statement statement = connection.createStatement()) {
				statement.execute("ALTER TABLE courses ADD COLUMN user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE");
			}
		}
	}

	private static boolean hasColumn(Connection connection, String table, String column) throws SQLException {
		try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
			while (result.next()) if (column.equalsIgnoreCase(result.getString("name"))) return true;
		}
		return false;
	}

	private static void migrateOwnership(Connection connection, String table, long userId) throws SQLException {
		if (hasColumn(connection, table, "user_id")) return;
		String old = table + "_legacy";
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + table + " RENAME TO " + old);
			if ("lesson_progress".equals(table)) {
				statement.execute("CREATE TABLE lesson_progress (progress_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, lesson_id INTEGER NOT NULL, is_completed INTEGER NOT NULL DEFAULT 0 CHECK (is_completed IN (0,1)), completed_at TEXT, UNIQUE(user_id, lesson_id), FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE, FOREIGN KEY(lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE)");
				statement.executeUpdate("INSERT INTO lesson_progress(progress_id,user_id,lesson_id,is_completed,completed_at) SELECT progress_id," + userId + ",lesson_id,is_completed,completed_at FROM " + old);
			} else if ("quiz_results".equals(table)) {
				statement.execute("CREATE TABLE quiz_results (result_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, quiz_id INTEGER NOT NULL, score INTEGER NOT NULL CHECK(score >= 0), total_questions INTEGER NOT NULL CHECK(total_questions > 0), completed_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, CHECK(score <= total_questions), FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE, FOREIGN KEY(quiz_id) REFERENCES quizzes(quiz_id))");
				statement.executeUpdate("INSERT INTO quiz_results(result_id,user_id,quiz_id,score,total_questions,completed_at) SELECT result_id," + userId + ",quiz_id,score,total_questions,completed_at FROM " + old);
			}
			statement.execute("DROP TABLE " + old);
		}
	}

	private static void createIndexes(Connection connection) throws SQLException {
		String[] indexes = {
			"CREATE INDEX IF NOT EXISTS idx_tasks_user_status_due ON tasks(user_id, status, due_at)",
			"CREATE INDEX IF NOT EXISTS idx_study_sessions_user_started ON study_sessions(user_id, started_at)",
			"CREATE INDEX IF NOT EXISTS idx_quiz_results_user_completed ON quiz_results(user_id, completed_at)",
			"DROP INDEX IF EXISTS idx_unique_course",
			"CREATE INDEX IF NOT EXISTS idx_courses_user ON courses(user_id, course_name)",
			"CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_global_course ON courses(category_id, course_name) WHERE user_id IS NULL",
			"CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_user_course ON courses(user_id, category_id, course_name) WHERE user_id IS NOT NULL",
			"CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_chapter_name ON chapters(course_id, chapter_name)",
			"CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_chapter_order ON chapters(course_id, chapter_order)",
			"CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_lesson_title ON lessons(chapter_id, lesson_title)",
			"CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_lesson_order ON lessons(chapter_id, lesson_order)"
		};
		try (Statement statement = connection.createStatement()) {
			for (String index : indexes) statement.execute(index);
		}
	}
}
