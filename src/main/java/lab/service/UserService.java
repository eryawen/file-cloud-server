package lab.service;

import lab.daos.SessionDataDAO;
import lab.daos.UsersDAO;
import lab.db.tables.pojos.SessionData;
import lab.db.tables.pojos.Users;
import lab.exceptions.UnsuccessfulLoginException;
import lab.util.Helper;
import lab.util.PasswordUtil;
import lab.util.Path;

import java.util.Optional;

import static lab.db.Tables.SESSION_DATA;
import static lab.db.Tables.USERS;
import static org.valid4j.Validation.otherwiseThrowing;
import static org.valid4j.Validation.validate;

public class UserService {
	private final UsersDAO usersDAO;
	private final SessionDataDAO sessionDataDAO;
	public static final int TIME_AFTER_SESSION_EXPIRES_IN_SEC = 200;
	
	public UserService(UsersDAO usersDAO, SessionDataDAO sessionDataDAO) {
		this.usersDAO = usersDAO;
		this.sessionDataDAO = sessionDataDAO;
	}
	
	public Users createUser(String username, String password) {
		Users user = new Users(null, username, username, PasswordUtil.hashPassword(password));
		return usersDAO.store(user);
	}
	
	public Users getUserIfLoginWasSuccessful(String name, String pass) {
		Optional<Users> user = usersDAO.fetchOptional(USERS.USER_NAME, name);
		validate(user.isPresent(), otherwiseThrowing(UnsuccessfulLoginException.class));
		validate(PasswordUtil.verifyPassword(pass, user.get().getHashedPassword()),
			    otherwiseThrowing(UnsuccessfulLoginException.class));
		return user.get();
	}
	
	public SessionData createSession(Users user) {
		SessionData sessionData = new SessionData(generateSessionId(), user.getId(), Helper.getTime());
		return sessionDataDAO.store(sessionData);
	}
	
	public boolean sessionIsActive(String sessionId) {
		return (sessionDataDAO.fetchOptional(SESSION_DATA.SESSION_ID, sessionId).isPresent()) &&
			  (Helper.getTimeDifferenceInSeconds(
					sessionDataDAO.fetchOneBySessionId(sessionId)
							    .getLastAccessed()) < TIME_AFTER_SESSION_EXPIRES_IN_SEC);
	}
	
	public void deleteSession(String sessionId) {
		sessionDataDAO.deleteById(sessionId);
	}
	
	public SessionData updateLastAccessField(String sessionId) {
		SessionData sessionData = sessionDataDAO.fetchOneBySessionId(sessionId);
		sessionData.setLastAccessed(Helper.getTime());
		sessionDataDAO.update(sessionData);
		return sessionDataDAO.fetchOneBySessionId(sessionId);
	}
	
	private Users getUserConnectedWithSession(String sessionId) {
		Integer userId = sessionDataDAO.fetchOneBySessionId(sessionId).getUserId();
		return usersDAO.fetchOneById(userId);
	}
	
	public boolean usernameIsAlreadyTaken(String username) {
		return usersDAO.fetchOptional(USERS.USER_NAME, username).isPresent();
	}
	
	private String generateSessionId() {
		String uid;
		do {
			uid = Helper.generateRandomString();
		} while (sessionDataDAO.fetchOptional(SESSION_DATA.SESSION_ID, uid).isPresent());
		return uid.toString();
	}
	
	public boolean userHasAccessToFile(String session, Path filePath) {
		return getUserConnectedWithSession(session).getDisplayName().equals(filePath.getOwnerName());
	}
	
	public void checkIfAnySessionExpired() {
		sessionDataDAO.findAll().forEach(sessionData -> {
			if (!sessionIsActive(sessionData.getSessionId())) {
				deleteSession(sessionData.getSessionId());
			}
		});
	}
}
