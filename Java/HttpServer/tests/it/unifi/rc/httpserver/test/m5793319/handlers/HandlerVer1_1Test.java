package it.unifi.rc.httpserver.test.m5793319.handlers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import it.unifi.rc.httpserver.HTTPProtocolException;
import it.unifi.rc.httpserver.HTTPReply;
import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPRequest;
import it.unifi.rc.httpserver.test.m5793319.TestHelper;

public class HandlerVer1_1Test {
	private HTTPReply currentReply;
	private TestHelper helper;

	@Before
	public void init() {
		helper = new TestHelper();
	}

	@Test
	public void getFileOnRootDirectoryTest() {
		currentReply = helper.processRequestWithHandler1_1("tests/root", "GET /root_file_html.html HTTP/1.1");
		assertEquals("200", currentReply.getStatusCode());
		assertEquals("OK", currentReply.getStatusMessage());
		assertEquals(true, currentReply.getData().contains("<!DOCTYPE html><html><body><h1>Heading</h1><p>A paragraph.</p></body></html>"));
	}

	@Test
	public void getFileOnSubDirectoryTest() {		
		currentReply = helper.processRequestWithHandler1_1("tests/root", "GET /sub-directory/sub-directory_file_html.html HTTP/1.1");
		assertEquals("200", currentReply.getStatusCode());
		assertEquals("OK", currentReply.getStatusMessage());
		assertEquals(true, currentReply.getData().contains("<!DOCTYPE html><html><body><h1>Heading</h1><p>A paragraph.</p></body></html>"));
	}
	
	@Test
	public void getFileOnSubDirectoryVersion1_0Test() {
		currentReply = helper.processRequestWithHandler1_1("tests/root", "GET /sub-directory/sub-directory_file_html.html HTTP/1.0");
		assertEquals("200", currentReply.getStatusCode());
		assertEquals("OK", currentReply.getStatusMessage()); 
		assertEquals(true, currentReply.getData().contains("<!DOCTYPE html><html><body><h1>Heading</h1><p>A paragraph.</p></body></html>"));
	}
	
	
		@Test
		public void headRequestTest() {		
			currentReply = helper.processRequestWithHandler1_1("tests/root", "HEAD /head_folder HTTP/1.1");
			assertEquals("200", currentReply.getStatusCode());
			assertEquals("OK", currentReply.getStatusMessage());
		}
	
	
	@Test
	public void postFileInDirectoryTest() {
		try {
			OutputStream myOutputStream = new FileOutputStream("tests/root/sub-directory/file_from_post_method");
			myOutputStream.write("".getBytes());
			myOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentReply = helper.processRequestWithHandler1_1("tests/root", "POST /sub-directory/file_from_post_method HTTP/1.1");
		assertEquals("204", currentReply.getStatusCode());
		assertEquals("No Content", currentReply.getStatusMessage());
	}
	
	

	public void deleteFileBefore(String file) {
		if(new File(file).exists()) {
			new File(file).delete();
		}
	}
	
		@Test
	public void putWithNoContentTest() {
		currentReply = helper.processRequestWithHandler1_1("tests/root", "PUT /file_from_put_method.html HTTP/1.1");
		assertEquals("204", currentReply.getStatusCode());
		assertEquals("No Content", currentReply.getStatusMessage());
	}
	
	@Test
	public void putFileInRootDirectoryTest() {
		deleteFileBefore("tests/root/file_from_put_method.html");
		currentReply = helper.processRequestWithHandler1_1("tests/root", "PUT /file_from_put_method.html HTTP/1.1");
		assertEquals("201", currentReply.getStatusCode());
		assertEquals("Created", currentReply.getStatusMessage());
	}

	public void createFileBefore(String file) {
		if (!new File(file).exists()) {
			try {
				new File(file).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

		
	@Test
	public void deleteFileInRootDirectoryTest(){
		createFileBefore("tests/root/file_from_delete_method.html");
		try {
			currentReply = helper.deleteFileFromRootWithCustomRequestHandler1_1("tests/root", new MyHTTPRequest("DELETE /file_from_delete_method.html HTTP/1.1", "Fake: fake\r\nAuthentication: correct_password123", null));
		}catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("202", currentReply.getStatusCode());
		assertEquals("Accepted", currentReply.getStatusMessage());
	}
	
	@Test 
	public void deleteFileWithNoContentTest() {
		try {
			currentReply = helper.deleteFileFromRootWithCustomRequestHandler1_1("tests/root", new MyHTTPRequest("DELETE /file_from_delete_method.html HTTP/1.1", "Fake: fake\r\nAuthentication: correct_password123", null));
			assertEquals("204", currentReply.getStatusCode());
			assertEquals("No Content", currentReply.getStatusMessage());
		} catch (HTTPProtocolException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void deleteFileWithWrongAutenticationTest() {
		createFileBefore("tests/root/file_from_delete_method.html");
		try {
			currentReply = helper.deleteFileFromRootWithCustomRequestHandler1_1("tests/root", new MyHTTPRequest("DELETE /file_from_delete_method.html HTTP/1.1", "Fake: fake\r\nAuthentication: wrong_password", null));
		} catch (HTTPProtocolException e) {
			e.printStackTrace();
		}
		assertEquals("401", currentReply.getStatusCode());
		assertEquals("Unauthorized", currentReply.getStatusMessage());
		new File("tests/res_root/will_delete.html").delete();
	}
	
		@Test
		public void getWithWrongHostNameTest() {
			currentReply = helper.processRequestWithHandler1_1WithHostName("tests/root", "panino", "GET /root_file_html.html HTTP/1.1");
			assertNull(currentReply);
		}

		@Test
		public void getWithEmptyHostNameTest() {
			currentReply = helper.processRequestWithHandler1_1WithHostName("tests/root", "", "GET /root_file_html.html HTTP/1.1");
			assertNull(currentReply);
		}

	
	@Test
	public void wrongMethodNameCallTest() {
		currentReply = helper.processRequestWithHandler1_1("tests/res_root", "TRACE /delete_me.html HTTP/1.1");
		assertEquals("501", currentReply.getStatusCode());
		assertEquals("Not Implemented", currentReply.getStatusMessage());
	}

	@Test
	public void getWithWrongHttpVersionTest() {
		currentReply = helper.processRequestWithHandler1_1("", "GET /url HTTP/2.0");
		assertEquals("505", currentReply.getStatusCode());
		assertEquals("HTTP Version Not Supported", currentReply.getStatusMessage());
	}

	@Test 
	public void wrongNameMethodTest() {
		currentReply = helper.processRequestWithHandler1_1("tests/root", "SBIRU /methor_error.html HTTP/1.0");
		assertEquals("501", currentReply.getStatusCode());
		assertEquals("Not Implemented", currentReply.getStatusMessage());
	}

}