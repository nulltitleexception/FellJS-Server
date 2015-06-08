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
	}
	
	public void spawnIn(){
		Random random = new Random();
		Color C = Color.getHSBColor(random.nextFloat(), 0.9f, 0.9f);
		String color = String.format("#%02X%02X%02X", C.getRed(), C.getGreen(),
				C.getBlue());
		e = new Entity(server.world ,new Rect2D(10, 10, 32, 32), color, username, 10);
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
		boolean xmove = false;
		boolean ymove = false;
		if (isKeyDown['W']) {
			ymod -= speed;
			ymove = !ymove;
		}
		if (isKeyDown['S']) {
			ymod += speed;
			ymove = !ymove;
		}
		if (isKeyDown['A']) {
			xmod -= speed;
			xmove = !xmove;
		}
		if (isKeyDown['D']) {
			xmod += speed;
			xmove = !xmove;
		}
		if (xmove && ymove){
			xmod /= Math.sqrt(2.0);
			ymod /= Math.sqrt(2.0);
		}
		e.move(xmod, ymod);
	}

	public int getDataStride() {
		return 7;
	}

	public void sendData(ArrayList<Client> clients) {
		if (!validated) {
			return;
		}
		String entityData = server.world.toString();
		connection.send("pos:" + e);
		if (entityData.length() > 0) {
			connection.send("dat" + getDataStride() + ":"
					+ entityData);
		}
	}
}
