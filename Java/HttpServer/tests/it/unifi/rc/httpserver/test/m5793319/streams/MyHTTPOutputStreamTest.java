package it.unifi.rc.httpserver.test.m5793319.streams;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import it.unifi.rc.httpserver.HTTPOutputStream;
import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPReply;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPRequest;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPOutputStream;

public class MyHTTPOutputStreamTest {
	private ByteArrayOutputStream myOutputStream;
	private HTTPOutputStream myHttpOutputStream;

	@Before
	public void setUp() {
		myOutputStream = new ByteArrayOutputStream();
		myHttpOutputStream = new MyHTTPOutputStream(myOutputStream);
	}

	@Test
	public void writeFullReplyToOutputStreamTest() {
		buildOkReply("HTTP/1.1 200 OK","Date: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27","body");
		assertEquals("HTTP/1.1 200 OK\r\nDate: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27\r\n\r\nbody", myOutputStream.toString());
	}

	@Test
	public void writReplyWrongStatusLineToOutputStreamTest() {
		buildOkReply("HTTP/1.1 200 OK","Date: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27","body");
		assertNotEquals("HTTP/1.1 100 OK\r\nDate: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27\r\n\r\nbody", myOutputStream.toString());
	}
	
	@Test
	public void writeReplyWrongParametersToOutputStreamTest() {
		buildOkReply("HTTP/1.1 200 OK","Date: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27","body");
		assertNotEquals("HTTP/1.1 200 OK\r\nDate: Thu, 28 Feb 2014 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27\r\n\r\nbody", myOutputStream.toString());
	}
	
	@Test
	public void writeReplyWrongBodyToOutputStreamTest() {
		buildOkReply("HTTP/1.1 200 OK","Date: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\r\nServer: Apache/1.3.27","body");
		assertNotEquals("HTTP/1.1 200 OK\r\nDate: Thu, 20 May 2004 21:12:58 GMT\r\nConnection: close\\r\\nServer: Apache/1.3.27\r\n\r\nwrongbody", myOutputStream.toString());
	}
	
	private void buildOkReply(String statusLine,String headers,String body) {
		try {
			HTTPReply res = new MyHTTPReply(statusLine, headers , body);
			myHttpOutputStream.writeHttpReply(res);
		} catch (HTTPProtocolException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void writeRequestToOutputStream() {
		buildRequest();
		assertEquals("HEAD /head_directory HTTP/1.1\r\nHost: fakehost.0.0\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0\r\n\r\nbody", myOutputStream.toString());
	}
	
	@Test
	public void writeRequestWrongVersionToOutputStream() {
		buildRequest();
		assertNotEquals("HEAD /head_directory HTTP/2.0\r\nHost: fakehost.0.0\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0\r\n\r\nbody", myOutputStream.toString());
	}
	
	@Test
	public void writeRequestWrongHostToOutputStream() {
		buildRequest();
		assertNotEquals("HEAD /head_directory HTTP/2.0\r\nHost: wrong_fakehost.0.0\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0\r\n\r\nbody", myOutputStream.toString());
	}
	
	@Test
	public void writeRequestWrongBodyToOutputStream() {
		buildRequest();
		assertNotEquals("HEAD /head_directory HTTP/2.0\r\nHost: fakehost.0.0\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0\r\n\r\nwrong_body", myOutputStream.toString());
	}

	private void buildRequest() {
		try {
			HTTPRequest req = new MyHTTPRequest("HEAD /head_directory HTTP/1.1", "Host: fakehost.0.0\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0", "body");
			myHttpOutputStream.writeHttpRequest(req);
		} catch (HTTPProtocolException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test (expected = NullPointerException.class)
	public void useInputStreamAfterCloseTest() throws IOException {
		myHttpOutputStream.close();
		myHttpOutputStream.writeHttpReply(new MyHTTPReply(null, null, null));
	}
}