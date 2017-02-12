package lab.daos;

import lab.db.tables.daos.FolderMetadataDao;
import lab.db.tables.pojos.FolderMetadata;
import lab.db.tables.records.FolderMetadataRecord;
import lab.util.Paths;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static lab.db.Tables.FOLDER_METADATA;

public class FolderMetadataDAO extends FolderMetadataDao {
	
	public FolderMetadata store(FolderMetadata folderMetadata) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			FolderMetadataRecord record = create.newRecord(FOLDER_METADATA);
			record.from(folderMetadata);
			record.store();
			FolderMetadata retrieved = record.into(FolderMetadata.class);
			return retrieved;
		}
	}
	
	public Integer getFolderIdByPath(String path) {
		return fetchOne(FOLDER_METADATA.PATH_DISPLAY, path).getFolderId();
	}
	
	public FolderMetadata getFolderMetadataByPath(String path) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(
				   FOLDER_METADATA.PATH_DISPLAY.equal(path)).fetchOne();
			return record.into(FolderMetadata.class);
		}
	}
	
	public FolderMetadataDAO(Configuration configuration) {
		super(configuration);
	}
	
	public FolderMetadataDAO() {
	}
}
