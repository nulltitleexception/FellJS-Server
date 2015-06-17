package com.monolc.felljs.console;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class SocketClient extends WebSocketClient {
	RemoteConsole	rc;
	String			fMsg	= null;
	public SocketClient(RemoteConsole r, String address) throws URISyntaxException {
		super(new URI(address));
		rc = r;
	}
	public SocketClient(RemoteConsole r, String address, String firstMsg) throws URISyntaxException {
		super(new URI(address));
		rc = r;
		fMsg = firstMsg;
	}
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		if (fMsg != null) {
			send(fMsg);
		}
	}
	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Connection closed with exit code " + code + " additional info: " + reason);
	}
	@Override
	public void onMessage(String message) {
		rc.addMessage(message);
	}
	@Override
	public void onError(Exception ex) {
		System.err.println("A local error occured:" + ex);
	}
}
