/* 
 * Fornire l'implementazione per l'interfaccia HTTPServer che implementa, come dice il nome stesso, un server HTTP.  
 * Un istanza di questa classe è ottenibile mediante il metodo 
 * getHTTPServer( int port , int backlog , InetAddress address , HTTPHandler ... handlers)della classe MyHTTPFactory.
 * 
*/

package it.unifi.rc.httpserver.m5793319.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

import it.unifi.rc.httpserver.HTTPHandler;
import it.unifi.rc.httpserver.HTTPServer;

/**
 * This Class implement the {@link HTTPServer} interface
 * 
 * @author Lorenzo Pratesi Mariti, 5793319
 */
public class MyHTTPServer implements HTTPServer {

	private int port;
	private int backlog;
	private InetAddress address;
	private List<HTTPHandler> handlersList;

	private ServerSocket serverSocket;
	private Thread listener;
	private List<HTTPHandler> others;
	private HTTPHandler chain;
	private volatile boolean isOnline;

	/**
	 * Constructor that set the fields, and olso add all handlers incoming to the
	 * handelrsList field
	 * 
	 * @param port
	 * @param backlog
	 * @param address
	 * @param handlers
	 */
	public MyHTTPServer(int port, int backlog, InetAddress address, HTTPHandler[] handlers) {
		this.port = port;
		this.backlog = backlog;
		this.address = address;

		handlersList = importHandlers(handlers);
		handlersList.forEach(handler -> addHandler(handler));
		isOnline = false;
	}

	/**
	 * 
	 * @param handlers
	 * @return a copy of the handlers list
	 */
	private List<HTTPHandler> importHandlers(HTTPHandler[] handlers) {
		return Optional.ofNullable(handlers).map(Arrays::stream).orElseGet(Stream::empty).collect(toList());
	}
 
	/**
	 * Check if the handler in an HTTPHandler Object
	 */
	@Override
	public void addHandler(HTTPHandler handler) {
		if (handler instanceof HTTPHandler) {
			chain = handler;
		} else {
			others.add(handler);
		}
	}

	/**
	 * Start the Server
	 */
	@Override
	public void start() throws IOException {
		if (!isOnline) {
			serverSocket = new ServerSocket(port, backlog, address);
			serverSocket.setReuseAddress(true);
			serverSocket.setSoTimeout(50);

			System.out.println("SERVER: starting server...");
			listener = new AsyncListener(serverSocket, chain, others); 
			listener.start();
			System.out.println("SERVER: online, wating for requests");
			isOnline = true;
		}
	}

	/**
	 * Stops the Server
	 */
	@Override
	public void stop() {
		if (isOnline) {
			listener.interrupt();
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.out.println("-- ERROR --\n" + e.toString());
			}
			isOnline = false;
		}
	}
}
