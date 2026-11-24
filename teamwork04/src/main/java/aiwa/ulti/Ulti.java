package aiwa.ulti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Ulti {
	private static final String DB_PATH = resolveDatabasePath();
	private static final String URL = "jdbc:sqlite:" + DB_PATH;

	private static String resolveDatabasePath() {
		String path = System.getProperty("studyflow.db");
		if (path == null || path.trim().isEmpty()) path = System.getenv("STUDYFLOW_DB");
		if (path == null || path.trim().isEmpty()) path = "WEB-INF/studyflow.db";
		return path;
	}

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(
					"sqlite-jdbc の jar が WEB-INF/lib にありません: " + e.getMessage());
		}
	}

	private Ulti() { }

	public static Connection getConnection() throws SQLException {
		Connection con = DriverManager.getConnection(URL);
		try (Statement st = con.createStatement()) {
			st.execute("PRAGMA foreign_keys = ON");
			st.execute("PRAGMA busy_timeout = 5000");
		}
		return con;
	}

	public static int toInt(String s, int def) {
		if (s == null || s.trim().isEmpty()) return def;
		try {
			return Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
