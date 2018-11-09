package it.unifi.rc.httpserver.m5793319.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import it.unifi.rc.httpserver.HTTPHandler;
import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPProtocolException;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPReply;

/**
 * Class that implements the interface {@link HTTPHandler}.
 * <p>
 * HTTP/1.0 protocol, accepts only HTTP/1.0 requests.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class HandlerVer1_0 implements HTTPHandler {
	protected File root;
	protected String host;

	/**
	 * Constructor: set up the handler with rootDirectory and no host.
	 * 
	 * @param root
	 */
	public HandlerVer1_0(File root) {
		this.root = root;
		setHost(null);
	}

	/**
	 * Constructor that set up the handler with both rootDirectory and host.
	 * 
	 * @param root
	 * @param host
	 */
	public HandlerVer1_0(File root, String host) {
		this.root = root;
		setHost(host);
	}

	/**
	 * Set the host.
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return a list of supported methods from {@link HandlerVer1_0}.
	 */
	protected List<String> listAvailableMethods() {
		return Arrays.asList("HEAD", "GET", "POST");
	}

	/**
	 * @return HTTP version accepted by {@link HandlerVer1_0}.
	 */
	protected String getHttpVersion() {
		return "HTTP/1.0";
	}

	/**
	 * Handle the request and returns a HTTPReply that contains the result.
	 */
	@Override
	public HTTPReply handle(HTTPRequest request) {
		if (request == null) {
			return null;
		}
		if (!getHttpVersion().equals(request.getVersion())) {
			return new MyHTTPReply(new MyHTTPProtocolException(505, "HTTP Version Not Supported",
					request.getVersion() + " not supported in current handler"), getHttpVersion());
		}
		if (!listAvailableMethods().contains(request.getMethod())) {
			return new MyHTTPReply(new MyHTTPProtocolException(501, "Not Implemented",
					request.getMethod() + " not implemented in current handler"), getHttpVersion());
		}
		if (host != null) {
			if (!request.getParameters().get("Host").equals(host) || request.getParameters().get("Host") == null) {
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
			default:
				throw new MyHTTPProtocolException(501, "Not Implemented",
						request.getMethod() + " not implemented in current handler");
			}

		} catch (HTTPProtocolException ex) {
			return new MyHTTPReply((MyHTTPProtocolException) ex, getHttpVersion());
		}
	}

	/**
	 * Execute the POST method, use an OutputStream to add the body to the file specified by path.
	 * 
	 * @param path
	 * @param body
	 * @return a {@link MyHTTPReply}
	 * @throws HTTPProtocolException
	 */
	protected HTTPReply execPostMethod(String path, String body) throws HTTPProtocolException {
		try {
			OutputStream myOutputStream = new FileOutputStream(new File(root.getAbsolutePath() + path), true);
			myOutputStream.write(body.getBytes());
			myOutputStream.close();
			return new MyHTTPReply("HTTP/1.0 204 No Content", MyHTTPReply.parseHeaders().toString(), "");
		} catch (IOException e) {
			return new MyHTTPReply("HTTP/1.0 404 Not Found", MyHTTPReply.parseHeaders().toString(),
					e.getStackTrace().toString());
		}
	}

	/**
	 * Execute the HEAD method.
	 * 
	 * @param url
	 * @return a {@link MyHTTPReply} created from {@link #getHeader(String)} with
	 *         {@code url} used as parameter.
	 * @throws HTTPProtocolException
	 */
	protected HTTPReply execHeadMethod(String url) throws HTTPProtocolException {
		return new MyHTTPReply("HTTP/1.0 200 OK", getHeader(url), "");
	}

	/**
	 * Execute the GET method.
	 * 
	 * @param url
	 * @return a {@link MyHTTPReply} created by {@link #getHeader(String)} and
	 *         {@link #parseResource(String)} both with {@code url} as parameter.
	 * @throws HTTPProtocolException
	 */
	protected HTTPReply execGetMethod(String url) throws HTTPProtocolException {
		return new MyHTTPReply("HTTP/1.0 200 OK", getHeader(url), parseResource(url));
	}

	/**
	 * 
	 * 
	 * @param url
	 *            path to the resource
	 * @return a {@link String} containing the specified resource for the
	 *         {@link #execGetMethod(String)}.
	 * @throws MyHTTPProtocolException
	 */
	private String parseResource(String url) throws MyHTTPProtocolException {
		StringBuilder myStringBuilder = new StringBuilder();
		String myRes;
		try {
			
			String path = root.getAbsolutePath() + url;
			FileReader fileReader = new FileReader(path);
			
			BufferedReader myReader = new BufferedReader(fileReader);
			char[] buffer = new char[256];
			int currentReadingInt;

			while ((currentReadingInt = myReader.read(buffer)) != -1)
				myStringBuilder.append(buffer, 0, currentReadingInt);
			myRes = myStringBuilder.toString();
			myReader.close();
		} catch (IOException e) {
			throw new MyHTTPProtocolException(404, "Not Found", url);
		} catch (NullPointerException e) {
			throw new MyHTTPProtocolException(500, "Internal Server Error", url);
		}
		return myRes;
	}

	/**
	 * Generate a {@link String} from the {@code url}.
	 * 
	 * @param url
	 * @return a {@link String} with some headers, as "Last-Modified".
	 */
	private String getHeader(String url) {
		StringBuilder myStringBuilder = MyHTTPReply.parseHeaders();
		if (url != null) {
			myStringBuilder.append("Last-Modified: ");
			File file = new File(root.getAbsolutePath() + url);
			if (!file.exists()) {
				file.mkdir();
			}
			myStringBuilder.append(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(file.lastModified()));
			myStringBuilder.append("\r\n");
		}
		return myStringBuilder.toString();
	}
}