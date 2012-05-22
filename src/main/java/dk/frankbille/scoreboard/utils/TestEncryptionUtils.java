package dk.frankbille.scoreboard.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestEncryptionUtils {

	@Test
	public void testMD5Encode() {
		String md5One = EncryptionUtils.md5Encode("Some string");
		String md5Two = EncryptionUtils.md5Encode("Some string");
		assertEquals(md5One, md5Two);
	}

}
