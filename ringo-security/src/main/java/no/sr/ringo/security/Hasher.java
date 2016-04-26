package no.sr.ringo.security;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Custom MD5 + salt implementation
 * Crates hash of raw password using salt (originatorId and registrationDate).
 * The implementation is written in the exactly same way as in 
 * SpringSecurity MessageDigestPasswordEncoder to make it compliant with one
 * used in UserService when creating users
 * 
 * @author adam
 *
 */
public class Hasher {

	private final String algorithm = "MD5";
	
	/**
	 * Constructor with hashing algorithm parameter
	 */
	public Hasher() {
	}
	
	/**
	 * Creates hash from rawPassword and salt
	 * @param rawPassword
	 * @param salt
	 * @return
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws java.io.UnsupportedEncodingException
	 */
	public HashedPassword hash(String rawPassword, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String result = null;
		MessageDigest md = MessageDigest.getInstance(algorithm);
		String saltedPassword = createSaltedPassword(rawPassword, salt);
		md.update(saltedPassword.getBytes("UTF-8"));
		result = new String(Hex.encodeHex(md.digest()));
		return new HashedPassword(result);
	}
	
	/**
	 * Creates salted password as rawPassword + { + salt + } as implemented in SpringSecurity
	 * 
	 * @param rawPassword
	 * @param salt
	 * @return
	 */
	private String createSaltedPassword(String rawPassword, String salt) {
		return rawPassword + "{" + salt + "}";
	}

}
