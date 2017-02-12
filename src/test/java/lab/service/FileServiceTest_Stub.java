package lab.service;

import lab.daos.FileContentDAO;
import lab.daos.FileMetadataDAO;
import lab.daos.FolderMetadataDAO;
import lab.daos.UsersDAO;
import lab.db.tables.pojos.FileMetadata;
import lab.db.tables.pojos.FolderMetadata;
import lab.db.tables.pojos.Users;
import lab.util.Path;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FileServiceTest_Stub {
	private FileService service;
	private FileMetadataDAO fileMetadataDAO;
	private UsersDAO usersDAO;
	private FolderMetadataDAO folderMetadataDAO;
	private FileContentDAO fileContentMetadataDAO;
	
	@Before
	public void setup() {
		usersDAO = mock(UsersDAO.class);
		fileContentMetadataDAO = mock(FileContentDAO.class);
		fileMetadataDAO = mock(FileMetadataDAO.class);
		folderMetadataDAO = mock(FolderMetadataDAO.class);
		service = new FileService(fileMetadataDAO, folderMetadataDAO, fileContentMetadataDAO, usersDAO);
	}
	
	@Test
	public void storeFileMetadata_ShouldCallDaoMethod() throws Exception {
		Path path = new Path("/name/file.txt");
		int size = 20;
		FileMetadata fileMetadata = new FileMetadata(2, "file.txt", "/name/file.txt", null, 1, size, null, null, 1);
		
		when(folderMetadataDAO.getFolderMetadataByPath("/name")).thenReturn(
			   new FolderMetadata(1, null, null, null, null, null, null));
		when(fileMetadataDAO.store(fileMetadata)).thenReturn(fileMetadata);
		when(usersDAO.getUserByName(anyString())).thenReturn(new Users(1, path.getName(), path.getName(), "pass"));
		
		service.storeFileMetadata(path, size);
		verify(fileMetadataDAO, times(1)).store(any(FileMetadata.class));
	}
}