package it.unifi.rc.httpserver.test.m5793319.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.Test;

import it.unifi.rc.httpserver.HTTPInputStream;
import it.unifi.rc.httpserver.HTTPOutputStream;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPRequest;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPInputStream;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPOutputStream;


public class ClientV1Launcher {
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int SERVER_PORT = 12000;
	
	private String s = "HEAD /head_directory HTTP/1.0\r\nHost: fakehost.0.0\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0\r\n\r\n Entity body\r\n";
	

	/**
	 * Fails only if an exception occurs.
	 */
	@Test
	public void test() {
		try {
			System.out.println("-- CLIENT --");			
			new MyHTTPRequest("GET /example.txt HTTP/1.1", "Host: example.com\r\n\r\n", null);
			
			HTTPRequest request = new MyHTTPRequest("GET /get_directory HTTP/1.1", "Connection: Keep-Alive\r\nUser-Agent: myBrowser", null);
			new MyHTTPRequest("GET /get_directory HTTP/1.0", null, null);
		
			sendRequest(request);
		} catch (Exception e) {
			System.out.println("--- ERROR ---\n" + e.toString());
			e.printStackTrace();
			fail();
		}
	}

	private void sendRequest(HTTPRequest... req) throws InterruptedException {
		int i = 0;
		for(HTTPRequest r : req) {
			sendToServer(i, r);
		}
		System.out.println("--- END ---");
	}

	private void sendToServer(int i, HTTPRequest r) throws InterruptedException {
		System.out.println("\n--- REQUEST " + Integer.toString(i++) + " ---");
		System.out.println(r.toString().replaceAll("(^\\[|\\]$)", ""));
		HTTPReply reply = elaborateAndSend(r);
		
		System.out.println("-- REPLY --");
		assertNotNull(reply);
		System.out.println(reply.toString());
		Thread.sleep(1000);
	}

	private HTTPReply elaborateAndSend(HTTPRequest request) {
		HTTPReply reply = null;
		try {
			Socket server = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
			HTTPOutputStream out = new MyHTTPOutputStream(server.getOutputStream());
			HTTPInputStream in = new MyHTTPInputStream(new ByteArrayInputStream(s.getBytes()));
			out.writeHttpRequest(request);
			reply = in.readHttpReply();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reply;
	}

}
