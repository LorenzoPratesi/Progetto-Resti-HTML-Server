package it.unifi.rc.httpserver.m5793319.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPProtocolException;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPReply;

/**
 * Class that extends {@link HandlerVer1_0}.<p>
 * This handler version accepts both HTTP/1.0 and HTTP/1.1 requests.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class HandlerVer1_1 extends HandlerVer1_0 {
	/**
	 * Constructor that set up handler with a root and no host, using the superclass {@link HandlerVer1_0} constructor.
	 * 
	 * @param root
	 */
	public HandlerVer1_1(File root) {
		super(root);
	}

	/**
	 * Constructor that set up handler with both root and host, using the superclass {@link HandlerVer1_0} constructor.
	 * 
	 * @param root
	 * @param host
	 */
	public HandlerVer1_1(File root, String host) {
		super(root, host);
	}

	/**
	 * @return a list of supported methods from {@link HandlerVer1_1}.
	 */
	@Override
	protected List<String> listAvailableMethods() {
		return Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTION", "CONNECT");
	}

	/**
	 * @return HTTP versions accepted by {@link HandlerVer1_1}.
	 */
	@Override
	protected String getHttpVersion() {
		return "HTTP/1.0 and HTTP/1.1";
	}

	/**
	 * Handle the {@code request} and returns a {@link HTTPReply} that contains the eventual result.
	 */
	@Override
	public HTTPReply handle(HTTPRequest request) {
		if (!getHttpVersion().contains(request.getVersion())) {
			return new MyHTTPReply(new MyHTTPProtocolException(505, "HTTP Version Not Supported", request.getVersion() + " not supported in current handler"),getHttpVersion());
		}
		if (!listAvailableMethods().contains(request.getMethod())) {
			return new MyHTTPReply(new MyHTTPProtocolException(501, "Not Implemented", request.getMethod() + " not implemented in current handler"),getHttpVersion());
		}
		if(host!=null) {
			if(!request.getParameters().get("Host").equals(host) || request.getParameters().get("Host")==null) {
				return null;
			}
		}
		try {
			switch (request.getMethod()) {
			case "HEAD":
				return execHeadMethod(request.getPath());
			case "GET":
				return execGetMethod(request.getPath());
			case "POST":
				return execPostMethod(request.getPath(), request.getEntityBody());
			case "PUT":
				return execPutMethod(request.getPath(), request.getEntityBody());
			case "DELETE":
				return execDeleteMethod(request.getPath(), request.getParameters());
			}
			throw new MyHTTPProtocolException(501, "Not Implemented", request.getMethod() + " not implemented in current handler");
		} catch (HTTPProtocolException ex) {
			return new MyHTTPReply((MyHTTPProtocolException)ex, "Error while processing request!");
		}
	}

	/**
	 * Execute the PUT method, use a {@link OutputStream} to add {@code entityBody} to the file specified by {@code path}.
	 * 
	 * @param path
	 * @param entityBody
	 * @return a {@link HTTPReply}
	 * @throws HTTPProtocolException
	 */
	private HTTPReply execPutMethod(String path, String entityBody) throws HTTPProtocolException {
		StringBuilder myStringBuilder = fetchHeaders();
		HTTPReply reply;
		if (new File(root.getAbsolutePath() + path).exists())
			reply = new MyHTTPReply("HTTP/1.1 204 No Content", myStringBuilder.toString(), "");
		else
			reply = new MyHTTPReply("HTTP/1.1 201 Created", myStringBuilder.toString(), "");

		try {
			OutputStream myOutputStream = new FileOutputStream(new File(root.getAbsolutePath() + path));
			myOutputStream.write(entityBody.getBytes());
			myOutputStream.close();
			return reply;
		} catch (IOException e) {
			throw new MyHTTPProtocolException(500, "Internal Server Error", path);
		}
	}

	/**
	 * Execute the DELETE method, imitating an authentication system to delete the file specified by {@code path}.
	 * 
	 * @param path
	 * @param pars
	 * @return a {@link HTTPReply}
	 * @throws HTTPProtocolException
	 */
	private HTTPReply execDeleteMethod(String path, Map<String, String> pars) throws HTTPProtocolException {
		String password = pars.get("Authentication");
		if (password == null || !password.equals("correct_password123"))
			return new MyHTTPReply("HTTP/1.1 401 Unauthorized", fetchHeaders().toString(), "");

		if (new File(root.getAbsolutePath() + path).delete())
			return new MyHTTPReply("HTTP/1.1 202 Accepted", fetchHeaders().toString(), "");
		else
			return new MyHTTPReply("HTTP/1.1 204 No Content", fetchHeaders().toString(), "");
	}

	/**
	 * 
	 * @return a {@link StringBuilder} containing the headers of a {@link MyHTTPReply}. 
	 */
	private StringBuilder fetchHeaders() {
		StringBuilder myStringBuilder = MyHTTPReply.parseHeaders();
		myStringBuilder.append("Connection: close");
		myStringBuilder.append("\r\n");
		return myStringBuilder;
	}
}