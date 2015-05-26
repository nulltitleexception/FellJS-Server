package com.monolc.felljs.world;

import java.util.ArrayList;

public class World {
	ArrayList<Entity> entities;
	public World(){
		entities = new ArrayList<Entity>();
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