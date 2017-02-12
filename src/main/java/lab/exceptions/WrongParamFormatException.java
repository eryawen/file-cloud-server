package lab.exceptions;

import lab.util.Status;

public class WrongParamFormatException extends ServerException {
	public WrongParamFormatException() {
		super("Wrong paramether format");
	}
	
	@Override
	public int getStatusCode() {
		return Status.WRONG_PARAM_FORMAT;
	}
}
