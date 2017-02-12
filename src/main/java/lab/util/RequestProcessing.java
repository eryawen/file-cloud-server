package lab.util;

import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

public class RequestProcessing {
	
	public static void prepareRequestPaths(Request req) {
		if (req.params(":path") != null) {
			req.attribute("path", decode(req.params(":path")));
		}
		Set<String> queries = req.queryParams();
		queries.forEach(query -> req.attribute(query, decode(req.queryParams(query))));
	}
	
	public static String decode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
