package it.unifi.rc.httpserver.test.m5793319.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.test.m5793319.TestHelper;

public class HandlerVer1_0Test {
	private HTTPReply currentReply;
	private TestHelper helper;

	@Before
	public void init() {
		helper = new TestHelper();
	}
	
	@Test
	public void testNullReply() {
		currentReply = helper.processNullRequest0("tests/root", "HEAD /head_folder HTTP/1.0");
		assertNull(currentReply);
	}



	@Test
	public void headRequestTest() {
		currentReply = helper.processRequestWithHandler1_0("tests/root", "HEAD /head_folder HTTP/1.0");
		assertEquals("200", currentReply.getStatusCode());
		assertEquals("OK", currentReply.getStatusMessage());
	}

		@Test
	public void getFileFromRootTest() {
		currentReply = helper.processRequestWithHandler1_0("tests/root", "GET /root_file_html.html HTTP/1.0");
		assertEquals("200", currentReply.getStatusCode());
		assertEquals("OK", currentReply.getStatusMessage());
		assertEquals(true, currentReply.getData().toString()
				.contains("<!DOCTYPE html><html><body><h1>Heading</h1><p>A paragraph.</p></body></html>"));
	}

	@Test
	public void getFileFromSubdirectoryTest() {
		currentReply = helper.processRequestWithHandler1_0("tests/root",
				"GET /sub-directory/sub-directory_file_html.html HTTP/1.0");
		assertEquals("200", currentReply.getStatusCode());
		assertEquals("OK", currentReply.getStatusMessage());
		assertEquals(true, currentReply.getData()
				.contains("<!DOCTYPE html><html><body><h1>Heading</h1><p>A paragraph.</p></body></html>"));
	}

	@Test
	public void getWithWrongPathTest() {
		currentReply = helper.processRequestWithHandler1_0("tests/root", "GET /stuff_not_there.html HTTP/1.0");
		assertEquals("404", currentReply.getStatusCode());
		assertEquals("Not Found", currentReply.getStatusMessage());
	}



	@Test
	public void postFileToDirectoryTest() {
		try {
			OutputStream myOutputStream = new FileOutputStream("tests/root/sub-directory/file_from_post_method");
			myOutputStream.write("".getBytes());
			myOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentReply = helper.processRequestWithHandler1_0("tests/root",
				"POST /sub-directory/file_from_post_method HTTP/1.0");
		assertEquals("204", currentReply.getStatusCode());
		assertEquals("No Content", currentReply.getStatusMessage());
	}

	@Test
	public void postFileToDirectoryWrongDirectoryNameTest() {
		try {
			OutputStream myOutputStream = new FileOutputStream("tests/root/sub-directory/file_from_post_method");
			myOutputStream.write("".getBytes());
			myOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentReply = helper.processRequestWithHandler1_0("tests/root_WRONG",
				"POST /sub-directory/file_from_post_method HTTP/1.0");
		assertEquals("404", currentReply.getStatusCode());
		assertEquals("Not Found", currentReply.getStatusMessage());
	}

	@Test
	public void postNoFileTest() {
		currentReply = helper.processRequestWithHandler1_0("tests",
				"POST /sub-directory/file_from_post_method HTTP/1.0");
		assertEquals("404", currentReply.getStatusCode());
		assertEquals("Not Found", currentReply.getStatusMessage());
	}

	@Test
	public void wrongHostNameTest() {
		currentReply = helper.processRequestWithHandler1_0WithHostName("tests/root", "wrongHost",
				"GET /root_file_html.html HTTP/1.0");
		assertNull(currentReply);
	}

	@Test
	public void emptyHostNameTest() {
		currentReply = helper.processRequestWithHandler1_0WithHostName("tests/root", "",
				"GET /root_file_html.html HTTP/1.0");
		assertNull(currentReply);
	}

	@Test
	public void emptyRootPathTest() {
		currentReply = helper.processRequestWithHandler1_0("", "GET /url HTTP/1.1");
		assertEquals("505", currentReply.getStatusCode());
		assertEquals("HTTP Version Not Supported", currentReply.getStatusMessage());
	}

	@Test
	public void emptyRootPathAndWrongVersionTest() {
		currentReply = helper.processRequestWithHandler1_0("", "DELETE /url HTTP/1.0");
		assertEquals("501", currentReply.getStatusCode());
		assertEquals("Not Implemented", currentReply.getStatusMessage());
	}

	@Test
	public void wrongMethodNameTest() {
		currentReply = helper.processRequestWithHandler1_0("tests/root", "SBIRU /methor_error.html HTTP/1.0");
		assertEquals("501", currentReply.getStatusCode());
		assertEquals("Not Implemented", currentReply.getStatusMessage());
	}

}