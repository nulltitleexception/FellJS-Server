package com.monolc.felljs;

import java.util.ArrayList;

import org.java_websocket.*;

public class Client {
	public WebSocket connection;
	public boolean validated = false;
	public double x, y;
	boolean[] isKeyDown = new boolean[256];

	public Client(WebSocket conn) {
		connection = conn;
		x = 10;
		y = 10;
	}

	public void handleInput(String msg) {
		if (!msg.startsWith("keys:")) {
			System.out.println("invalid input.");
			return;
		}
		msg = msg.replace("keys:", "");
		for (int i = 0; i < msg.length() && i < isKeyDown.length; i++) {
			isKeyDown[i] = msg.charAt(i) == '1';
		}
	}

	public void update(double dt) {
		double speed = 300 * dt;
		if (isKeyDown['W']) {
			y -= speed;
		}
		if (isKeyDown['A']) {
			x -= speed;
		}
		if (isKeyDown['S']) {
			y += speed;
		}
		if (isKeyDown['D']) {
			x += speed;
		}
		if (x < 1) {
			x = 1;
		}
		if (y < 1) {
			y = 1;
		}
	}

	public String getData() {
		return (int) x + "," + (int) y;
	}

	public void sendData(ArrayList<Client> clients) {
		if (!validated) {
			return;
		}
		String clientData = "";
		for (Client c : clients) {
			if (!c.connection.equals(connection) && c.validated) {
				clientData += c.getData() + ",";
			}
		}
		connection.send("pos:" + getData());
		if (clientData.length() > 0) {
			connection.send("dat:"
					+ clientData.substring(0, clientData.length() - 1));
		}
	}
}
