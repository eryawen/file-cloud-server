package lab.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelperTest {
	@Test
	public void decodeUsernameAndPassword() throws Exception {

		String[] user = Helper.decode("Basic TW9uaWthOnBhc3M=");
		assertThat(user[0].equals("Monika"));
		assertThat(user[1].equals("pass"));
	}
}