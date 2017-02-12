package lab.exceptions;

import lab.util.Status;

public class InvalidPathParameterException extends ServerException {
	
	public InvalidPathParameterException() {
		super("Invalid path parameter");
	}
	
	@Override
	public int getStatusCode() {
		return Status.INVALID_PATH;
	}
}