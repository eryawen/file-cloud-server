package lab.util;

import lab.exceptions.InvalidPathParameterException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Path {
	final static private Logger LOGGER_FILES = LoggerFactory.getLogger("Path");
	Pattern pathPattern = Pattern.compile("(/[^/]+)*/(?<enclosingFolder>[^/]+)/(?<name>[^/]+)$");
	Pattern pathRootPattern = Pattern.compile("/(?<name>[^/]+)$");
	private String name;
	private String enclosingFolderName;
	private String parentPath;
	private String pathLower;
	private String pathDisplay;
	private String ownerName;
	
	public boolean isMainUserPath() {
		return name.equals(ownerName);
	}
	
	public Path(String path) {
		
		Matcher matcher = pathPattern.matcher(path);
		Matcher matcherRoot = pathRootPattern.matcher(path);
		
		if (matcher.matches()) {
			name = matcher.group("name");
			enclosingFolderName = matcher.group("enclosingFolder");
			parentPath = path.substring(0, path.length() - name.length() - 1);
			
			ownerName = path.split("/")[1];
		} else if (matcherRoot.matches()) {
			name = matcherRoot.group("name");
			enclosingFolderName = "";
			parentPath = "";
			ownerName = name;
		} else {
			throw new InvalidPathParameterException();
		}
		
		pathDisplay = path;
		pathLower = path.toLowerCase();
	}
	
	public static Path getUpdatedPath(Path oldPath, Path newPath, String resourcePath) {
		return new Path(resourcePath.replaceFirst(oldPath.getPathDisplay(), newPath.getPathDisplay()));
	}
	//TODO
}
