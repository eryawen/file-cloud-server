package lab.service.daosHashmapImpl;

import lab.daos.FolderMetadataDAO;
import lab.db.tables.pojos.FolderMetadata;
import org.jooq.Field;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class FolderMetadataDaoHashMapImpl extends FolderMetadataDAO {
	private HashMap<Integer, FolderMetadata> folderMetadataHashMap;
	Random random = new Random();
	
	public FolderMetadataDaoHashMapImpl() {
		folderMetadataHashMap = new HashMap<>();
	}
	
	@Override
	public FolderMetadata store(FolderMetadata folderMetadata) {
		int id = abs(random.nextInt());
		folderMetadataHashMap.put(id, folderMetadata);
		return folderMetadata.setFolderId(id);
	}
	
	@Override
	public <Z> Optional<FolderMetadata> fetchOptional(Field<Z> field, Z value) {
		if (field.equals("FOLDER_METADATA.PATH_DISPLAY")) {
			return folderMetadataHashMap.values().stream().filter(
				   folderMetadata -> folderMetadata.getPathDisplay().equals(value)).findAny();
		}
		return null;
	}
	
	@Override
	public void update(FolderMetadata object) {
		folderMetadataHashMap.put(object.getFolderId(), object);
	}
	
	@Override
	public Integer getFolderIdByPath(String path) {
		Optional<FolderMetadata> foldermetadata = folderMetadataHashMap.values().stream().filter(
			   folderMetadata -> folderMetadata.getPathDisplay().equals(path)).findAny();
		return foldermetadata.isPresent() ? foldermetadata.get().getFolderId() : null;
	}
	
	@Override
	public FolderMetadata getFolderMetadataByPath(String path) {
		return folderMetadataHashMap.values().stream().filter(
			   folderMetadata -> folderMetadata.getPathDisplay().equals(path)).findAny().get();
	}
	
	@Override
	public void update(Collection<FolderMetadata> objects) {
		objects.forEach(folderMetadata -> folderMetadataHashMap.put(folderMetadata.getFolderId(), folderMetadata));
	}
	
	@Override
	public FolderMetadata fetchOneByFolderId(Integer value) {
		Optional<FolderMetadata> foldermetadata = folderMetadataHashMap.values().stream().filter(
			   folderMetadata -> folderMetadata.getFolderId().equals(value)).findAny();
		return foldermetadata.isPresent() ? foldermetadata.get() : null;
	}
	
	@Override
	public void delete(FolderMetadata... objects) {
		folderMetadataHashMap.remove(objects);
	}
	
	public void deleteAll() {
		folderMetadataHashMap = new HashMap<>();
	}
	
	public List<FolderMetadata> findAll() {
		return folderMetadataHashMap.values().stream().collect(Collectors.toList());
	}
}


