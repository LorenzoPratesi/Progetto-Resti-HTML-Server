package it.unifi.rc.httpserver.test.m5793319;
import java.io.File;

import it.unifi.rc.httpserver.HTTPHandler;
import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.handlers.HandlerVer1_0;
import it.unifi.rc.httpserver.m5793319.handlers.HandlerVer1_1;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPReply;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPRequest;

public class TestHelper {
	private String parameters = "Host: testHost\r\n\r\n";
	private String body = "Html body";

	public HTTPRequest createRequestFromMessage(String message) {
		try {
			return new MyHTTPRequest(message, parameters, body);
		} catch (HTTPProtocolException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public HTTPReply processNullRequest0(String rootPath, String requestMessage) {
		HTTPHandler myHttpHandler1_0 = new HandlerVer1_0(new File(rootPath));
		return myHttpHandler1_0.handle(null);
	}

	public HTTPReply processRequestWithHandler1_0(String rootPath, String requestMessage) {
		HTTPHandler myHttpHandler1_0 = new HandlerVer1_0(new File(rootPath));
		HTTPRequest myRequest = createRequestFromMessage(requestMessage);
		return myHttpHandler1_0.handle(myRequest);
	}

	public HTTPReply processRequestWithHandler1_1(String rootPath, String requestMessage) {
		HTTPHandler myHttpHandler1_1 = new HandlerVer1_1(new File(rootPath));
		HTTPRequest myRequest = createRequestFromMessage(requestMessage);
		return myHttpHandler1_1.handle(myRequest);
	}

	public HTTPReply processRequestWithHandler1_0WithHostName(String rootPath, String hostName, String requestMessage) {
		return new HandlerVer1_0(new File(rootPath), hostName).handle(createRequestFromMessage(requestMessage));
	}

	public HTTPReply deleteFileFromRootWithCustomRequestHandler1_1(String rootPath, HTTPRequest request) {
		HTTPHandler myHttpHandler1_1 = new HandlerVer1_1(new File(rootPath));
		return myHttpHandler1_1.handle(request);
	}

	public HTTPReply processRequestWithHandler1_1WithHostName(String rootPath, String hostName, String requestMessage) {
		return new HandlerVer1_1(new File(rootPath), hostName).handle(createRequestFromMessage(requestMessage));
	}

	public HTTPReply createReply(String firstLine, String parameters, String data) throws HTTPProtocolException {
		return new MyHTTPReply(firstLine, parameters, data);
	}

	public HTTPRequest createRequest(String firstLine, String parameters, String body) throws HTTPProtocolException {
		return new MyHTTPRequest(firstLine, parameters, body);
	}
}