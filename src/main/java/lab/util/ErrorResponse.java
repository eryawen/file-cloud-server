package lab.util;

import lombok.AllArgsConstructor;
import spark.Request;

import java.util.Map;

@AllArgsConstructor
public class ErrorResponse {
	private String uri;
	private Map<String, String> params;
	private final String query;
	private String message;
	
	public ErrorResponse(Request request, Exception ex) {
		this.uri = request.uri();
		this.params = request.params();
		this.query = request.queryString();
		message = ex.getMessage();
	}
}

