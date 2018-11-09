package it.unifi.rc.httpserver.test.m5793319.streams;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPInputStream;

public class MyHTTPInputStreamTest {
	private MyHTTPInputStream myHTTPInputStream;
	private HTTPRequest myRequest;
	private HTTPReply myReply;

	// REQUEST
	
	private void buildHeadRequest() {
		prepareInputStream("HEAD /head_directory HTTP/1.0\r\nHost: fakehost.0.0\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0\r\n\r\n Entity body\r\n");
		try {
			myRequest = myHTTPInputStream.readHttpRequest();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void requestHeadMethodNameTest() {
		buildHeadRequest();
		assertEquals("HEAD", myRequest.getMethod());
	}
	
	@Test
	public void requestHeadMethodVersionTest() {
		buildHeadRequest();
		assertEquals("HTTP/1.0", myRequest.getVersion());
	}
	
	@Test
	public void requestHeadMethodParametersTest() {
		buildHeadRequest();
		Map<String, String> mapParameters = new HashMap<>();
		mapParameters.put("Host", "fakehost.0.0");
		mapParameters.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0");
		assertEquals(mapParameters, myRequest.getParameters());
	}
	
	@Test
	public void requestHeadMethodBodyTest() {
		buildHeadRequest();
		assertEquals(true, myRequest.getEntityBody().contains("Entity body"));
	}

	// REPLY
	
	@Test
	public void replyParametersVersionTest() {
		buildOkReply();
		assertEquals("HTTP/1.1", myReply.getVersion());
	}
	
	@Test
	public void replyParametersStatusCodeTest() {
		buildOkReply();
		assertEquals("200", myReply.getStatusCode());
	}
	
	@Test
	public void replyParametersMessageTest() {
		buildOkReply();
		assertEquals("OK", myReply.getStatusMessage());
	}
	
	@Test
	public void replyParametersParametersMapTest() {
		buildOkReply();
		Map<String, String> map = new HashMap<>();
		map.put("Date", "Thu, 20 May 2004 21:12:58 GMT");
		map.put("Connection", "close");
		map.put("Server", "Apache/1.3.27");
		assertEquals(map, myReply.getParameters());
	}
	
	@Test
	public void replyParametersBodyTest() {
		buildOkReply();
		assertEquals("<html> <head> <title> </title> </head> </html>", myReply.getData());
	}
	

	private void buildOkReply() {
		prepareInputStream("HTTP/1.1 200 OK\r\nDate: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27\r\n\r\n<html> <head> <title> </title> </head> </html>");
		try {
			myReply = myHTTPInputStream.readHttpReply();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}


	@Test (expected = NullPointerException.class)
	public void useInputStreamAfterCloseTest() throws IOException {
		myHTTPInputStream.close();
		myHTTPInputStream.readHttpReply();
	}

	private void prepareInputStream(String inputString) {
		myHTTPInputStream = new MyHTTPInputStream(new ByteArrayInputStream(inputString.getBytes()));
	}
}