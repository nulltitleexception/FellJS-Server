package com.monolc.felljs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.java_websocket.*;

import com.monolc.felljs.physics.Rect2D;
import com.monolc.felljs.world.Entity;

public class Client {
	public Program server;
	public WebSocket connection;
	public boolean validated = false;
	public boolean guest = false;
	public String username = null;
	boolean[] isKeyDown = new boolean[256];
	Entity e;

	public Client(Program s, WebSocket conn) {
		server = s;
		connection = conn;
		Random random = new Random();
		Color C = Color.getHSBColor(random.nextFloat(), 0.9f, 0.9f);
		String color = String.format("#%02X%02X%02X", C.getRed(), C.getGreen(),
				C.getBlue());
		e = new Entity(s.world ,new Rect2D(10, 10, 32, 32), color, 10);
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
		e.move(xmod, ymod);
	}

	public String getData() {
		return (int) e.box.x + "," + (int) e.box.y + "," + (int) e.box.w + ","
				+ (int) e.box.h + "," + e.color + ","
				+ (username != null ? username : "SERVER_ERROR") + ","
				+ e.health;
	}

	public int getDataStride() {
		return 7;
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
			connection.send("dat" + getDataStride() + ":"
					+ clientData.substring(0, clientData.length() - 1));
		}
	}
}
