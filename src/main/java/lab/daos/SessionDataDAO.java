package lab.daos;

import lab.db.tables.daos.SessionDataDao;
import lab.db.tables.pojos.SessionData;
import lab.db.tables.records.SessionDataRecord;
import lab.util.Paths;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Random;

import static lab.db.Tables.SESSION_DATA;

public class SessionDataDAO extends SessionDataDao {
	
	public SessionData store(SessionData sessionData) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			SessionDataRecord record = create.newRecord(SESSION_DATA, sessionData);
			record.store();
			return record.into(SessionData.class);
		}
	}
	
	public SessionDataDAO(Configuration configuration) {
		super(configuration);
	}
	
	public SessionDataDAO() {
	}
}
