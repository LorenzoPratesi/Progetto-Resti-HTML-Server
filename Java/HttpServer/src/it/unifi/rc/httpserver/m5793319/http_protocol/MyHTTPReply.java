package it.unifi.rc.httpserver.m5793319.http_protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;

/**
 * Class that implements the interface {@link HTTPReply}.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class MyHTTPReply extends MySetUp implements HTTPReply {

	private String version;
	private String statusCode;
	private String statusMessage;
	private String data;

	static String delimiter = "\r\n";

	/**
	 * Constructor of {@link MyHTTPReply} that assigns the parameters to the object
	 * of this class
	 * 
	 * @param firstLine
	 * @param headerLine
	 * @param body
	 * @throws HTTPProtocolException
	 */
	public MyHTTPReply(String firstLine, String headerLine, String body) throws HTTPProtocolException {
		setUpFirstLineParameters(firstLine);
		setUpParametersMap(headerLine);
		this.data = body;
	}

	/**
	 * Constructor that create a request from an exception.
	 * 
	 * @param protocolException
	 * @param version
	 */
	public MyHTTPReply(MyHTTPProtocolException protocolException, String version) {
		this.version = version;
		this.statusCode = String.valueOf(protocolException.getErrorCode());
		this.statusMessage = protocolException.getErrorMessage();
	}

	/**
	 * Getter
	 * 
	 * @return the version.
	 */
	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * Getter
	 * 
	 * @return the statusCode.
	 */
	@Override
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Getter
	 * 
	 * @return the statusMessage.
	 */
	@Override
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @return the data.
	 */
	@Override
	public String getData() {
		return data;
	}

	/**
	 * Getter
	 * 
	 * @return the parameters.
	 */
	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Format the first line of the reply extracting parameters from the string
	 * line.
	 * 
	 * @param line
	 * @throws MyHTTPProtocolException
	 */
	protected void setUpFirstLineParameters(String line) throws MyHTTPProtocolException {
		String[] parameters = line.split(" ", 3);
		try {
			version = parameters[0];
			statusCode = parameters[1];
			statusMessage = parameters[2];
			statusMessage.replace(delimiter, "");
		} catch (Exception e) {
			throw new MyHTTPProtocolException(400, "Bad Request ",
					"Error while building first line " + e.getStackTrace());
		}
	}

	/**
	 * @return a StringBuilder used for parsing the headers
	 */
	public static StringBuilder parseHeaders() {
		String s = "";
		try {
			s = "Date: " + DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()) + delimiter + "Host: "
					+ InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e1) {
			s += "Unknown Host";
		}
		s += delimiter;
		return s.chars().collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);
	}

	@Override
	public String getMethod() {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public String getEntityBody() {
		return null;
	}

	@Override
	public String toString() {
		return version + " " + statusCode + " " + statusMessage + " "
				+ getParameters().toString().replaceAll("(^\\{|\\}$)", "") + " " + getData();
	}
}