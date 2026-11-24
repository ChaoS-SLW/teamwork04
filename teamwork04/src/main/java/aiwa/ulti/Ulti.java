package aiwa.ulti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 共通ユーティリティ（DB接続など）
 */
public class Ulti {

	// ★ studyflow.db を置いた場所に合わせて変更してください（Windowsでも / 区切りでOK）
	private static final String DEFAULT_DB_PATH = "C:/Users/B-0345/Documents/b-0345/studyflow.db";

	// JVM引数 -Dstudyflow.db=... で上書きも可能
	private static final String DB_PATH = System.getProperty("studyflow.db", DEFAULT_DB_PATH);
	private static final String URL = "jdbc:sqlite:" + DB_PATH;

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(
					"sqlite-jdbc の jar が WEB-INF/lib にありません: " + e.getMessage());
		}
	}

	private Ulti() {
	}

	public static Connection getConnection() throws SQLException {
		Connection con = DriverManager.getConnection(URL);
		try (Statement st = con.createStatement()) {
			st.execute("PRAGMA foreign_keys = ON");
		}
		return con;
	}

	/** 文字列 → int（変換できなければ def を返す） */
	public static int toInt(String s, int def) {
		if (s == null || s.trim().isEmpty()) {
			return def;
		}
		try {
			return Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
