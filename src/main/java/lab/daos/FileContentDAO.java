package lab.daos;

import lab.db.tables.daos.FileContentsDao;
import lab.db.tables.pojos.FileContents;
import lab.db.tables.pojos.FileMetadata;
import lab.db.tables.records.FileContentsRecord;
import lab.util.Paths;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static lab.db.Tables.FILE_CONTENTS;

public class FileContentDAO extends FileContentsDao {
	FileMetadataDAO fileMetadataDAO = new FileMetadataDAO(this.configuration());
	
	public FileContents create(FileContents fileContents) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			FileContentsRecord record = create.newRecord(FILE_CONTENTS, fileContents);
			record.store();
			return record.into(FileContents.class);
		}
	}
	
	public FileContentDAO(Configuration configuration) {
		super(configuration);
	}
	
	public FileContentDAO() {
	}
	
	public byte[] getContentByPath(String filePath) {
		FileMetadata fileMetadata = fileMetadataDAO.getFileMetadataByPath(filePath);
		FileContents fileContents = fetchOneByFileId(fileMetadata.getFileId());
		return fileContents.getContents();
	}
}
