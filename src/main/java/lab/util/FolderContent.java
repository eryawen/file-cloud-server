package lab.util;

import lab.db.tables.pojos.FileMetadata;
import lab.db.tables.pojos.FolderMetadata;
import lombok.Getter;

import java.util.List;

@Getter
public class FolderContent {
	FolderMetadata root;
	List<FolderContent> folders;
	List<FileMetadata> files;
	
	public FolderContent(FolderMetadata root, List<FolderContent> folders,
					 List<FileMetadata> files) {
		this.root = root;
		this.folders = folders;
		this.files = files;
	}
	
	public FolderContent() {
	}
	
	public FolderMetadata getRoot() {
		return root;
	}
	
	public List<FolderContent> getFolders() {
		return folders;
	}
	
	public List<FileMetadata> getFiles() {
		return files;
	}
	
	@Override
	public String toString() {
		return "FolderContent{" +
			  "root=" + root +
			  ", folders=" + folders +
			  ", files=" + files +
			  '}';
	}
}
