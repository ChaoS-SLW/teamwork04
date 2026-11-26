package aiwa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import aiwa.entity.User;

public class UserDAO {
	public User findActiveByUsername(String username) throws SQLException {
		String sql = "SELECT user_id,username,email,password_hash,is_active FROM users WHERE username=? AND is_active=1";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, username);
			try (ResultSet rs = statement.executeQuery()) {
				if (!rs.next())
					return null;
				User user = new User();
				user.setUserId(rs.getLong("user_id"));
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setPasswordHash(rs.getString("password_hash"));
				user.setActive(rs.getInt("is_active") == 1);
				return user;
			}
		}
	}

	public long create(User user) throws SQLException {
		String sql = "INSERT INTO users(username,email,password_hash) VALUES(?,?,?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql,
						PreparedStatement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPasswordHash());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next())
					return keys.getLong(1);
			}
		}
		throw new SQLException("The database did not return the new user id");
	}
}
