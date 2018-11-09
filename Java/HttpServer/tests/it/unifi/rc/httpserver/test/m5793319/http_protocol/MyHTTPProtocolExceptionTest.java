package it.unifi.rc.httpserver.test.m5793319.http_protocol;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import it.unifi.rc.httpserver.m5793319.http_protocol.MyHTTPProtocolException;

public class MyHTTPProtocolExceptionTest {
	MyHTTPProtocolException myException;
	private int errorCode = 400;
	private String errorMsg = "Error message";
	private String verboseErrMsg = "Very long and verbose error message";
	private String wrongEndStringAdded = "wrong end string";

	@Before
	public void resetException() {
		myException = new MyHTTPProtocolException(errorCode, errorMsg, verboseErrMsg);
	}

	@Test
	public void rightCodeTest() {
		assertEquals(errorCode, myException.getErrorCode());
	}

	@Test
	public void wrongCodeTest() {
		assertNotEquals(errorCode+1, myException.getErrorCode());
	}

	@Test
	public void rightMsgTest() {
		assertEquals(errorMsg, myException.getErrorMessage());
	}

	@Test
	public void wrongMsgTest() {
		assertNotEquals(errorMsg, myException.getErrorMessage().concat(wrongEndStringAdded));
	}

	@Test
	public void rightVerboseMessageTest() {
		assertEquals(verboseErrMsg, myException.getFullMessage());
	}

	@Test
	public void wrongVerboseMessageTest() {
		assertNotEquals(verboseErrMsg, myException.getFullMessage().concat(wrongEndStringAdded));
	}
}