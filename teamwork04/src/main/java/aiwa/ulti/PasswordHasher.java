package aiwa.ulti;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password hashing using the JDK's PBKDF2 implementation; no extra library is
 * required.
 */
public final class PasswordHasher {
	private static final int ITERATIONS = 210_000;
	private static final int KEY_BITS = 256;
	private static final int SALT_BYTES = 16;
	private static final SecureRandom RANDOM = new SecureRandom();

	private PasswordHasher() {
	}

	public static String hash(char[] password) {
		byte[] salt = new byte[SALT_BYTES];
		RANDOM.nextBytes(salt);
		byte[] derived = derive(password, salt, ITERATIONS);
		return "pbkdf2-sha256$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$"
				+ Base64.getEncoder().encodeToString(derived);
	}

	public static boolean verify(char[] password, String encoded) {
		if (encoded == null)
			return false;
		try {
			String[] parts = encoded.split("\\$", -1);
			if (parts.length != 4 || !"pbkdf2-sha256".equals(parts[0]))
				return false;
			int iterations = Integer.parseInt(parts[1]);
			if (iterations < 100_000 || iterations > 1_000_000)
				return false;
			byte[] salt = Base64.getDecoder().decode(parts[2]);
			byte[] expected = Base64.getDecoder().decode(parts[3]);
			if (salt.length < 16 || expected.length < 16)
				return false;
			return MessageDigest.isEqual(expected, derive(password, salt, iterations));
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static byte[] derive(char[] password, byte[] salt, int iterations) {
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
		try {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (Exception e) {
			throw new IllegalStateException("Password hashing is unavailable", e);
		} finally {
			spec.clearPassword();
		}
	}
}
