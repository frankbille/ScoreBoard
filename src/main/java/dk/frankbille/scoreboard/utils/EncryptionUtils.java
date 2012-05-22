package dk.frankbille.scoreboard.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.wicket.util.crypt.Base64;

public final class EncryptionUtils {

	public static String md5Encode(String string) {
		String md5String = null;
		try {
			byte[] bytesOfMessage = string.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			md5String = Base64.encodeBase64URLSafeString(thedigest);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return md5String;
	}

	private EncryptionUtils() {
	}

}
