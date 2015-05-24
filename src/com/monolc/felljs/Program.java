package com.monolc.felljs;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.java_websocket.*;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Program extends WebSocketServer {
	public ArrayList<Client> clients = new ArrayList<Client>();

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
		while (true) {
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Client c : server.clients) {
				c.update();
				synchronized (server.clients) {
					c.sendData(server.clients);
				}
			}
		}
	}
}
