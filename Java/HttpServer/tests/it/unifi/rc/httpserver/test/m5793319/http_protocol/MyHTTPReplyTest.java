package it.unifi.rc.httpserver.test.m5793319.http_protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPProtocolException;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPReply;
import it.unifi.rc.httpserver.test.m5793319.TestHelper;

public class MyHTTPReplyTest {
	private HTTPReply currentReply;
	private TestHelper helper;

	@Before
	public void init() {
		helper = new TestHelper();
	}

	@Test
	public void httpVersionTest() {
		try {
			currentReply = helper.createReply("HTTP/1.0 200 OK", null, null);
			assertEquals("HTTP/1.0", currentReply.getVersion());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void wrongHttpVersionTest() {
		try {
			currentReply = helper.createReply("HTTP/2.0 OK", null, null);
		} catch (HTTPProtocolException e) {
			assertEquals(400, ((MyHTTPProtocolException) e).getErrorCode());
			return;
		}
		fail();
	}

	@Test
	public void statusCodeTest() {
		try {
			currentReply = helper.createReply("HTTP/1.0 500 server error", null, null);
			assertEquals("500", currentReply.getStatusCode());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void statusMessageTest() {
		try {
			currentReply = helper.createReply("HTTP/1.0 200 OK", null, null);
			assertEquals("OK", currentReply.getStatusMessage());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void dataTest() {
		try {
			currentReply = helper.createReply("HTTP/1.0 200 OK", null, "<html><body><title></title></body></html>");
			assertEquals("<html><body><title></title></body></html>", currentReply.getData());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void parametersTest() {
		try {
			currentReply = helper.createReply("HTTP/1.0 200 OK", "Connection: close\r\nServer: Apache/1.3.27", null);
			assertEquals("close", currentReply.getParameters().get("Connection"));
			assertEquals("Apache/1.3.27", currentReply.getParameters().get("Server"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void wrongParametersTest() {
		try {
			currentReply = helper.createReply("HTTP/1.1 200 OK", "Connection close\r\r\nServer: Apache/1.3.27", null);
		} catch (RuntimeException | HTTPProtocolException e) {
			assertTrue(e.getMessage().contains("400"));
			return;
		}
		fail();
	}

	@Test
	public void wrongFirstLineTest() {
		try {
			currentReply = helper.createReply("HTTP/1.1 200OK", "Connection: close\r\nServer: Apache/1.3.27", null);
		} catch (HTTPProtocolException e) {
			assertEquals(400, ((MyHTTPProtocolException) e).getErrorCode());
			return;
		}
		fail();
	}

	@Test
	public void parseHeadersTest() {
		StringBuilder headersBuilder = MyHTTPReply.parseHeaders();
		assertEquals(true, headersBuilder.toString().contains("Date"));
		assertEquals(true, headersBuilder.toString().contains("Host"));
	}

	@Test
	public void parseHeadersLenghtTest() {
		StringBuilder headersBuilder = MyHTTPReply.parseHeaders();
		assertEquals(true, headersBuilder.toString().length() > 10);
	}
}