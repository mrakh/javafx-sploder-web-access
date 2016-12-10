package utils;

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

public class StringUtils {
	private static final String SALT = "098e7fe5f0e70987fadfe00e70897dcd";
	private static final MessageDigest DIGESTER = md5Digester();
	
	public static String sign(String str) {
		try {
			String saltedStr = str + SALT;
			DIGESTER.reset();
			byte[] result = DIGESTER.digest(saltedStr.getBytes());
			return DatatypeConverter.printHexBinary(result).toLowerCase();
		} catch(Exception e) {
			throw new RuntimeException("Could not digest.");
		}
	}
	
	public static String str(int i) {
		return Integer.toString(i);
	}
	
	private static MessageDigest md5Digester() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch(Exception e) {
			return null;
		}
	}
}
