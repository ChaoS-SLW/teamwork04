package aiwa.tools;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/** Prints non-sensitive account fields from the selected StudyFlow SQLite file. */
public final class CheckUsers {
    private CheckUsers() { }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java aiwa.tools.CheckUsers <path-to-studyflow.db>");
            System.exit(2);
        }

        Path databasePath = Path.of(args[0]).toAbsolutePath().normalize();
        String url = "jdbc:sqlite:" + databasePath;
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA query_only = ON");
            try (ResultSet rows = statement.executeQuery(
                    "SELECT user_id, username, email, created_at, is_active "
                    + "FROM users ORDER BY user_id DESC")) {
                int count = 0;
                System.out.printf("%-8s %-24s %-36s %-22s %s%n",
                        "user_id", "username", "email", "created_at", "is_active");
                while (rows.next()) {
                    count++;
                    System.out.printf("%-8d %-24s %-36s %-22s %d%n",
                            rows.getLong("user_id"), rows.getString("username"),
                            rows.getString("email"), rows.getString("created_at"),
                            rows.getInt("is_active"));
                }
                if (count == 0) System.out.println("No user rows found in: " + databasePath);
                else System.out.println(count + " user row(s) found.");
            }
        }
    }
}
