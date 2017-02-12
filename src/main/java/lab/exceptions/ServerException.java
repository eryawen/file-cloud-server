package lab.exceptions;

public abstract class ServerException extends RuntimeException {
	public abstract int getStatusCode();
	
	public ServerException(String msg) {
		super(msg);
	}
}

