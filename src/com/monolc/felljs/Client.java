package com.monolc.felljs;

import java.awt.Color;
import java.util.Random;

import org.java_websocket.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.monolc.felljs.ai.EntityAI;
import com.monolc.felljs.console.Console;
import com.monolc.felljs.physics.Rect2D;
import com.monolc.felljs.world.Entity;

public class Client implements EntityAI {
	public Program		server;
	public WebSocket	connection;
	public boolean		validated				= false;
	public boolean		guest					= false;
	public boolean		needsNewLevelStaticData	= true;
	public String		username				= null;
	boolean[]			isKeyDown				= new boolean[256];
	int					mx						= 0;
	int					my						= 0;
	boolean[]			mb						= { false, false, false };
	boolean[]			mbprev					= { false, false, false };
	Entity				e;
	public Client(Program s, WebSocket conn) {
		server = s;
		connection = conn;
	}
	@SuppressWarnings("unchecked")
	public void kick(String s) {
		JSONObject send = new JSONObject();
		send.put("kicked", s);
		connection.send(send.toJSONString());
		connection.close(0);
	}
	@SuppressWarnings("unchecked")
	public void errNoKick(int code) {
		JSONObject send = new JSONObject();
		send.put("err", code);
		connection.send(send.toJSONString());
	}
	@SuppressWarnings("unchecked")
	public void validate(boolean isGuest, String user) {
		username = user;
		guest = isGuest;
		validated = true;
		JSONObject send = new JSONObject();
		send.put("validated", new Boolean(true));
		connection.send(send.toJSONString());
		spawnIn();
	}
	public void spawnIn() {
		Random random = new Random();
		Color C = Color.getHSBColor(random.nextFloat(), 0.9f, 0.9f);
		String color = String.format("#%02X%02X%02X", C.getRed(), C.getGreen(), C.getBlue());
		e = new Entity(server.level, new Rect2D(33, 33, 28, 28), color, "player");
		e.name = username;
		e.brain = this;
	}
	public void handleInput(String msg) {
		JSONObject parsedMsg = (JSONObject) JSONValue.parse(msg);
		if (!parsedMsg.containsKey("keys") || ((JSONArray) parsedMsg.get("keys")).size() != 256) {
			Console.println("invalid input: \"" + msg + "\"");
			return;
		} else {
			for (int i = 0; i < 256; i++) {
				isKeyDown[i] = ((Boolean) ((JSONArray) parsedMsg.get("keys")).toArray()[i]).booleanValue();
			}
			if (parsedMsg.containsKey("mouse")) {
				mx = ((Long) ((JSONObject) parsedMsg.get("mouse")).get("x")).intValue();
				my = ((Long) ((JSONObject) parsedMsg.get("mouse")).get("y")).intValue();
				e.angle = Double.parseDouble(((JSONObject) parsedMsg.get("mouse")).get("angle").toString());
				mbprev[0] = mb[0];
				mbprev[1] = mb[1];
				mbprev[2] = mb[2];
				mb[0] = ((Boolean) ((JSONObject) parsedMsg.get("mouse")).get("button0")).booleanValue();
				mb[1] = ((Boolean) ((JSONObject) parsedMsg.get("mouse")).get("button1")).booleanValue();
				mb[2] = ((Boolean) ((JSONObject) parsedMsg.get("mouse")).get("button2")).booleanValue();
				if (mb[0] && !mbprev[0]) {
					e.state.attemptAttack();
				}
			}
		}
	}
	public void update(Entity e, double dt) {
		double speed = 300;
		double sqrt2 = Math.sqrt(2.0);
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
		if (xmove && ymove) {
			xmod /= sqrt2;
			ymod /= sqrt2;
		}
		e.move(xmod, ymod, dt);
	}
	@SuppressWarnings("unchecked")
	public void sendData() {
		if (!validated) {
			return;
		}
		JSONObject send = server.level.toJSONDynamic();
		send.put("player", e.toJSON());
		if (needsNewLevelStaticData) {
			send.put("level", server.level.toJSONStatic());
			needsNewLevelStaticData = false;
		}
		connection.send(send.toJSONString());
	}
}
