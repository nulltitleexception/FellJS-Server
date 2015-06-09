package com.monolc.felljs.world;

import org.json.simple.JSONObject;

public class Tile {
	int id;
	boolean passable;
	public Tile(int ID, boolean p){
		id = ID;
		passable = p;
	}
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(){
		JSONObject ret = new JSONObject();
		ret.put("id", new Integer(id));
		ret.put("p", new Boolean(passable));
		return ret;
	}
}
