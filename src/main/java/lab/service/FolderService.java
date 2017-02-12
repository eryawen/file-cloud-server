package lab.service;

import lab.daos.FileMetadataDAO;
import lab.daos.FolderMetadataDAO;
import lab.daos.UsersDAO;
import lab.db.tables.pojos.FileMetadata;
import lab.db.tables.pojos.FolderMetadata;
import lab.db.tables.pojos.Users;
import lab.util.FolderContent;
import lab.util.Path;

import java.util.ArrayList;
import java.util.List;

import static lab.db.Tables.FOLDER_METADATA;
import static lab.util.Helper.getTime;

public class FolderService {
	private final FolderMetadataDAO folderMetadataDAO;
	private final FileMetadataDAO fileMetadataDAO;
	private final UsersDAO usersDAO;
	
	public FolderService(FolderMetadataDAO folderMetadataDAO, FileMetadataDAO fileMetadataDAO, UsersDAO usersDAO) {
		this.folderMetadataDAO = folderMetadataDAO;
		this.fileMetadataDAO = fileMetadataDAO;
		this.usersDAO = usersDAO;
	}
	
	public FolderMetadata addFolderToDatabase(Path path) {
		FolderMetadata parentFolder = folderMetadataDAO.getFolderMetadataByPath(path.getParentPath());
		Users user = usersDAO.getUserByName(path.getOwnerName());
		FolderMetadata folderMetadata = new FolderMetadata(null, parentFolder.getFolderId(),
												 path.getName(),
												 path.getPathLower(),
												 path.getPathDisplay(),
												 getTime(), user.getId());
		return folderMetadataDAO.store(folderMetadata);
	}
	
	public void createUserFolder(Integer id) {
		String username = usersDAO.fetchOneById(id).getUserName();
		String path = String.format("/%s", username);
		FolderMetadata userFolder = new FolderMetadata(null, null, username,
											  path.toLowerCase(),
											  path,
											  getTime(), id);
		folderMetadataDAO.store(userFolder);
	}
	
	public FolderMetadata deleteFolder(Path path) {
		FolderMetadata folderToDelete = folderMetadataDAO.getFolderMetadataByPath(path.getPathDisplay());
		folderMetadataDAO.delete(folderToDelete);
		return folderToDelete;
	}
	
	public FolderMetadata getFolderMetadata(Path path) {
		return folderMetadataDAO.getFolderMetadataByPath(path.getPathDisplay());
	}
	
	public FolderContent getFolderContent(Path path, String recursive) {
		FolderMetadata listedFolder = folderMetadataDAO.getFolderMetadataByPath(path.getPathDisplay());
		return buildFolderContent(listedFolder, recursive);
	}
	
	private FolderContent buildFolderContent(FolderMetadata folderMetadata, String recursive) {
		Integer folderId = folderMetadata.getFolderId();
		List<FileMetadata> filesWithinPath = fileMetadataDAO.fetchByEnclosingFolderId(folderId);
		List<FolderContent> foldersWithinPath = new ArrayList<>();
		List<FolderMetadata> foldersDirectlyInListedFolder = folderMetadataDAO.fetchByParentFolderId(folderId);
		
		if (recursive.equals("false")) {
			foldersDirectlyInListedFolder.forEach(
				   folder -> foldersWithinPath.add(new FolderContent(folder, new ArrayList<>(),
														   new ArrayList<>())));
		} else {
			foldersDirectlyInListedFolder.forEach(
				   folder -> foldersWithinPath.add(buildFolderContent(folder, "true")));
		}
		
		return new FolderContent(folderMetadata, foldersWithinPath, filesWithinPath);
	}
	
	private void updatePathsWithinFolder(FolderContent folderContent, Path currentPath, Path newPath) {
		folderContent.getFiles().forEach(file -> FileService.updatePath(currentPath, newPath, file));
		fileMetadataDAO.update(folderContent.getFiles());
		folderContent.getFolders().forEach(folder -> {
			updatePathsWithinFolder(folder, currentPath, newPath);
			updatePath(currentPath, newPath, folder.getRoot());
			folderMetadataDAO.update(folder.getRoot());
		});
	}
	
	private void updatePath(Path oldPath, Path renamedPath, FolderMetadata folderWithinPath) {
		Path newFolderPath = Path.getUpdatedPath(oldPath, renamedPath, folderWithinPath.getPathDisplay());
		folderWithinPath.setPathDisplay(newFolderPath.getPathDisplay());
		folderWithinPath.setPathLower(newFolderPath.getPathLower());
		folderWithinPath.setName(newFolderPath.getName());
	}
	
	public FolderMetadata renameFolderAndFolderContent(Path currentPath, Path newPath) {
		FolderMetadata folderToRename = folderMetadataDAO.getFolderMetadataByPath(currentPath.getPathDisplay());
		FolderContent folderContent = buildFolderContent(folderToRename, "true");
		updatePathsWithinFolder(folderContent, currentPath, newPath);
		updatePath(currentPath, newPath, folderToRename);
		folderMetadataDAO.update(folderToRename);
		return folderToRename;
	}
	
	public FolderMetadata moveFolderAndFolderContent(Path parsedCurrentPath, Path parsedNewPath) {
		FolderMetadata folderToMove = renameFolderAndFolderContent(parsedCurrentPath, parsedNewPath);
		Integer parentFolderIdAfterMove = folderMetadataDAO.getFolderMetadataByPath(
			   parsedNewPath.getParentPath()).getFolderId();
		folderToMove.setParentFolderId(parentFolderIdAfterMove);
		folderMetadataDAO.update(folderToMove);
		return folderToMove;
	}
	
	public boolean pathRepresentsFolder(Path path) {
		return folderMetadataDAO.fetchOptional(FOLDER_METADATA.PATH_DISPLAY, path.getPathDisplay()).isPresent();
	}
	
	public boolean parentPathIsFolder(Path path) {
		return folderMetadataDAO.fetchOptional(FOLDER_METADATA.PATH_DISPLAY, path.getParentPath()).isPresent();
	}
}
