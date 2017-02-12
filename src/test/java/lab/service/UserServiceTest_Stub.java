package lab.service;

import lab.daos.SessionDataDAO;
import lab.daos.UsersDAO;
import lab.db.tables.pojos.SessionData;
import lab.db.tables.pojos.Users;
import lab.exceptions.UnsuccessfulLoginException;
import lab.util.Helper;
import lab.util.PasswordUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static lab.db.Tables.USERS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest_Stub {
	private UserService service;
	private UsersDAO usersDAO;
	private SessionDataDAO sessionDAO;
	
	@Before
	public void setup() {
		usersDAO = mock(UsersDAO.class);
		sessionDAO = mock(SessionDataDAO.class);
		service = new UserService(usersDAO, sessionDAO);
	}
	
	@Test
	public void createUser_ShouldCallUsersDaoMethod() throws Exception {
		service.createUser("user", "pass");
		verify(usersDAO, times(1)).store(any());
	}
	
	@Test
	public void getUserIfLoginWasSuccessful_UserAndPasswordOK_ShouldReturnUser() throws Exception {
		Users user = new Users(1, "Somebody", "Somebody", PasswordUtil.hashPassword("password"));
		
		when(usersDAO.fetchOptional(USERS.USER_NAME, user.getDisplayName())).thenReturn(Optional.of(user));
		assertThat(service.getUserIfLoginWasSuccessful("Somebody", "password")).isEqualTo(user);
	}
	
	@Test
	public void getUserIfLoginWasSuccessful_UserDoesntExistsInDatabase_ShouldThrowException() throws Exception {
		Users user = new Users(1, "Somebody", "Somebody", PasswordUtil.hashPassword("password"));
		
		when(usersDAO.fetchOptional(USERS.USER_NAME, user.getDisplayName())).thenReturn(Optional.ofNullable(null));
		assertThatThrownBy(() -> service.getUserIfLoginWasSuccessful("Somebody", "wrongPassword")).isInstanceOf(
			   UnsuccessfulLoginException.class);
	}
	
	@Test
	public void updateLastAccessField_ShouldCallDaoMethod() {
		String id = Helper.generateRandomString();
		when(sessionDAO.fetchOneBySessionId(id)).thenReturn(new SessionData(id, null, null));
		service.updateLastAccessField(id);
		Mockito.verify(sessionDAO, times(1)).update(Mockito.any(SessionData.class));
	}
}
