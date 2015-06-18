package com.monolc.felljs.world;

import org.json.simple.JSONObject;

import com.monolc.felljs.res.EntitySchematic;

public class EntityData {
	public int				health;
	public EntitySchematic	schematic;
	public EntityData(EntitySchematic es) {
		schematic = es;
	}
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("health", new Integer(health));
		return ret;
	}
}
