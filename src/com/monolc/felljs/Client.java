package com.monolc.felljs;

import java.util.ArrayList;

import org.java_websocket.*;

import com.monolc.felljs.physics.Rect2D;

public class Client {
	public WebSocket connection;
	public boolean validated = false;
	public boolean guest = false;
	public Rect2D box;
	boolean[] isKeyDown = new boolean[256];

	public Client(WebSocket conn) {
		connection = conn;
		box = new Rect2D(10, 10, 30, 50);
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

	public boolean checkCollisions(ArrayList<Client> clients) {
		for (Client c : clients) {
			if (!c.connection.equals(connection) && c.validated
					&& box.intersects(c.box)) {
				return true;
			}
		}
		return false;
	}

	public boolean fixCollisions(ArrayList<Client> clients) {
		for (Client c : clients) {
			if (!c.connection.equals(connection) && c.validated
					&& box.intersects(c.box)) {
				Rect2D intrsct = box.getIntersect(c.box);
				if (intrsct.w < intrsct.h) {
					if (box.x < c.box.x) {
						box.x -= intrsct.w;
					} else {
						box.x += intrsct.w;
					}
				} else {
					if (box.y < c.box.y) {
						box.y -= intrsct.h;
					} else {
						box.y += intrsct.h;
					}
				}
			}
		}
		return false;
	}

	public void update(double dt, ArrayList<Client> clients) {
		double speed = 300 * dt;
		double ymod = 0;
		double xmod = 0;
		if (isKeyDown['W']) {
			ymod -= speed;
		}
		if (isKeyDown['S']) {
			ymod += speed;
		}
		if (isKeyDown['A']) {
			xmod -= speed;
		}
		if (isKeyDown['D']) {
			xmod += speed;
		}
		if (box.x + xmod < 1) {
			xmod = 0;
		}
		if (box.y + ymod < 1) {
			ymod = 0;
		}
		if (checkCollisions(clients)) {
			System.out.println("LOGIC ERROR!");
		}
		box.x += xmod;
		box.y += ymod;
		fixCollisions(clients);
	}

	public String getData() {
		return (int) box.x + "," + (int) box.y;
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
