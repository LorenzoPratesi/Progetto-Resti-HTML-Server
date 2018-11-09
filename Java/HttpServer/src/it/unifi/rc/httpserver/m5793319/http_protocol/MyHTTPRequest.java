package it.unifi.rc.httpserver.m5793319.http_protocol;

import java.util.Map;
import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPRequest;

/**
 * Class that implements the interface {@link HTTPRequest}.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class MyHTTPRequest extends MySetUp implements HTTPRequest {
	
	private String entityBody;

	/**
	 * Constructor of {@link MyHTTPRequest} that assigns the parameters of a request to 
	 * the object created 
	 * 
	 * @param requestLine
	 * @param headerLines
	 * @param body
	 * @throws HTTPProtocolException
	 */
	public MyHTTPRequest(String requestLine, String headerLines, String body) throws HTTPProtocolException {
		setUpFirstLineParameters(requestLine);
		setUpParametersMap(headerLines);
		this.entityBody = body;
	}

	/**
	 * Getter
	 * 
	 * @return the {@link #version}.
	 */
	@Override
	public String getVersion() {
		return this.version;
	}

	/**
	 * Getter
	 * 
	 * @return the {@link #method}.
	 */
	@Override
	public String getMethod() {
		return this.method;
	}

	/**
	 * Getter
	 * 
	 * @return the {@link #path}.
	 */
	@Override
	public String getPath() {
		return this.path;
	}

	/**
	 * Getter
	 * 
	 * @return the {@link #entityBody}.
	 */
	@Override
	public String getEntityBody() {
		return this.entityBody;
	}

	/**
	 * Getter
	 * 
	 * @return the {@link #parameters}.
	 */
	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return getMethod() + " " + getPath() + " " + getVersion();
	}
}