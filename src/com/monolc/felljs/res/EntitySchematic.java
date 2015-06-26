package com.monolc.felljs.res;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.monolc.felljs.console.Console;

public class EntitySchematic {
	JSONObject		json;
	public String	name;
	public String	AI;
	public String	faction;
	public int		maxHealth;
	public int		damage;
	public int		reach;
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
		AI = getAttribute("AI");
		faction = getAttribute("faction");
		maxHealth = getAttributeAsInt("maxHealth");
		damage = getAttributeAsInt("damage");
		reach = getAttributeAsInt("reach");
		System.out.println("EntitySchematic \"" + name + "\" added: " + maxHealth + ", " + damage);
	}
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("maxHealth", new Integer(maxHealth));
		//We're omitting anything unimportant to the client, such as AI, faction, and (at least for now) damage and reach
		//(not sure if damage and reach will be useful in the future, but they certainly aren't just yet)
		return ret;
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
