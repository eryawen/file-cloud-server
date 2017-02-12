package lab.service.daosHashmapImpl;

import lab.daos.FileContentDAO;
import lab.db.tables.pojos.FileContents;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FileContentDaoHashMapImpl extends FileContentDAO {
	private HashMap<Integer, FileContents> fileContentHashMap = new HashMap<>();
	
	@Override
	public FileContents fetchOneByFileId(Integer id) {
		return fileContentHashMap.get(id);
	}
	
	@Override
	public void insert(FileContents object) {
		fileContentHashMap.put(object.getFileId(), object);
	}
	
	public List<FileContents> findAll() {
		return fileContentHashMap.values().stream().collect(Collectors.toList());
	}
}

