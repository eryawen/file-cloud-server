package lab.controller;

import lab.db.tables.pojos.SessionData;
import lab.db.tables.pojos.Users;
import lab.exceptions.UsernameAlreadyExistsException;
import lab.service.FolderService;
import lab.service.UserService;
import lab.util.PasswordUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.Request;
import spark.Response;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class UserControllerTest {
	private UserController controller;
	private UserService userService;
	private FolderService folderService;
	private Request request;
	private Response response;
	
	@Before
	public void setup() {
		userService = mock(UserService.class);
		folderService = mock(FolderService.class);
		request = mock(Request.class);
		response = mock(Response.class);
		controller = new UserController(userService, folderService);
	}
	
	@Test
	public void handleCreateUser_withValidParamethers_ShouldCreateUserAndFolder() throws Exception {
		Mockito.when(request.body()).thenReturn("{\n" +
										"  \"user_name\": \"user\",\n" +
										"  \"user_pass\": \"pass\"\n" +
										"}");
		Mockito.when(userService.usernameIsAlreadyTaken(any())).thenReturn(false);
		
		Mockito.when(userService.createUser(anyString(), anyString())).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			return new Users(null, (String) args[0], (String) args[0],
						  PasswordUtil.hashPassword((String) args[1]));
		});
		
		controller.handleCreateUser(request, response);
		Mockito.verify(userService, times(1)).createUser("user", "pass");
		Mockito.verify(folderService, times(1)).createUserFolder(any());
	}
	
	@Test
	public void handleCreateUser_UsernameIsAlreadyTaken_ShouldThrowException() throws Exception {
		Mockito.when(request.body()).thenReturn("{\n" +
										"  \"user_name\": \"user\",\n" +
										"  \"user_pass\": \"pass\"\n" +
										"}");
		Mockito.when(userService.usernameIsAlreadyTaken(any())).thenReturn(true);
		
		assertThatThrownBy(() -> controller.handleCreateUser(request, response)).isInstanceOf(
			   UsernameAlreadyExistsException.class);
		
		Mockito.verify(userService, times(0)).createUser(anyString(), anyString());
		Mockito.verify(folderService, times(0)).createUserFolder(any());
	}
	
	@Test
	public void handleLogin_ValidParams_ShouldCreateSession() throws Exception {
		Mockito.when(request.headers("Authorization")).thenReturn("Basic QWxhZGRpbjpPcGVuU2VzYW1l");
		Mockito.when(userService.getUserIfLoginWasSuccessful(anyString(), anyString())).thenReturn(
			   new Users(1, "a", "a", PasswordUtil.hashPassword("b")));
		Mockito.when(userService.createSession(any())).thenReturn(new SessionData("123", 1, null));
		
		controller.handleLogin(request, response);
		
		Mockito.verify(response, times(1)).cookie(anyString(), anyString(), anyInt());
		Mockito.verify(userService, times(1)).createSession(any());
	}
}