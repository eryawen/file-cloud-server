package lab.exceptions;

import lab.util.Status;

public class UsernameAlreadyExistsException extends ServerException {
	public UsernameAlreadyExistsException() {
		super("Username already exists");
	}
	
	@Override
	public int getStatusCode() {
		return Status.USER_ALREADY_EXIST;
	}
}
