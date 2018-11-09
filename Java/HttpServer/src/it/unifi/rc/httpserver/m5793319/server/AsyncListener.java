/**
 * 
 */
package it.unifi.rc.httpserver.m5793319.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import it.unifi.rc.httpserver.HTTPHandler;
import it.unifi.rc.httpserver.HTTPInputStream;
import it.unifi.rc.httpserver.HTTPOutputStream;
import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPInputStream;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPOutputStream;

/**
 * Class that implements an asynchronous listener for the HTTP Server.
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 *
 */
public class AsyncListener extends Thread {
	private ServerSocket serverSocket;
	private HTTPHandler chain;
	private List<HTTPHandler> others;
	private volatile boolean alive;

	/**
	 * Constructor.
	 * 
	 * @param serverSocket
	 * @param chain
	 * @param otherHandlersList
	 */
	public AsyncListener(ServerSocket serverSocket, HTTPHandler chain, List<HTTPHandler> otherHandlers) {
		this.serverSocket = serverSocket;
		this.chain = chain;
		this.others = otherHandlers;
		this.alive = false;
	}

	/**
	 * Listens for the incoming requests on the server socket, manage them ad
	 * answers the reply.
	 */
	@Override
	public void run() {
		Socket client = null;
		alive = true;
		while (alive) {
			try {
				client = accept();
				if (client != null) {
					System.out.println("Serving new client...");
					serve(client);
					client.close();
					System.out.println("Client Served!");
				}
			} catch (IOException e) {
				System.out.println("AsyncListener Stopped!");
			}
		}
	}

	/**
	 * Interrupts the thread.
	 */
	@Override
	public void interrupt() {
		alive = false;
	}

	/**
	 * Accept an incoming request. If timeout expires catch the exception and
	 * returns null.
	 * 
	 * @return the client socket or null if timeout expires.
	 * @throws IOException
	 */
	private Socket accept() throws IOException {
		Socket client = null;
		try {
			client = serverSocket.accept();
		} catch (SocketTimeoutException e) {
		}
		return client;
	}

	private void serve(Socket client) throws IOException {
		HTTPInputStream inputStream = new MyHTTPInputStream(client.getInputStream());
		HTTPOutputStream outputStream = new MyHTTPOutputStream(client.getOutputStream());

		HTTPRequest request = getRequest(inputStream, outputStream);
		if (request != null) {
			HTTPReply reply = askForReply(request);
			outputStream.writeHttpReply(reply);
		}
	}

	/**
	 * Method that reads a request from the socket
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @return the HTTP request
	 */
	private HTTPRequest getRequest(HTTPInputStream inputStream, HTTPOutputStream outputStream) {
		HTTPRequest req = null;
		try {
			req = inputStream.readHttpRequest();
		} catch (HTTPProtocolException e) {
			System.out.println("-- ERROR --\n" + e.getMessage());
		}
		return req;
	}

	/**
	 * Method that looks for reply, sending the request to the specific handler
	 * 
	 * @param request
	 * @return the HTTP reply
	 */
	private HTTPReply askForReply(HTTPRequest request) {
		HTTPReply reply = sendToChain(request);
		if (reply != null) {
			return reply;
		}
		reply = sendToList(request);
		if (reply != null) {
			return reply;
		}
		return reply;
	}

	/**
	 * Method that sent request to the handler chain
	 * 
	 * @param request
	 * @return an HTTP reply
	 */
	private HTTPReply sendToChain(HTTPRequest request) {
		return chain.handle(request);
	}

	/**
	 * Method that send a request to the handler list
	 * 
	 * @param request
	 * @return
	 */
	private HTTPReply sendToList(HTTPRequest request) {
		return others.stream()
					 .map(req -> req.handle(request))
					 .filter(req -> req != null)
					 .findFirst()
					 .get();
	}
}
