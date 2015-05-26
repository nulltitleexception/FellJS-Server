package com.monolc.felljs.world;

import java.util.ArrayList;

import com.monolc.felljs.physics.Rect2D;

public class Entity {
	int id;
	public Rect2D box;
	public String color = null;
	public int health;
	public World world;
	public Entity(World w, Rect2D b, String c, int h) {
		world = w;
		box = b;
		color = c;
		health = h;
	}

	public void move(double dx, double dy) {
		if (checkCollisions(world.entities)) {
			System.out.println("LOGIC ERROR!");
		}
		box.x += dx;
		box.y += dy;
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
}
