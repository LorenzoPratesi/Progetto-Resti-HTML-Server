package it.unifi.rc.httpserver.m5793319.http_protocol;

import it.unifi.rc.httpserver.HTTPProtocolException;

/**
 * Class that extends {@link HTTPProtocolException}.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class MyHTTPProtocolException extends HTTPProtocolException {
	private static final long serialVersionUID = 7569538066492881383L;

	private final int errorCode;
	private final String errorMessage;
	private final String fullMessage;

	/**
	 * Constructor that initializes the errorCode, errorMessage and fullMessage
	 * variables.
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param fullMessage
	 */
	public MyHTTPProtocolException(int errorCode, String errorMessage, String fullMessage) {
		super(errorCode + " " + errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.fullMessage = fullMessage;
	}

	/**
	 * Getter
	 * 
	 * @return the errorCode.
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter
	 * 
	 * @return the errorMessage.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Getter
	 * 
	 * @return the fullMessage.
	 */
	public String getFullMessage() {
		return fullMessage;
	}
}