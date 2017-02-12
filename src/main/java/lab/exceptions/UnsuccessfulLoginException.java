package lab.exceptions;

import lab.util.Status;

public class UnsuccessfulLoginException extends ServerException {
	public UnsuccessfulLoginException() {
		super("Unsuccessful login");
	}
	
	@Override
	public int getStatusCode() {
		return Status.UNSUCCESSFUL_LOGIN;
	}
}
