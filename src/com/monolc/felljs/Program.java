package com.monolc.felljs;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.java_websocket.*;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Program extends WebSocketServer {
	public ArrayList<Client> clients = new ArrayList<Client>();
	public String[] users = { "Null", "Zyber17", "Guest" };
	public String[] passwords = { "mnlc", "c0rbs", "" };

	public Program() {
		super(new InetSocketAddress(38734));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("Client Connected");
		synchronized (clients) {
			clients.add(new Client(conn));
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		if (message.startsWith("login:")) {
			// delete below here
			/*
			 * synchronized (clients) { for (Client c : clients) { if
			 * (c.connection.equals(conn)) { c.validated = true; return; } } }
			 */
			// delete above here
			String login = message.replace("login:", "");
			String user = login.substring(0, login.indexOf(","));
			String pass = login.substring(login.indexOf(",") + 1);
			boolean exists = false;
			for (int i = 0; i < users.length && !exists; i++) {
				if (user.equalsIgnoreCase(users[i])
						&& pass.equals(passwords[i])) {
					exists = true;
				}
			}
			if (!exists) {
				conn.close(0);
				;
			} else {
				synchronized (clients) {
					for (Client c : clients) {
						if (c.connection.equals(conn)) {
							c.validated = true;
							return;
						}
					}
				}
			}
			return;
		}
		synchronized (clients) {
			for (Client c : clients) {
				if (c.connection.equals(conn)) {
					c.handleInput(message);
					return;
				}
			}
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.print("Client Discon");
		synchronized (clients) {
			boolean cont = true;
			for (int i = 0; i < clients.size() && cont; i++) {
				if (clients.get(i).connection.equals(conn)) {
					clients.remove(i);
					System.out.println("nected");
					cont = false;
				}
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception exc) {
		System.out.println("Error: " + exc.getMessage());
	}

	public static void main(String[] args) {
		Program server = new Program();
		server.start();
		long startTime = System.nanoTime();
		long frameTime = 0;
		while (true) {
			long dt = System.nanoTime() - (frameTime + startTime);
			frameTime += dt;
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (server.clients) {
				for (Client c : server.clients) {
					c.update(dt / 1000000000.0, server.clients);
					c.sendData(server.clients);
				}
			}
		}
	}
}
