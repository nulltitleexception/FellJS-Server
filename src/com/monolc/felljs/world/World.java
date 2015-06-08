package com.monolc.felljs.world;

import org.json.simple.*;

import java.util.ArrayList;

public class World {
	public ArrayList<Entity> entities;
	public Tile[][] tiles;
	public World(){
		tiles = new Tile[100][100];
		for (int a = 0; a > tiles.length; a++){
			for (int b = 0; b < tiles[0].length; b++){
				tiles[a][b] = new Tile(0, true);
			}
		}
		entities = new ArrayList<Entity>();
	}
	@SuppressWarnings("unchecked")
	public String toString(){
		JSONObject ret = new JSONObject();
		ret.put("enum", "" + entities.size());
		JSONObject arr = new JSONObject();
		for (int i = 0; i < entities.size(); i++){
			arr.put(i, entities.get(i).toString());
		}
		ret.put("entities", arr.toString());
		return ret.toString();
	}
	public synchronized void addEntity(Entity e) {
		entities.add(e);
		e.id = entities.size() - 1;
	}
	public synchronized Entity removeEntity(Entity e) {
		Entity ret = entities.remove(e.id);
		ret.id = -1;
		reassignEntityIDs();
		return ret;
	}
	public synchronized Entity removeEntity(int e) {
		Entity ret = entities.remove(e);
		ret.id = -1;
		reassignEntityIDs();
		return ret;
	}
	public synchronized void reassignEntityIDs(){
		for (int i = 0; i < entities.size(); i++){
			entities.get(i).id = i;
		}
	}
}
