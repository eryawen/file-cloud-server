package lab.exceptions;

import static lab.util.Status.SESSION_EXPIRED;

public class SessionExpiredException extends ServerException {
	@Override
	public int getStatusCode() {
		return SESSION_EXPIRED;
	}
	
	public SessionExpiredException() {
		super("Session expired");
	}
}
