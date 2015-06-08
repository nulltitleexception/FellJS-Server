package com.monolc.felljs.world;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.monolc.felljs.physics.Rect2D;
import com.monolc.felljs.physics.Vector2D;

public class Entity {
	int id = -1;
	public Rect2D box;
	public Vector2D vel;
	public String color = null;
	public String name = null;
	public int health;
	public World world;

	public Entity(World w, Rect2D b, String c, String n, int h) {
		vel = new Vector2D();
		world = w;
		box = b;
		color = c;
		health = h;
		name = n;
		world.addEntity(this);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("x", new Integer((int) box.x));
		ret.put("y", new Integer((int) box.y));
		ret.put("width", new Integer((int) box.w));
		ret.put("height", new Integer((int) box.h));
		ret.put("color", color);
		ret.put("name", (name != null ? name : "SERVER_ERROR"));
		ret.put("health", new Integer(health));
		return ret;
	}

	public void move(double vx, double vy) {
		double muFactor = 0.9;
		vel = vel.mult(muFactor).add((new Vector2D(vx, vy)).mult(1 - muFactor));
		if (checkCollisions(world.entities)) {
			System.out.println("LOGIC ERROR!");
		}
		box.x += vel.X();
		box.y += vel.Y();
		fixCollisions(world.entities);
	}

	public boolean checkCollisions(ArrayList<Entity> entities) {
		for (Entity e : entities) {
			if (id != e.id && box.intersects(e.box)) {
				return true;
			}
		}
		return false;
	}

	public boolean fixCollisions(ArrayList<Entity> entities) {
		for (Entity e : entities) {
			if (id != e.id && box.intersects(e.box)) {
				Rect2D intrsct = box.getIntersect(e.box);
				if (intrsct.w < intrsct.h) {
					if (box.x < e.box.x) {
						box.x -= intrsct.w;
					} else {
						box.x += intrsct.w;
					}
				} else {
					if (box.y < e.box.y) {
						box.y -= intrsct.h;
					} else {
						box.y += intrsct.h;
					}
				}
			}
		}
		return false;
	}

	public void remove() {
		world.removeEntity(this);
	}
}
