package it.unifi.rc.httpserver.m5793319.streams;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import it.unifi.rc.httpserver.HTTPOutputStream;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;

/**
 * Class that extends {@link HTTPOutputStream}.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class MyHTTPOutputStream extends HTTPOutputStream {

	private BufferedOutputStream outputStream;
	private String delimiter = "\r\n";

	/**
	 * Constructor that set up the {@link MyHTTPOutputStream}
	 * 
	 * @param outputStream
	 */
	public MyHTTPOutputStream(OutputStream outputStream) {
		super(outputStream);
		setOutputStream(outputStream);
	}

	/**
	 * It write an {@link HTTPReply}
	 * 
	 * @param reply
	 */
	@Override
	public void writeHttpReply(HTTPReply reply) {
		StringBuilder myStringBuilder = new StringBuilder();
		buildUpFirstLine(reply, myStringBuilder);
		buildUpHeaderLine(reply.getParameters(), myStringBuilder);
		myStringBuilder.append(reply.getData());
		try {
			outputStream.write(myStringBuilder.toString().getBytes(), 0, myStringBuilder.toString().length());
			outputStream.flush();
		} catch (IOException e) {
			System.err.println("Error while writing reply in the stream!");
			e.printStackTrace();
		}
	}

	/**
	 * It write an {@link HTTPRequest}
	 * 
	 * @param request
	 */
	@Override
	public void writeHttpRequest(HTTPRequest request) {
		StringBuilder myStringBuilder = new StringBuilder();
		buildUpFirstLine(request, myStringBuilder);
		buildUpHeaderLine(request.getParameters(), myStringBuilder);
		myStringBuilder.append(request.getEntityBody());
		try {
			outputStream.write(myStringBuilder.toString().getBytes(), 0, myStringBuilder.toString().length());
			outputStream.flush();
		} catch (IOException e) {
			System.err.println("Error while writing request in the stream!");
			e.printStackTrace();
		}
	}

	/**
	 * Setter
	 * 
	 * It initializes {@link #outputStream} with an {@link OutputStream} {@code os}.
	 */
	@Override
	protected void setOutputStream(OutputStream os) {
		outputStream = new BufferedOutputStream(os);
	}

	/**
	 * Setter
	 * 
	 * It closes {@link #outputStream}.
	 */
	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	/**
	 * It append the parameters of the first line of {@code currentReply} to
	 * {@code myStringBuilder}.
	 * 
	 * @param currentReply
	 * @param myStringBuilder
	 */
	private void buildUpFirstLine(HTTPReply currentReply, StringBuilder myStringBuilder) {
		myStringBuilder.append(currentReply.getVersion());
		myStringBuilder.append(" ");
		myStringBuilder.append(currentReply.getStatusCode());
		myStringBuilder.append(" ");
		myStringBuilder.append(currentReply.getStatusMessage());
		myStringBuilder.append(delimiter);
	}

	/**
	 * It append the parameters of the first line of {@code currentRequest} to
	 * {@code myStringBuilder}
	 * 
	 * @param currentRequest
	 * @param myStringBuilder
	 */
	private void buildUpFirstLine(HTTPRequest currentRequest, StringBuilder myStringBuilder) {
		myStringBuilder.append(currentRequest.getMethod());
		myStringBuilder.append(" ");
		myStringBuilder.append(currentRequest.getPath());
		myStringBuilder.append(" ");
		myStringBuilder.append(currentRequest.getVersion());
		myStringBuilder.append(delimiter);
	}

	/**
	 * It append the parameters of {@code parametersMap} to {@code myStringBuilder}
	 * where the key and the value are separated by ':' and each pair is on a
	 * different line, that is ended by a {@link #delimiter}.
	 * 
	 * @param parametersMap
	 * @param myStringBuilder
	 */
	private void buildUpHeaderLine(Map<String, String> parametersMap, StringBuilder myStringBuilder) {
		StringBuilder finalParametersString = new StringBuilder();
		if (parametersMap != null)
			parametersMap.keySet().forEach(s -> appendParameters(parametersMap, finalParametersString, s));

		myStringBuilder.append(finalParametersString.toString());
		myStringBuilder.append(delimiter);
	}

	private void appendParameters(Map<String, String> parametersMap, StringBuilder finalParametersString,
			String currentkey) {
		finalParametersString.append(currentkey);
		finalParametersString.append(": ");
		finalParametersString.append(parametersMap.get(currentkey));
		finalParametersString.append(delimiter);
	}
}