package lab.controller;

import lab.db.tables.pojos.FolderMetadata;
import lab.exceptions.AuthorisationRequiredException;
import lab.exceptions.InvalidPathParameterException;
import lab.exceptions.WrongParamFormatException;
import lab.service.FileService;
import lab.service.FolderService;
import lab.service.UserService;
import lab.util.FolderContent;
import lab.util.Helper;
import lab.util.Path;
import lab.util.Status;
import spark.Request;
import spark.Response;

import static org.valid4j.Validation.otherwiseThrowing;
import static org.valid4j.Validation.validate;

public class FolderController {
	private FolderService folderService;
	private FileService fileService;
	private UserService userService;
	
	public FolderController(FolderService folderService, FileService fileService, UserService userService) {
		this.folderService = folderService;
		this.fileService = fileService;
		this.userService = userService;
	}
	
	public Object handleGetFolderMetadata(Request request, Response response) {
		Path path = new Path(request.attribute("path"));
		
		validate(folderService.pathRepresentsFolder(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		response.status(Status.SUCCESSFUL_OPERATION);
		return folderService.getFolderMetadata(path);
	}
	
	public Object handleListFolderContent(Request request, Response response) {
		Path path = new Path(request.attribute("path"));
		String recursive = request.queryParams("recursive");
		
		validate(folderService.pathRepresentsFolder(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(Helper.stringContainsBoolean(recursive),
			    otherwiseThrowing(WrongParamFormatException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FolderContent folderContent = folderService.getFolderContent(path, recursive);
		response.status(Status.SUCCESSFUL_OPERATION);
		return folderContent;
	}
	
	public Object handleCreateDirectory(Request request, Response response) {
		Path path = new Path(request.attribute("path"));
		
		validate(folderService.parentPathIsFolder(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(!folderService.pathRepresentsFolder(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FolderMetadata createdDirectory = folderService.addFolderToDatabase(path);
		response.status(Status.CREATED);
		return createdDirectory;
	}
	
	public Object handleDeleteFolder(Request request, Response response) {
		Path path = new Path(request.attribute("path"));
		
		validate(folderService.pathRepresentsFolder(path),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(!path.isMainUserPath(),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), path),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FolderMetadata deletedFolder = folderService.deleteFolder(path);
		response.status(Status.DELETED);
		return deletedFolder;
	}
	
	public Object handleRenameFolder(Request request, Response response) {
		Path currentPath = new Path(request.attribute("path"));
		String newName = request.attribute("newName");
		Path newPath = new Path(String.format("%s/%s", currentPath.getParentPath(), newName));
		
		validate(folderService.pathRepresentsFolder(currentPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(folderService.parentPathIsFolder(newPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(!pathExistsInDatabase(newPath), otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), newPath),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), currentPath),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FolderMetadata renamedFolder = folderService.renameFolderAndFolderContent(currentPath, newPath);
		response.status(Status.SUCCESSFUL_OPERATION);
		return renamedFolder;
	}
	
	public Object handleMoveFolder(Request request, Response response) {
		Path currentPath = new Path(request.attribute("path"));
		Path newPath = new Path(request.attribute("newPath"));
		
		validate(folderService.pathRepresentsFolder(currentPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(!pathExistsInDatabase(newPath), otherwiseThrowing(InvalidPathParameterException.class));
		validate(folderService.pathRepresentsFolder(currentPath),
			    otherwiseThrowing(InvalidPathParameterException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), newPath),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		validate(userService.userHasAccessToFile(request.cookie("session"), currentPath),
			    otherwiseThrowing(AuthorisationRequiredException.class));
		
		FolderMetadata movedFolder = folderService.moveFolderAndFolderContent(currentPath, newPath);
		response.status(Status.SUCCESSFUL_OPERATION);
		return movedFolder;
	}
	
	private boolean pathExistsInDatabase(Path path) {
		return (fileService.pathRepresentsFile(path) || folderService.pathRepresentsFolder(path));
	}
}
