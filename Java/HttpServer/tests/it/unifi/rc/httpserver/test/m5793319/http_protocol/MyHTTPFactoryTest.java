package it.unifi.rc.httpserver.test.m5793319.http_protocol;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import it.unifi.rc.httpserver.HTTPOutputStream;
import it.unifi.rc.httpserver.HTTPServer;
import it.unifi.rc.httpserver.m5793319.MyHTTPFactory;
import it.unifi.rc.httpserver.m5793319.handlers.HandlerVer1_0;
import it.unifi.rc.httpserver.m5793319.handlers.HandlerVer1_1;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPInputStream;
import it.unifi.rc.httpserver.m5793319.streams.MyHTTPOutputStream;

public class MyHTTPFactoryTest {

	private MyHTTPFactory myhttpFactory;

	@Before
	public void setUp() {
		myhttpFactory = new MyHTTPFactory();
	}

	private BufferedInputStream initBufferedInputStream() {
		return new BufferedInputStream(new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		});
	}

	private BufferedOutputStream initBufferedOutputStream() {

		return new BufferedOutputStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		});
	}

	@Test
	public void getHTTPInputStreamTest() {
		BufferedInputStream inputStream = initBufferedInputStream();
		assertTrue(myhttpFactory.getHTTPInputStream(inputStream) != null);
	}

	@Test
	public void getHTTPInputStreamReturnsAnInstanceOfHTTPInputStreamTest() {
		BufferedInputStream inputStream = initBufferedInputStream();
		MyHTTPInputStream myHTTPInputStream = new MyHTTPInputStream(inputStream);
		assertTrue(myhttpFactory.getHTTPInputStream(inputStream).getClass().isInstance(myHTTPInputStream));
	}

	@Test
	public void getHTTPOutputStreamtTest() {
		BufferedOutputStream outputStream = initBufferedOutputStream();
		assertTrue(myhttpFactory.getHTTPOutputStream(outputStream) != null);
	}

	@Test
	public void getHTTPOutputStreamReturnsAnInstanceOfHTTPOutputStreamTest() {
		BufferedOutputStream outputStream = initBufferedOutputStream();
		HTTPOutputStream myHTTPOutputStream = new MyHTTPOutputStream(outputStream);
		assertTrue(myhttpFactory.getHTTPOutputStream(outputStream).getClass().isInstance(myHTTPOutputStream));
	}

	@Test
	public void getHTTPServerTest() {
		try {
			File root = new File("myroots/server");
			if (!root.exists()) {
				root.mkdir();
			}
			HandlerVer1_0 pippo = new HandlerVer1_0(root, "pippo.com");
			HandlerVer1_0 example = new HandlerVer1_0(root, "example.com");
			HandlerVer1_0 all = new HandlerVer1_0(root);
			HandlerVer1_0[] handlers = { pippo, example, all };

			HTTPServer httpServer = myhttpFactory.getHTTPServer(12000, 10, InetAddress.getByName("127.0.0.1"),
					handlers);

			assertTrue(httpServer != null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void getFileSystemHandler1_0ReturnsAnInstanceOfHandlerVer1_0Test() {
		assertTrue(myhttpFactory.getFileSystemHandler1_0(new File("root")).getClass()
				.isInstance(new HandlerVer1_0(new File("root"))));
	}

	@Test
	public void getFileSystemHandler1_0NotNullTest() {
		assertTrue(myhttpFactory.getFileSystemHandler1_0(new File("root")) != null);
	}

	@Test
	public void getFileSystemHandler1_0WithHostReturnsAnInstanceOfHandlerVer1_0Test() {
		assertTrue(myhttpFactory.getFileSystemHandler1_1("host", new File("root")).getClass()
				.isInstance(new HandlerVer1_1(new File("root"), "host")));
	}

	@Test
	public void getFileSystemHandler1_0WithHostNotNullTest() {
		assertTrue(myhttpFactory.getFileSystemHandler1_1("host", new File("root")) != null);
	}

	@Test
	public void getFileSystemHandler1_1ReturnsAnInstanceOfHandlerVer1_1Test() {
		assertTrue(myhttpFactory.getFileSystemHandler1_1(new File("root")).getClass()
				.isInstance(new HandlerVer1_1(new File("root"))));
	}

	@Test
	public void getFileSystemHandler1_1NotNullTest() {
		assertTrue(myhttpFactory.getFileSystemHandler1_0(new File("root")) != null);
	}

	@Test
	public void getFileSystemHandler1_1WithHostReturnsAnInstanceOfHandlerVer1_1Test() {
		assertTrue(myhttpFactory.getFileSystemHandler1_1("host", new File("root")).getClass()
				.isInstance(new HandlerVer1_1(new File("root"), "host")));
	}

	@Test
	public void getFileSystemHandler1_1WithHostNotNullTest() {
		assertTrue(myhttpFactory.getFileSystemHandler1_1("host", new File("root")) != null);
	}

	@Test
	public void getProxyHandlerTest() {
		assertTrue(myhttpFactory.getProxyHandler() == null);
	}

}
