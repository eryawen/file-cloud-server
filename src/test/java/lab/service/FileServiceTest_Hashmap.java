package lab.service;

import lab.db.tables.pojos.FileMetadata;
import lab.db.tables.pojos.FolderMetadata;
import lab.db.tables.pojos.Users;
import lab.service.daosHashmapImpl.FileContentDaoHashMapImpl;
import lab.service.daosHashmapImpl.FileMetadataDaoHashMapImpl;
import lab.service.daosHashmapImpl.FolderMetadataDaoHashMapImpl;
import lab.service.daosHashmapImpl.UserDaoHashMapImpl;
import lab.util.Path;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileServiceTest_Hashmap {
	private FileService service;
	private UserDaoHashMapImpl usersDAO;
	private FileMetadataDaoHashMapImpl fileMetadataDAO;
	private FolderMetadataDaoHashMapImpl folderMetadataDAO;
	private FileContentDaoHashMapImpl fileContentMetadataDAO;
	
	@Before
	public void setUp() {
		usersDAO = new UserDaoHashMapImpl();
		fileMetadataDAO = new FileMetadataDaoHashMapImpl();
		folderMetadataDAO = new FolderMetadataDaoHashMapImpl();
		fileContentMetadataDAO = new FileContentDaoHashMapImpl();
		
		service = Mockito.spy(new FileService(fileMetadataDAO, folderMetadataDAO, fileContentMetadataDAO, usersDAO));
		usersDAO.store(new Users(null, "name", "name", "pass"));
		folderMetadataDAO.store(new FolderMetadata(null, null, "/name", "/name", "/name", null, 1));
	}
	
	@Test
	public void addFileToDatabase() throws Exception {
		Path path = new Path("/name/file.txt");
		int size = 20;
		FileMetadata retrieved = service.addFileToDatabase(path, new byte[size]);
		
		assertThat(fileMetadataDAO.findAll()).asList().hasSize(1);
		assertThat(retrieved.getFileId()).isPositive();
		assertThat(retrieved.getSize().equals(size));
		assertThat(retrieved.getPathDisplay().equals(path.getPathDisplay()));
		assertThat(fileContentMetadataDAO.findAll().size() == 1);
		
		Mockito.verify(service).storeFileMetadata(path, size);
	}
	
	@Test
	public void deleteFile() throws Exception {
		Path path = new Path("/name/file.txt");
		int size = 20;
		service.addFileToDatabase(path, new byte[size]);
		service.deleteFile(path);
		
		assertThat(fileMetadataDAO.findAll()).asList().isEmpty();
	}
	
	@Test
	public void renameFile() throws Exception {
		Path path = new Path("/name/file.txt");
		Path newPath = new Path("/name/changed.txt");
		int size = 20;
		service.addFileToDatabase(path, new byte[size]);
		FileMetadata retrieved = service.renameFile(path, newPath);
		
		assertThat(retrieved.getPathDisplay().equals(newPath));
	}
	
	@Test
	public void moveFile() throws Exception {
		folderMetadataDAO.store(new FolderMetadata(null, null, "/user", "/user", "/user", null, 1));
		Path path = new Path("/name/file.txt");
		Path newPath = new Path("/user/file.txt");
		int size = 20;
		service.addFileToDatabase(path, new byte[size]);
		FileMetadata retrieved = service.moveFile(path, newPath);
		
		assertThat(retrieved.getPathDisplay().equals(newPath));
	}
}

/*
	   Mockito.when(fileContentMetadataDAO.fetchOneByFileId(Mockito.anyInt())).thenCallRealMethod();
        doAnswer(new CallsRealMethods()).when(fileContentMetadataDAO)
                                        .insert(any(FileContents.class));

        Mockito.when(fileMetadataDAO.store(Mockito.any(FileMetadata.class))).thenCallRealMethod();
        Mockito.when(fileMetadataDAO.getFileMetadataByPath(anyString())).thenCallRealMethod();
        Mockito.when(fileMetadataDAO.isPathInUseByFile(any(Path.class))).thenCallRealMethod();
        Mockito.when(fileMetadataDAO.findAll()).thenCallRealMethod();
        doAnswer(new CallsRealMethods()).when(folderMetadataDAO)
                                        .delete(anyCollection());
        doAnswer(new CallsRealMethods()).when(fileMetadataDAO)
                                        .update(any(FileMetadata.class));

        Mockito.when(folderMetadataDAO.store(Mockito.any(FolderMetadata.class))).thenCallRealMethod();
        Mockito.when(folderMetadataDAO.fetchOptional(any(), any())).thenCallRealMethod();
        Mockito.when(folderMetadataDAO.getFolderIdByPath(any())).thenCallRealMethod();
        Mockito.when(folderMetadataDAO.getFolderMetadataByPath(any())).thenCallRealMethod();
        Mockito.when(folderMetadataDAO.fetchOneByFolderId(any())).thenCallRealMethod();
        Mockito.when(folderMetadataDAO.findAll()).thenCallRealMethod();
        doAnswer(new CallsRealMethods()).when(folderMetadataDAO)
                                        .delete(any(FolderMetadata.class));
        doAnswer(new CallsRealMethods()).when(folderMetadataDAO)
                                        .update(anyCollection());


        Mockito.when(usersDAO.store(Mockito.any())).thenCallRealMethod();
        Mockito.when(usersDAO.getUserByName(Mockito.any())).thenCallRealMethod();
        Mockito.when(usersDAO.fetchOneById(Mockito.any())).thenCallRealMethod();
        Mockito.when(usersDAO.fetchOptional(any(), any())).thenCallRealMethod();
        Mockito.when(usersDAO.findAll()).thenCallRealMethod();

        doAnswer(new CallsRealMethods()).when(usersDAO)
                                        .delete(anyCollection());
*/