package lab.util;

public class Paths {
	public static final String DB_URL = "jdbc:sqlite:test.db";
	
	public final static String FOLDERS = "/folders/:path/*";
	
	public final static String LIST_FOLDER_CONTENT = "/folders/:path/list_folder_content";
	public final static String CREATE_DIR = "/folders/:path/create_directory";
	public final static String DELETE_DIR = "/folders/:path/delete";
	public final static String RENAME_DIR = "/folders/:path/rename";
	public final static String GET_FOLDER_METADATA = "/folders/:path/get_meta_data";
	public final static String MOVE_DIR = "/folders/:path/move";
	
	public final static String FILES = "/files/:path/*";
	public final static String DOWNLOAD_FILE = "/files/:path/download";
	public final static String RENAME_FILE = "/files/:path/rename";
	public final static String DELETE_FILE = "/files/:path/delete";
	public final static String GET_FILE_METADATA = "/files/:path/get_meta_data";
	public final static String UPLOAD_FILE = "/files/upload";
	
	public final static String MOVE_FILE = "/files/:path/move";
	public final static String CREATE_USER = "/users/create_user";
	public final static String USER_LOGIN = "/users/access";
	public final static String USER_LOGOUT = "/users/logout";
}
