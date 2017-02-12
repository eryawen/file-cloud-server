package lab.controller;

import com.google.gson.Gson;
import lab.db.tables.pojos.FileMetadata;
import lab.exceptions.AuthorisationRequiredException;
import lab.exceptions.InvalidPathParameterException;
import lab.service.FileService;
import lab.service.FolderService;
import lab.service.UserService;
import lab.util.Path;
import lab.util.Status;
import spark.Request;
import spark.Response;

import static org.valid4j.Validation.otherwiseThrowing;
import static org.valid4j.Validation.validate;

public class FileController {
	final Gson gson = new Gson();
	
	private FolderService folderService;
	private FileService fileService;
	private UserService userService;
	
	public FileController(FolderService folderService, FileService fileService, UserService userService) {
		this.folderService = folderService;
		this.fileService = fileService;
		this.userService = userService;
	}
	
	public Object handleUploadFile(Request request, Response response) {
		byte[] fileContent = request.bodyAsBytes();
		Path path = new Path(request.attribute("path"));
		
		validate(folderService.parentPathIsFolder(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(!pathExistsInDatabase(path), otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FileMetadata retrieved = fileService.addFileToDatabase(path, fileContent);
		response.status(Status.UPLOADED);
		return retrieved;
	}
	
	public Object handleDownloadFile(Request request, Response response) {
		Path path = new Path(request.attribute("path"));
		
		validate(fileService.pathRepresentsFile(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		response.header("x-file-metadata", gson.toJson(fileService.getFileMetadata(path)));
		response.status(Status.SUCCESSFUL_OPERATION);
		return fileService.getFileContent(path);
	}
	
	public Object handleDeleteFile(Request request, Response response) {
		Path path = new Path(request.attribute("path"));
		
		validate(fileService.pathRepresentsFile(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FileMetadata fileToDelete = fileService.deleteFile(path);
		response.status(Status.DELETED);
		return fileToDelete;
	}
	
	public Object handleGetFileMetadata(Request request, Response response) {
		Path path = new Path(request.attribute("path"));
		
		validate(fileService.pathRepresentsFile(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		response.status(Status.SUCCESSFUL_OPERATION);
		return fileService.getFileMetadata(path);
	}
	
	public Object handleRenameFile(Request request, Response response) {
		Path currentPath = new Path(request.attribute("path"));
		String newName = request.attribute("newName");
		Path newPath = new Path(String.format("%s/%s", currentPath.getParentPath(), newName));
		
		validate(fileService.pathRepresentsFile(currentPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(pathExistsInDatabase(newPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), currentPath),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FileMetadata renamedFile = fileService.renameFile(currentPath, newPath);
		response.status(Status.SUCCESSFUL_OPERATION);
		return renamedFile;
	}
	
	public Object handleMoveFile(Request request, Response response) {
		Path currentPath = new Path(request.attribute("path"));
		Path newPath = new Path(request.attribute("newPath"));
		
		validate(fileService.pathRepresentsFile(currentPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(!fileService.pathRepresentsFile(newPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), currentPath),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FileMetadata movedFile = fileService.moveFile(currentPath, newPath);
		response.status(Status.MOVED);
		return movedFile;
	}
	
	private boolean pathExistsInDatabase(Path path) {
		return (fileService.pathRepresentsFile(path) || folderService.pathRepresentsFolder(path));
	}
}
