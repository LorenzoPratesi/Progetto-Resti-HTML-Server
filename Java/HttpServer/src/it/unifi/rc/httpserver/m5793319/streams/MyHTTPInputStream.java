package it.unifi.rc.httpserver.m5793319.streams;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import it.unifi.rc.httpserver.HTTPInputStream;
import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPReply;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPRequest;

/**
 * Class that extends {@link HTTPInputStream}.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class MyHTTPInputStream extends HTTPInputStream {

	private BufferedInputStream inputStream;
	private Scanner stringsScanner;
	private String firstLineDelimiter = "\r\n";
	private String headerLineDelimiter = "\r\n\r\n";

	/**
	 * Constructor that set up the InputStream
	 *
	 * @param is
	 */
	public MyHTTPInputStream(InputStream is) {
		super(is);
	}

	/**
	 * It initializes the inputStream with an InputStream.
	 */
	@Override
	protected void setInputStream(InputStream inputStream) {
		this.inputStream = new BufferedInputStream(inputStream);
		byte[] buffer = new byte[256];
		int currentReading;
		String finalString;
		try {
			currentReading = inputStream.read(buffer);
			finalString = new String(buffer, 0, currentReading);
		} catch (IOException e) {
			e.printStackTrace();
			finalString = "";
		}
		stringsScanner = new Scanner(finalString);
		stringsScanner.useDelimiter("\r\n");
	}

	/**
	 * @return {@link MyHTTPRequest} parsed from {@link #stringsScanner} 
	 */
	@Override
	public HTTPRequest readHttpRequest() throws HTTPProtocolException {
		String firstLine = readLineWithDelimiter(firstLineDelimiter);
		String headerLine = readLineWithDelimiter(headerLineDelimiter);
		String body = readBody();
		return new MyHTTPRequest(firstLine, headerLine, body);
	}

	/**
	 * 
	 * @return {@link MyHTTPReply} parsed from {@link #stringsScanner} 
	 */
	@Override
	public HTTPReply readHttpReply() throws HTTPProtocolException {
		String firstLine = readLineWithDelimiter(firstLineDelimiter);
		String headerLine = readLineWithDelimiter(headerLineDelimiter);
		String body = readBody();
		return new MyHTTPReply(firstLine, headerLine, body);
	}

	/**
	 * It closes both {@link #inputStream} and {@link #stringsScanner}.
	 */
	@Override
	public void close() throws IOException {
		inputStream.close();
		stringsScanner.close();
	}

	/**
	 * It splits the data in the {@link #stringsScanner} 
	 * 
	 * @param delimiter
	 * @return a {@link String} from the {@link #stringsScanner} using the
	 *         {@code delimiter}.
	 */
	private String readLineWithDelimiter(String delimiter) {
		stringsScanner.useDelimiter(delimiter);
		String firstLine = stringsScanner.hasNext() ? stringsScanner.next() : "";

		stringsScanner.skip(delimiter);
		return firstLine;
	}

	/**
	 * It parses the "body" as {@link String} using {@link #stringScanner}.
	 * 
	 * @return a {@link String} containing the "body".
	 */
	private String readBody() {
		StringBuilder myStringBuilder = new StringBuilder();
		stringsScanner.forEachRemaining(myStringBuilder::append);
		return myStringBuilder.toString();
	}
}