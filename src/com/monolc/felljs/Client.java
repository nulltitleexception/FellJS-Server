package com.monolc.felljs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import org.java_websocket.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.monolc.felljs.physics.Rect2D;
import com.monolc.felljs.world.Entity;

public class Client {
	public Program		server;
	public WebSocket	connection;
	public boolean		validated				= false;
	public boolean		guest					= false;
	public boolean		needsNewLevelStaticData	= true;
	public String		username				= null;
	boolean[]			isKeyDown				= new boolean[256];
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
		e = new Entity(server.level, new Rect2D(10, 10, 32, 32), color, username, 10);
		e.client = this;
	}
	public void handleInput(String msg) {
		JSONObject parsedMsg = (JSONObject) JSONValue.parse(msg);
		if (!parsedMsg.containsKey("keys") || ((JSONArray) parsedMsg.get("keys")).size() != 256) {
			System.out.println("invalid input: \"" + msg + "\"");
			return;
		} else {
			for (int i = 0; i < 256; i++) {
				isKeyDown[i] = ((Boolean) ((JSONArray) parsedMsg.get("keys")).toArray()[i]).booleanValue();
			}
		}
	}
	public void update(double dt, ArrayList<Client> clients) {
		double speed = 300 * dt;
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
		e.move(xmod, ymod);
	}
	public int getDataStride() {
		return 7;
	}
	@SuppressWarnings("unchecked")
	public void sendData(ArrayList<Client> clients) {
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
