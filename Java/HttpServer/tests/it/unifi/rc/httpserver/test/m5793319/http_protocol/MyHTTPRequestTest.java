package it.unifi.rc.httpserver.test.m5793319.http_protocol;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPRequest;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPProtocolException;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPRequest;
import it.unifi.rc.httpserver.test.m5793319.TestHelper;

public class MyHTTPRequestTest {
	private HTTPRequest currentRequest;
	private TestHelper helper;

	@Before
	public void init() {
		helper = new TestHelper();
	}

	@Test
	public void httpVersionTest() {
		try {
			currentRequest = helper.createRequest("GET /get_directory HTTP/1.0", null, null);
			assertEquals("HTTP/1.0", currentRequest.getVersion());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void wrongFirstLineTest() {
		try {
			new MyHTTPRequest("GET/get_directory HTTP/1.0", null, null);
		} catch (HTTPProtocolException e) {
			assertEquals(400, ((MyHTTPProtocolException) e).getErrorCode());
			return;
		}
		fail();
	}

	@Test
	public void methodNameTest() {
		try {
			currentRequest = helper.createRequest("GET /get_directory HTTP/1.0", null, null);
			assertEquals("GET", currentRequest.getMethod());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void pathTest() {
		try {
			currentRequest = helper.createRequest("POST /post_directory HTTP/1.0", null, null);
			assertEquals("/post_directory", currentRequest.getPath());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void bodyTest() {
		try {
			currentRequest = helper.createRequest("POST /post_directory HTTP/1.0", null, "body");
			assertEquals("body", currentRequest.getEntityBody());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void parametersTest() {
		try {
			currentRequest = helper.createRequest("POST /post_directory HTTP/1.0", "Connection: Keep-Alive\r\nUser-Agent: myBrowser", null);
			System.out.println(currentRequest.getParameters().get("Connection"));
			assertEquals("Keep-Alive", currentRequest.getParameters().get("Connection"));
			assertEquals("myBrowser", currentRequest.getParameters().get("User-Agent"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}

	@Test
	public void wrongParametersDelimiterTest() {
		try {
			currentRequest = helper.createRequest("POST /post_directory HTTP/1.0", "Connection: Keep-Alive\n\n User-Agent: myBrowser", null);
		} catch (HTTPProtocolException e) {
			assertEquals(400, ((MyHTTPProtocolException) e).getErrorCode());
			return;
		}
		fail();
	}

	@Test
	public void wrongParametersNameTest() {
		try {
			currentRequest = helper.createRequest("POST /post_directory HTTP/1.0", "Connection.... Keep-Alive\r\n User-Agent.. myBrowser", null);
		} catch (RuntimeException | HTTPProtocolException e) {	
			assertTrue(e.getMessage().contains("400"));
			return;
		}
		fail();
	}
}