package com.monolc.felljs.world;

import java.util.ArrayList;

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

	public String toString() {
		return (int) box.x + "," + (int) box.y + "," + (int) box.w + ","
				+ (int) box.h + "," + color + ","
				+ (name != null ? name : "SERVER_ERROR") + "," + health;
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
