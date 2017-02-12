package lab.service;

import lab.daos.FileContentDAO;
import lab.daos.FileMetadataDAO;
import lab.daos.FolderMetadataDAO;
import lab.daos.UsersDAO;
import lab.db.tables.pojos.FileContents;
import lab.db.tables.pojos.FileMetadata;
import lab.db.tables.pojos.FolderMetadata;
import lab.db.tables.pojos.Users;
import lab.util.Path;

import static lab.util.Helper.getTime;

public class FileService {
	private FileMetadataDAO fileMetadataDAO;
	private FolderMetadataDAO folderMetadataDAO;
	private FileContentDAO fileContentDAO;
	private UsersDAO usersDAO;
	
	public FileService(FileMetadataDAO fileMetadataDAO, FolderMetadataDAO folderMetadataDAO,
				    FileContentDAO fileContentDAO,
				    UsersDAO usersDAO) {
		this.fileMetadataDAO = fileMetadataDAO;
		this.folderMetadataDAO = folderMetadataDAO;
		this.fileContentDAO = fileContentDAO;
		this.usersDAO = usersDAO;
	}
	
	public FileMetadata addFileToDatabase(Path path, byte[] fileContent) {
		FileMetadata retrieved = storeFileMetadata(path, fileContent.length);
		FileContents fileContents = new FileContents(retrieved.getFileId(), fileContent);
		fileContentDAO.insert(fileContents);
		return retrieved;
	}
	
	public FileMetadata storeFileMetadata(Path path, int fileSize) {
		FolderMetadata parentFolder = folderMetadataDAO.getFolderMetadataByPath(path.getParentPath());
		Users user = usersDAO.getUserByName(path.getOwnerName());
		String time = getTime();
		FileMetadata fileMetadata = new FileMetadata(null, path.getName(),
											path.getPathLower(),
											path.getPathDisplay(),
											parentFolder.getFolderId(),
											fileSize, time, time,
											user.getId());
		return fileMetadataDAO.store(fileMetadata);
	}
	
	public FileMetadata getFileMetadata(Path path) {
		return fileMetadataDAO.getFileMetadataByPath(path.getPathDisplay());
	}
	
	public byte[] getFileContent(Path path) {
		return fileContentDAO.getContentByPath(path.getPathDisplay());
	}
	
	public FileMetadata deleteFile(Path path) {
		FileMetadata fileToDelete = fileMetadataDAO.getFileMetadataByPath(path.getPathDisplay());
		fileMetadataDAO.delete(fileToDelete);
		return fileToDelete;
	}
	
	public FileMetadata renameFile(Path currentPath, Path newPath) {
		FileMetadata fileToRename = fileMetadataDAO.getFileMetadataByPath(currentPath.getPathDisplay());
		updatePath(currentPath, newPath, fileToRename);
		fileMetadataDAO.update(fileToRename);
		return fileToRename;
	}
	
	public FileMetadata moveFile(Path currentPath, Path newPath) {
		FileMetadata fileToMove = getFileMetadata(currentPath);
		updatePath(currentPath, newPath, fileToMove);
		Integer parentFolderIdAfterMoving = folderMetadataDAO.getFolderMetadataByPath(
			   newPath.getParentPath()).getFolderId();
		fileToMove.setEnclosingFolderId(parentFolderIdAfterMoving);
		fileMetadataDAO.update(fileToMove);
		return fileToMove;
	}
	
	public static void updatePath(Path oldPath, Path renamedPath, FileMetadata fileWithinPath) {
		Path newPath = Path.getUpdatedPath(oldPath, renamedPath, fileWithinPath.getPathDisplay());
		fileWithinPath.setPathDisplay(newPath.getPathDisplay());
		fileWithinPath.setPathLower(newPath.getPathLower());
		fileWithinPath.setName(newPath.getName());
	}
	
	public boolean pathRepresentsFile(Path path) {
		return fileMetadataDAO.isPathInUseByFile(path);
	}
}
