package lab.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lab.db.tables.pojos.SessionData;
import lab.db.tables.pojos.Users;
import lab.exceptions.*;
import lab.service.FolderService;
import lab.service.UserService;
import lab.util.Helper;
import lab.util.Status;
import spark.Request;
import spark.Response;

import java.util.regex.PatternSyntaxException;

import static org.valid4j.Validation.otherwiseThrowing;
import static org.valid4j.Validation.validate;

public class UserController {
	private UserService userService;
	private FolderService folderService;
	
	public UserController(UserService userService, FolderService folderService) {
		this.userService = userService;
		this.folderService = folderService;
	}
	
	public Object handleCreateUser(Request request, Response response) {
		String username;
		String password;
		try {
			JsonObject obj = new Gson().fromJson(request.body(), JsonObject.class);
			username = obj.get("user_name").getAsString();
			password = obj.get("user_pass").getAsString();
		} catch (JsonSyntaxException e) {
			throw new WrongParamFormatException();
		}
		validate(!userService.usernameIsAlreadyTaken(username),
			    otherwiseThrowing(UsernameAlreadyExistsException.class));
		
		Users retrieved = userService.createUser(username, password);
		folderService.createUserFolder(retrieved.getId());
		response.status(Status.SUCCESSFUL_OPERATION);
		return retrieved;
	}
	
	public Object handleLogin(Request request, Response response) {
		SessionData sessionData;
		try {
			String[] usernameAndPassword = Helper.decode(request.headers("Authorization"));
			Users user = userService.getUserIfLoginWasSuccessful(usernameAndPassword[0], usernameAndPassword[1]);
			sessionData = userService.createSession(user);
//            response.cookie("session", sessionData.getSessionId(), 60, true);
			response.cookie("session", sessionData.getSessionId(), UserService.TIME_AFTER_SESSION_EXPIRES_IN_SEC);
		} catch (PatternSyntaxException e) {
			throw new UnsuccessfulLoginException();
		}
		response.status(Status.SUCCESSFUL_OPERATION);
		return sessionData.getSessionId();
	}
	
	public Object handleLogout(Request request, Response response) {
		String sessionId = request.cookie("session");
		userService.deleteSession(sessionId);
		response.removeCookie("session");
		response.status(Status.SUCCESSFUL_OPERATION);
		return response;
	}
	
	public void updateSessionIfActive(Request request, Response response) {
		validate((request.cookie("session") != null), otherwiseThrowing(AuthorisationRequiredException.class));
		String sessionId = request.cookie("session");
		validate(userService.sessionIsActive(sessionId), otherwiseThrowing(SessionExpiredException.class));
		
		userService.updateLastAccessField(sessionId);
//        response.cookie("session", sessionId, 60, true);
		response.cookie("session", sessionId, UserService.TIME_AFTER_SESSION_EXPIRES_IN_SEC);
	}
}
