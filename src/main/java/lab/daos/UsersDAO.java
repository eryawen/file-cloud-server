package lab.daos;

import lab.db.tables.daos.UsersDao;
import lab.db.tables.pojos.Users;
import lab.db.tables.records.UsersRecord;
import lab.util.Paths;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static lab.db.Tables.USERS;

public class UsersDAO extends UsersDao {
	public UsersDAO(Configuration configuration) {
		super(configuration);
	}
	
	public Users store(Users user) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			UsersRecord record = create.newRecord(USERS);
			record.from(user);
			record.store();
			return record.into(Users.class);
		}
	}
	
	public Users getUserByName(String username) {
		try (DSLContext create = DSL.using(Paths.DB_URL)) {
			UsersRecord record = create.selectFrom(USERS).where(
				   USERS.DISPLAY_NAME.equal(username)).fetchOne();
			return record.into(Users.class);
		}
	}
	
	public UsersDAO() {
	}
}
