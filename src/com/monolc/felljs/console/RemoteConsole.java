package com.monolc.felljs.console;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class RemoteConsole {
	private ArrayList<String>	messageBuffer;
	private java.io.Console		c;
	private SocketClient		ws;
	public RemoteConsole() throws URISyntaxException {
		messageBuffer = new ArrayList<String>();
	}
	public void start() {
		c = System.console();
		if (c == null) {
			System.err.println("No console.");
			System.exit(1);
		}
		String ip = c.readLine("Enter server address (leave blank for default): ");
		char[] p = c.readPassword("Enter admin pass: ");
		String pass = String.valueOf(p);
		if (ip.length() == 0) {
			ip = "ws://167.88.120.57:38734";
		}
		try {
			ws = new SocketClient(this, ip, "rc:" + pass);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		ws.connect();
		while (true) {
			String rec = getNextMessage();
			while (rec != null) {
				if (!rec.equals("ignoreme")) {
					System.out.println(rec);
				}
				rec = getNextMessage();
			}
		}
	}
	public void addMessage(String m) {
		synchronized (messageBuffer) {
			messageBuffer.add(m);
		}
	}
	/**
	 * returns a message, or null if there are none left.
	 */
	public String getNextMessage() {
		synchronized (messageBuffer) {
			if (messageBuffer.size() == 0) {
				return null;
			}
			return messageBuffer.remove(0);
		}
	}
}