package lab.daos;

import lab.db.tables.daos.FileMetadataDao;
import lab.db.tables.pojos.FileMetadata;
import lab.db.tables.records.FileMetadataRecord;
import lab.util.Path;
import lab.util.Paths;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static lab.db.Tables.FILE_METADATA;

public class FileMetadataDAO extends FileMetadataDao {
	
	public FileMetadata store(FileMetadata fileMetadata) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			FileMetadataRecord record = create.newRecord(FILE_METADATA, fileMetadata);
			record.store();
			FileMetadata retrieved = record.into(FileMetadata.class);
			return retrieved;
		}
	}
	
	public FileMetadata getFileMetadataByPath(String path) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			FileMetadataRecord record = create.selectFrom(FILE_METADATA).where(
				   FILE_METADATA.PATH_DISPLAY.equal(path)).fetchOne();
			return record.into(FileMetadata.class);
		}
	}
	
	public boolean isPathInUseByFile(Path path) {
		return fetchOptional(FILE_METADATA.PATH_DISPLAY, path.getPathDisplay()).isPresent();
	}
	
	public FileMetadataDAO(Configuration configuration) {
		super(configuration);
	}
	
	public FileMetadataDAO() {
	}
}
