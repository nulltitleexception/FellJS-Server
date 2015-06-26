package com.monolc.felljs;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.monolc.felljs.console.Console;
import com.monolc.felljs.console.RemoteConsole;
import com.monolc.felljs.res.Resources;
import com.monolc.felljs.world.Entity;
import com.monolc.felljs.world.Level;

public class Program extends WebSocketServer {
	public ArrayList<Client>	clients	= new ArrayList<Client>();
	public Level				level;
	public Program() {
		super(new InetSocketAddress(38734));
		System.out.println("loading level...");
		level = Resources.getLevelGenerator("dungeon").createDungeonLevel(101, 101);
		System.out.println("level loaded.");
	}
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		synchronized (clients) {
			clients.add(new Client(this, conn));
		}
	}
	@Override
	public void onMessage(WebSocket conn, String message) {
		if (message.startsWith("rc")) {
			String pass = message.substring(message.indexOf(':') + 1);
			if (Console.validatePass(pass)) {
				synchronized (clients) {
					boolean cont = true;
					for (int i = 0; i < clients.size() && cont; i++) {
						if (clients.get(i).connection.equals(conn)) {
							if (clients.get(i).e != null) {
								clients.get(i).e.remove();
							}
							Console.clients.add(clients.get(i));
							Console.println(clients.remove(i).connection.getRemoteSocketAddress() + " is now a remote console.");
							cont = false;
						}
					}
				}
			} else {
				synchronized (clients) {
					boolean cont = true;
					for (int i = 0; i < clients.size() && cont; i++) {
						if (clients.get(i).connection.equals(conn)) {
							if (clients.get(i).e != null) {
								clients.get(i).e.remove();
							}
							Console.println(clients.remove(i).connection.getRemoteSocketAddress() + " failed to connect as a remote console. (Invalid login)");
							cont = false;
						}
					}
				}
				conn.close(1000);
			}
			return;
		}
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
							Console.println("Guest \"" + user + "\" connected from \"" + conn.getRemoteSocketAddress() + "\"");
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
							Console.println(user + "\" connected from \"" + conn.getRemoteSocketAddress() + "\"");
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
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).connection.equals(conn)) {
					if (clients.get(i).e != null) {
						clients.get(i).e.remove();
					}
					Console.println(clients.remove(i).username + " disconnected");
					return;
				}
			}
		}
		for (int i = 0; i < Console.clients.size(); i++) {
			if (Console.clients.get(i).connection.equals(conn)) {
				if (Console.clients.get(i).e != null) {
					Console.clients.get(i).e.remove();
				}
				Console.println("Client \"" + Console.clients.remove(i).connection.getRemoteSocketAddress() + "\" disconnected from the Remote Console");
				return;
			}
		}
		Console.println("Unknown Socket Closed!");
	}
	@Override
	public void onError(WebSocket conn, Exception exc) {
		if (exc.getMessage().equals("Connection reset by peer")) {
			return;
		}
		String trace = "";
		for (int i = 0; i < exc.getStackTrace().length; i++) {
			trace += exc.getStackTrace()[i].toString() + (i != exc.getStackTrace().length - 1 ? "\n" : "");
		}
		Console.println("Error: " + exc.getMessage() + ",\n" + trace);
	}
	public static void main(String[] args) {
		if (args.length >= 1 && args[0].equals("remoteconsole")) {
			RemoteConsole rc = null;
			try {
				rc = new RemoteConsole();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			rc.start();
			return;
		}
		Console.init();
		Program server = new Program();
		server.start();
		long startTime = System.nanoTime();
		long frameTime = 0;
		while (true) {
			long dt = System.nanoTime() - (frameTime + startTime);
			if (dt > 100000000.0) {
				if (dt > 1000000000.0) {
					Console.println("SIGNIFICANT LAG DETECTED! SERVER FPS < 1"); // VERY CAPS
				} else {
					Console.println("lag detected: fps < 10");
				}
			}
			frameTime += dt;
			if (dt < 15000000) {
				try {
					Thread.sleep(15 - ((int) (dt / 1000000)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			synchronized (server.level) {
				for (int i = 0; i < server.level.entities.size(); i++) {
					Entity e = server.level.entities.get(i);
					if (e.isQueuedForDeath()) {
						server.level.entities.remove(i);
						i--;
					} else {
						e.update(dt / 1000000000.0);
					}
				}
			}
			synchronized (server.clients) {
				for (Client c : server.clients) {
					if (c.validated) {
						c.sendData();
					}
				}
			}
		}
	}
}
