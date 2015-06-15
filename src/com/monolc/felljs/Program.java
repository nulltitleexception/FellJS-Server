package com.monolc.felljs;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;

import org.java_websocket.*;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.monolc.felljs.res.Resources;
import com.monolc.felljs.world.Level;
import com.monolc.felljs.world.LevelGenerator;

public class Program extends WebSocketServer {
	public ArrayList<Client>	clients	= new ArrayList<Client>();
	public Level				level;
	public Program() {
		super(new InetSocketAddress(38734));
		level = LevelGenerator.createDungeonLevel(new Random(), 100, 100);
	}
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		synchronized (clients) {
			clients.add(new Client(this, conn));
		}
	}
	@Override
	public void onMessage(WebSocket conn, String message) {
		JSONObject parsedMessage = (JSONObject) JSONValue.parse(message);
		if (parsedMessage.containsKey("login")) {
			JSONObject login = (JSONObject) parsedMessage.get("login");
			String user = (String) login.get("user");
			String pass = (String) login.get("pass");
			if (pass.length() == 0) {
				synchronized (clients) {
					for (Client c : clients) {
						if (c.connection.equals(conn)) {
							c.validate(true, user);
							System.out.println("Guest \"" + user + "\" connected from \"" + conn.getRemoteSocketAddress() + "\"");
							return;
						}
					}
				}
			}
			synchronized (clients) {
				for (Client c : clients) {
					if (c.connection.equals(conn)) {
						if (Resources.isValidUser(user, pass)) {
							c.validate(false, user);
							System.out.println(user + "\" connected from \"" + conn.getRemoteSocketAddress() + "\"");
							return;
						} else {
							c.errNoKick(100);
						}
					}
				}
			}
			return;
		} else if (parsedMessage.containsKey("add")) {
			JSONObject add = (JSONObject) parsedMessage.get("add");
			String user = (String) add.get("user");
			String pass = (String) add.get("pass");
			if (!Resources.addUser(user, pass)) {
				conn.close(0);
			} else {
				conn.send("valid");
			}
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
		synchronized (clients) {
			boolean cont = true;
			for (int i = 0; i < clients.size() && cont; i++) {
				if (clients.get(i).connection.equals(conn)) {
					clients.get(i).e.remove();
					System.out.println(clients.remove(i).username + " disconected");
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
			if (dt > 100000000.0) {
				if (dt > 1000000000.0) {
					System.out.println("SIGNIFICANT LAG DETECTED! SERVER FPS < 1"); // VERY
																					// CAPS
				} else {
					System.out.println("lag detected: fps < 10");
				}
			}
			frameTime += dt;
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (server.clients) {
				for (Client c : server.clients) {
					if (c.validated) {
						c.update(dt / 1000000000.0, server.clients);
						c.sendData(server.clients);
					}
				}
			}
		}
	}
}
