package lab;

import com.google.gson.Gson;
import lab.controller.FileController;
import lab.controller.FolderController;
import lab.controller.UserController;
import lab.daos.*;
import lab.exceptions.ServerException;
import lab.service.FileService;
import lab.service.FolderService;
import lab.service.UserService;
import lab.util.ErrorResponse;
import lab.util.LoggerMessage;
import lab.util.Paths;
import lab.util.RequestProcessing;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lab.util.Paths.CREATE_USER;
import static spark.Spark.*;

public class App {
	final static private Logger LOGGER = LoggerFactory.getLogger("Requests");
	
	public static void main(String[] args) {
		final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;
		final String DB_URL = "jdbc:sqlite:test.db";
		
		port(4567);
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DB_URL);
			connection.createStatement().execute("PRAGMA foreign_keys = ON");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Configuration configuration = new DefaultConfiguration().set(connection).set(SQLDialect.SQLITE);
		
		final UserService userService = new UserService(new UsersDAO(configuration),
											   new SessionDataDAO(configuration));
		final FolderService folderService = new FolderService(new FolderMetadataDAO(configuration),
												    new FileMetadataDAO(configuration),
												    new UsersDAO(configuration));
		final FileService fileService = new FileService(new FileMetadataDAO(configuration),
											   new FolderMetadataDAO(configuration),
											   new FileContentDAO(configuration),
											   new UsersDAO(configuration));
		
		final UserController userController = new UserController(userService, folderService);
		final FileController fileController = new FileController(folderService, fileService, userService);
		final FolderController folderController = new FolderController(folderService, fileService, userService);

//		set the connection to be secure
//        String keyStoreLocation = "deploy/keystore.jks";
//        String keyStorePassword = "changeit";
//        secure(keyStoreLocation, keyStorePassword, null, null);
		
		before(Paths.FILES, (req, res) -> {
			RequestProcessing.prepareRequestPaths(req);
			userController.updateSessionIfActive(req, res);
		});
		before(Paths.FOLDERS, (req, res) -> {
			RequestProcessing.prepareRequestPaths(req);
			userController.updateSessionIfActive(req, res);
		});
		
		get(Paths.USER_LOGIN, userController::handleLogin);
		get(Paths.USER_LOGOUT, userController::handleLogout, json);
		post(CREATE_USER, userController::handleCreateUser, json);
		
		get(Paths.GET_FOLDER_METADATA, folderController::handleGetFolderMetadata, json);
		get(Paths.LIST_FOLDER_CONTENT, folderController::handleListFolderContent, json);
		put(Paths.CREATE_DIR, folderController::handleCreateDirectory, json);
		put(Paths.RENAME_DIR, folderController::handleRenameFolder, json);
		put(Paths.MOVE_DIR, folderController::handleMoveFolder, json);
		delete(Paths.DELETE_DIR, folderController::handleDeleteFolder, json);
		
		get(Paths.GET_FILE_METADATA, fileController::handleGetFileMetadata, json);
		get(Paths.DOWNLOAD_FILE, fileController::handleDownloadFile);
		put(Paths.RENAME_FILE, fileController::handleRenameFile, json);
		put(Paths.MOVE_FILE, fileController::handleMoveFile, json);
		delete(Paths.DELETE_FILE, fileController::handleDeleteFile, json);
		post(Paths.UPLOAD_FILE, (Request request, Response response) -> {
			RequestProcessing.prepareRequestPaths(request);
			userController.updateSessionIfActive(request, response);
			return fileController.handleUploadFile(request, response);
		}, json);
		
		after((req, res) -> LOGGER.debug(new LoggerMessage(req).toString()));
		
		exception(ServerException.class, (ex, request, response) ->
		{
			ServerException serverException = (ServerException) ex;
			response.status(serverException.getStatusCode());
			response.body(gson.toJson(new ErrorResponse(request, ex)));
			LOGGER.error(new LoggerMessage(request, ex).toString());
		});
		exception(Exception.class, (ex, request, response) -> LOGGER.error(new LoggerMessage(request, ex).toString())
		);
		
		new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				userService.checkIfAnySessionExpired();
			}
		}).start();
	}
}


