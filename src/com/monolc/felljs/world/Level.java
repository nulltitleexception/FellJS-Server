package com.monolc.felljs.world;

import org.json.simple.*;

import java.util.ArrayList;
import java.util.Random;

public class Level {
	public static final int		TILE_SIZE	= 32;
	public ArrayList<Entity>	entities;
	public Tile[][]				tiles;
	public Level(int w, int h) {
		Random r = new Random();
		tiles = new Tile[w][h];
		for (int a = 0; a < tiles.length; a++) {
			for (int b = 0; b < tiles[0].length; b++) {
				int id = r.nextInt(4);
				if (a < 5 && b < 5) {
					id = r.nextInt(3) + 1;
				}
				tiles[a][b] = new Tile(id, id != 0);
			}
		}
		entities = new ArrayList<Entity>();
	}
	public Level(int w, int h, String data) {
		JSONArray tilesData = (JSONArray) JSONValue.parse(data);
		tiles = new Tile[w][h];
		for (int a = 0; a < tiles.length; a++) {
			for (int b = 0; b < tiles[0].length; b++) {
				int id = ((Long) ((JSONArray) tilesData.get(a)).get(b)).intValue();
				tiles[a][b] = new Tile(id, id != 0);
			}
		}
		entities = new ArrayList<Entity>();
	}
	@SuppressWarnings("unchecked")
	public synchronized JSONObject toJSONDynamic() {
		JSONObject ret = new JSONObject();
		ret.put("enum", new Integer(entities.size()));
		JSONArray arr = new JSONArray();
		for (int i = 0; i < entities.size(); i++) {
			arr.add(entities.get(i).toJSON());
		}
		ret.put("entities", arr);
		return ret;
	}
	@SuppressWarnings("unchecked")
	public synchronized JSONObject toJSONStatic() {
		JSONObject ret = new JSONObject();
		ret.put("width", new Integer(tiles.length));
		ret.put("height", new Integer(tiles[0].length));
		JSONArray arr = new JSONArray();
		for (int a = 0; a < tiles.length; a++) {
			JSONArray col = new JSONArray();
			for (int b = 0; b < tiles[a].length; b++) {
				col.add(tiles[a][b].toJSON());
			}
			arr.add(col);
		}
		ret.put("tiles", arr);
		return ret;
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
	public synchronized void reassignEntityIDs() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).id = i;
		}
	}
}
