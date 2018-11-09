package it.unifi.rc.httpserver.m5793319.http_protocol;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPRequest;

/**
 * Abstract Class that collects the method in common to the classes
 * {@link MyHTTPRequest} and {@link MyHTTPReply}
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 *
 */
public abstract class MySetUp implements HTTPRequest {

	private static String delimiter = "\r\n";
	protected String method;
	protected String version;
	Map<String, String> parameters;
	String path;

	/**
	 * Create a Map of parameters from the headerLine string.
	 * 
	 * @param headerLine
	 * @throws HTTPProtocolException
	 */

	void setUpParametersMap(String headersLines) throws HTTPProtocolException {
		if (headersLines != null) {
			if (!headersLines.contains(delimiter)) {
				throw new MyHTTPProtocolException(400, "Bad Request", "'\r\n' not found in headerLine");
			}
			parameters = new LinkedHashMap<>();

			Pattern.compile(System.lineSeparator()).splitAsStream(headersLines).map((wrapper(s -> s)))
					.forEach(s -> parameters.put(s.substring(0, s.indexOf(':')), s.substring(s.indexOf(' ') + 1)));
		}
	}

	/**
	 * Method that, received as input the request line, assigns the values it has
	 * extrapolated to fields
	 * 
	 * @param line
	 * @exception MyHTTPProtocolException
	 */
	protected void setUpFirstLineParameters(String line) throws MyHTTPProtocolException {
		String[] parameters = line.split(" ", 3);
		try {
			method = parameters[0];
			path = parameters[1];
			version = parameters[2];
			version.replace(delimiter, "");
		} catch (Exception e) {
			throw new MyHTTPProtocolException(400, "Bad Request",
					"Error while building first line " + e.getStackTrace());
		}
	}

	/**
	 * Method that wraps a function and checks if this function generates an
	 * exception
	 * 
	 * @param function
	 * @return string
	 */
	private <T, R, E extends HTTPProtocolException> Function<T, R> wrapper(CheckProtocolExeption<T, R, E> function) {
		return string -> {
			try {
				if (!((String) string).contains(":")) {
					throw new MyHTTPProtocolException(400, "Bad Request", string + " not containes ':'");
				}
				return function.apply(string);
			} catch (HTTPProtocolException e) {
				throw new RuntimeException(e);
			}
		};
	}

}