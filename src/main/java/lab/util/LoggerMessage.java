package lab.util;

import spark.Request;

public class LoggerMessage {
	private String message = "";
	
	public LoggerMessage(Request req) {
		message = String.format("Request: uri=%s, params=%s}", req.uri(), req.params());
		if (req.queryString() != null) {
			message = message + String.format(", query: %s", req.queryString());
		}
	}
	
	public LoggerMessage(Request req, Exception ex) {
		message = String.format("Request: uri=%s, params=%s}", req.uri(), req.params());
		if (req.queryString() != null) {
			message = message + String.format(", query: %s", req.queryString());
		}
		message = message + String.format("; Exception: %s", ex.getMessage());
	}
	
	@Override
	public String toString() {
		return message;
	}
}


