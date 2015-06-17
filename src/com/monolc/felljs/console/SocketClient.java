package com.monolc.felljs.console;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

public class SocketClient extends WebSocketClient {
	RemoteConsole	rc;
	public SocketClient(RemoteConsole r, String address) throws URISyntaxException {
		super(new URI(address), new Draft_10());
		rc = r;
	}
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Connected.");
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
