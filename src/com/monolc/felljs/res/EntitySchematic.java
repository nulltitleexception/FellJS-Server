package com.monolc.felljs.res;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.monolc.felljs.console.Console;

public class EntitySchematic {
	JSONObject	json;
	String		name;
	int			maxHealth;
	int			damage;
	public EntitySchematic(String n) {
		FileReader fr = null;
		BufferedReader br = null;
		String data = "";
		try {
			fr = new FileReader("res/entity/" + n + ".fsch");
			br = new BufferedReader(fr);
			String line = "";
			while (line != null) {
				data += line;
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		name = n;
		json = null;
		try {
			json = (JSONObject) new JSONParser().parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		maxHealth = getAttributeAsInt("maxHealth");
		damage = getAttributeAsInt("damage");
		System.out.println("EntitySchematic \"" + name + "\" added: " + maxHealth + ", " + damage);
	}
	private String getAttribute(String n) {
		if (json.containsKey(n)) {
			return (String) json.get(n);
		}
		if (json.containsKey("parent")) {
			return Resources.getEntitySchematic((String) json.get("parent")).getAttribute(n);
		}
		if (name.equals("default")) {
			Console.println("SCHEMA ERROR: ENTITIES: " + n);
			return null;
		}
		return Resources.getEntitySchematic("default").getAttribute(n);
	}
	private int getAttributeAsInt(String n) {
		return Integer.parseInt(getAttribute(n));
	}
}