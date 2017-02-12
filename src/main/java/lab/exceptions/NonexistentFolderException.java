package lab.exceptions;

import lab.util.Status;

public class NonexistentFolderException extends ServerException {
	public NonexistentFolderException() {
		super("Folder or file doesn't exist in database");
	}
	
	@Override
	public int getStatusCode() {
		return Status.NONEXISTENT_FOLDER;
	}
}
