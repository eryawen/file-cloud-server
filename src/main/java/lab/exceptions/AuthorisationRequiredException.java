package lab.exceptions;

import lab.util.Status;

public class AuthorisationRequiredException extends ServerException {
	@Override
	public int getStatusCode() {
		return Status.AUTHORISATION_REQUIRED;
	}
	
	public AuthorisationRequiredException() {
		super("Authorisation required");
	}
}
