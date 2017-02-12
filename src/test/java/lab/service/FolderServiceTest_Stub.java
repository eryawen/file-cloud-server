package lab.service;

import lab.daos.FileMetadataDAO;
import lab.daos.FolderMetadataDAO;
import lab.daos.UsersDAO;
import lab.db.tables.pojos.FolderMetadata;
import lab.util.Path;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class FolderServiceTest_Stub {
	private FolderService service;
	private UsersDAO usersDAO;
	private FolderMetadataDAO folderMetadataDao;
	private FileMetadataDAO fileMetadataDAO;
	
	@Before
	public void setup() {
		usersDAO = mock(UsersDAO.class);
		folderMetadataDao = mock(FolderMetadataDAO.class);
		fileMetadataDAO = mock(FileMetadataDAO.class);
		service = new FolderService(folderMetadataDao, fileMetadataDAO, usersDAO);
	}
	
	@Test
	public void renameFolder_shouldChangeSubfolderPath() {
		Path currentPath = new Path("/user/folderToRename");
		Path newPath = new Path("/user/renamed");
		Path subfolderPath = new Path("/user/folderToRename/someFolder");
		
		FolderMetadata folder = new FolderMetadata(2, 1, "folderToRename", currentPath.getPathLower(),
										   currentPath.getPathDisplay(), null, null);
		FolderMetadata subfolder = new FolderMetadata(3, 2, "someFolder", subfolderPath.getPathLower(),
											 subfolderPath.getPathDisplay(), null, null);
		FolderMetadata renamedExpected = new FolderMetadata(folder).setPathDisplay(newPath.getPathDisplay()).setName(
			   newPath.getName()).setPathLower(
			   newPath.getPathLower());
		
		FolderMetadata subFolderRenamed = new FolderMetadata(subfolder).setPathDisplay(
			   "/user/renamed/someFolder").setPathLower(
			   "/user/renamed/somefolder");
		
		when(folderMetadataDao.getFolderMetadataByPath(currentPath.getPathDisplay())).thenReturn(folder);
		when(folderMetadataDao.fetchByParentFolderId(subfolder.getParentFolderId())).thenReturn(
			   Arrays.asList(subfolder));
		
		FolderMetadata renamedActual = service.renameFolderAndFolderContent(currentPath, newPath);
		assertThat(renamedActual.getPathDisplay()).isEqualTo(renamedExpected.getPathDisplay());
		assertThat(renamedActual.getName()).isEqualTo(renamedExpected.getName());
		
//		verify(folderMetadataDao, times(1)).update(Arrays.asList(subFolderRenamed, renamedExpected));
	}
}