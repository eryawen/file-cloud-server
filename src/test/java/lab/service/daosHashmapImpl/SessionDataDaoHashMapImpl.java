package lab.service.daosHashmapImpl;

import lab.daos.SessionDataDAO;
import lab.db.tables.pojos.SessionData;
import org.jooq.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static lab.util.Helper.generateRandomString;

public class SessionDataDaoHashMapImpl extends SessionDataDAO {
	private HashMap<String, SessionData> sessionHashMap = new HashMap<>();
	
	@Override
	public SessionData store(SessionData sessionData) {
		String id = generateRandomString();
		sessionHashMap.put(id, sessionData);
		return sessionData.setSessionId(id);
	}
	
	@Override
	public <Z> Optional<SessionData> fetchOptional(Field<Z> field, Z value) {
		if (field.equals("USERS.USER_NAME")) {
			return sessionHashMap.values().stream().filter(
				   sessionData -> sessionData.getSessionId().equals(value)).findAny();
		}
		return null;
	}
	
	@Override
	public SessionData fetchOneBySessionId(String value) {
		return sessionHashMap.get(value);
	}
	
	@Override
	public void update(SessionData object) {
		sessionHashMap.put(object.getSessionId(), object);
	}
	
	@Override
	public List<SessionData> findAll() {
		return sessionHashMap.values().stream().collect(Collectors.toList());
	}
	
	public void deleteAll() {
		sessionHashMap = new HashMap<>();
	}
}
