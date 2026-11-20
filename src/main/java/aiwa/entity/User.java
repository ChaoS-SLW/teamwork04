package aiwa.entity;

public class User {
	private long userId;
	private String username;
	private String email;
	private String passwordHash;
	private boolean active;

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
}
