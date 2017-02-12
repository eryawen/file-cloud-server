package lab.service.daosHashmapImpl;

import lab.daos.FileMetadataDAO;
import lab.db.tables.pojos.FileMetadata;
import lab.util.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class FileMetadataDaoHashMapImpl extends FileMetadataDAO {
	private HashMap<Integer, FileMetadata> fileMetadataHashmap = new HashMap<>();
	private Random random = new Random();
	
	public FileMetadataDaoHashMapImpl() {
	}
	
	@Override
	public FileMetadata store(FileMetadata fileMetadata) {
		int id = abs(random.nextInt());
		fileMetadataHashmap.put(id, fileMetadata);
		return fileMetadata.setFileId(id);
	}
	
	@Override
	public FileMetadata getFileMetadataByPath(String path) {
		Optional<FileMetadata> filemetadata = fileMetadataHashmap.values().stream().filter(
			   fileMetadata -> fileMetadata.getPathDisplay().equals(path)).findFirst();
		return filemetadata.isPresent() ? filemetadata.get() : null;
	}
	
	@Override
	public boolean isPathInUseByFile(Path path) {
		return fileMetadataHashmap.values().stream().anyMatch(
			   fileMetadata -> (fileMetadata.getPathDisplay()).equals(path));
	}
	
	@Override
	public void update(FileMetadata object) {
		fileMetadataHashmap.put(object.getFileId(), object);
	}
	
	public void deleteAll() {
		fileMetadataHashmap = new HashMap<>();
	}
	
	@Override
	public void delete(FileMetadata... objects) {
		fileMetadataHashmap.remove(objects[0].getFileId());
	}
	
	public List<FileMetadata> findAll() {
		return fileMetadataHashmap.values().stream().collect(Collectors.toList());
	}
}



