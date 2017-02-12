package lab.util;

import lab.exceptions.InvalidPathParameterException;
import org.junit.Test;

import static lab.util.PathParserAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathsParserTest {

	@Test
	public void filePathIsParsedCorrectly() throws Exception {
		Path path = new Path("/user/first/Second/third/file.txt");
		assertThat(path).hasName("file.txt").hasEnclosingFolderName("third").hasOwnerName("user").hasPathDisplay(
			   "/user/first/Second/third/file.txt").hasPathLower(
			   "/user/first/second/third/file.txt").hasParentPath("/user/first/Second/third");
	}

	@Test
	public void folderPathIsParsedCorrectly() throws Exception {
		Path path = new Path("/user/first");
		assertThat(path).hasName("first").hasEnclosingFolderName("user").hasOwnerName("user").hasPathDisplay(
			   "/user/first").hasPathLower("/user/first").hasParentPath("/user");
	}

	@Test
	public void userPathIsParsedCorrectly() throws Exception {
		Path path = new Path("/user");
		assertThat(path).hasName("user").hasEnclosingFolderName("").hasOwnerName("user").hasPathDisplay(
			   "/user").hasPathLower(
			   "/user").hasParentPath("");
	}

	@Test
	public void pathEndedWithSlashThrowsException() throws Exception {
		assertThatThrownBy(() -> new Path("/user/folder/")).isInstanceOf(InvalidPathParameterException.class);
	}

	@Test
	public void pathNotStartedWithSlashThrowsException() throws Exception {
		assertThatThrownBy(() -> new Path("user/folder")).isInstanceOf(InvalidPathParameterException.class);
	}

	@Test
	public void renamedFolderPathIsChangedCorrectly() throws Exception {
		assertThat(Path.getUpdatedPath(new Path("/old"), new Path("/new"), "/old")).hasPathDisplay(
			   "/new");
	}

	@Test
	public void filePathWithinRenamedFolderIsChangedCorrectly() throws Exception {
		assertThat(Path.getUpdatedPath(new Path("/main/old"), new Path("/main/new"),
								 "/main/old/file")).hasPathDisplay(
			   "/main/new/file");
	}
}
