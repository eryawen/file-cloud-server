package lab.util;

import org.assertj.core.api.AbstractAssert;

import java.util.Objects;

public class PathParserAssert extends AbstractAssert<PathParserAssert, Path> {

	public PathParserAssert(Path actual) {
		super(actual, PathParserAssert.class);
	}

	public static PathParserAssert assertThat(Path actual) {
		return new PathParserAssert(actual);
	}

	public PathParserAssert hasName(String name) {
		isNotNull();

		if (!Objects.equals(actual.getName(), name)) {
			failWithMessage("Expected folderName to be <%s> but was <%s>", name, actual.getName());
		}
		return this;
	}

	public PathParserAssert hasEnclosingFolderName(String name) {
		isNotNull();

		if (!Objects.equals(actual.getEnclosingFolderName(), name)) {
			failWithMessage("Expected enclosing folder name to be <%s> but was <%s>", name,
						 actual.getEnclosingFolderName());
		}
		return this;
	}

	public PathParserAssert hasParentPath(String path) {
		isNotNull();

		if (!Objects.equals(actual.getParentPath(), path)) {
			failWithMessage("Expected parent path to be <%s> but was <%s>", path, actual.getParentPath());
		}
		return this;
	}

	public PathParserAssert hasPathDisplay(String path) {
		isNotNull();

		if (!Objects.equals(actual.getPathDisplay(), path)) {
			failWithMessage("Expected path display to be <%s> but was <%s>", path, actual.getPathDisplay());
		}
		return this;
	}

	public PathParserAssert hasPathLower(String path) {
		isNotNull();

		if (!Objects.equals(actual.getPathLower(), path)) {
			failWithMessage("Expected path lower to be <%s> but was <%s>", path, actual.getPathLower());
		}
		return this;
	}

	public PathParserAssert hasOwnerName(String name) {
		isNotNull();

		if (!Objects.equals(actual.getOwnerName(), name)) {
			failWithMessage("Expected owner to be <%s> but was <%s>", name, actual.getOwnerName());
		}
		return this;
	}
}
