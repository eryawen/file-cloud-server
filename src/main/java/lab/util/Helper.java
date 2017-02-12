package lab.util;

import org.apache.commons.codec.binary.Base64;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class Helper {
	public static String getTime() {
		return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
	
	public static long getTimeDifferenceInSeconds(String time) {
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return ChronoUnit.SECONDS.between(zonedDateTime, ZonedDateTime.now());
	}
	
	public static boolean stringContainsBoolean(String param) {
		return (param.equals("true") || param.equals("false"));
	}
	
	public static String[] decode(String encodedString) {
		encodedString = encodedString.replaceFirst("Basic ", "");
		final byte[] decodedBytes = Base64.decodeBase64(encodedString.getBytes());
		final String pair = new String(decodedBytes);
		final String[] userDetails = pair.split(":", 2);
		return userDetails;
	}
	
	public static String generateRandomString() {
		Random random = new Random();
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		return Base64.encodeBase64String(bytes).substring(0, 16);
	}
}
