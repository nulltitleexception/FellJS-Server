package com.monolc.felljs.world;

import org.json.simple.JSONObject;

import com.monolc.felljs.Client;
import com.monolc.felljs.ai.EntityAI;
import com.monolc.felljs.console.Console;
import com.monolc.felljs.physics.Rect2D;
import com.monolc.felljs.physics.Vector2D;
import com.monolc.felljs.res.Resources;

public class Entity {
	int					id		= -1;
	public Rect2D		box;
	public Vector2D		vel;
	public double		angle;
	public String		color	= null;
	public String		name	= null;
	public Level		level;
	public EntityAI		brain;
	public EntityState	state;
	public Entity(Level w, Rect2D b, String c, String n) {
		state = new EntityState(Resources.getEntitySchematic(n));
		brain = Resources.getEntityAI(state.schematic.AI);
		vel = new Vector2D();
		angle = 0;
		level = w;
		box = b;
		color = c;
		name = Character.toUpperCase(n.charAt(0)) + n.substring(1);
		level.addEntity(this);
	}
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("x", new Integer((int) (box.x + 0.5)));
		ret.put("y", new Integer((int) (box.y + 0.5)));
		ret.put("angle", new Double((Math.PI / 2) - angle));
		ret.put("width", new Integer((int) (box.w + 0.5)));
		ret.put("height", new Integer((int) (box.h + 0.5)));
		ret.put("color", color);
		ret.put("name", (name != null ? name : "SERVER_ERROR"));
		ret.put("sprite", (brain instanceof Client) ? "player" : 2);
		ret.put("state", state.toJSON());
		return ret;
	}
	public void update(double dt) {
		if (state.health <= 0) {
			//TODO: Kill and add corpse?
			level.entities.remove(this);
		}
		brain.update(this, dt);
		state.update(dt);
	}
	public void move(double vx, double vy, double dt) {
		double muFactor = 0.9;
		vel = vel.mult(muFactor).add((new Vector2D(vx, vy)).mult(1 - muFactor));
		if (checkCollisions(level)) {
			Console.println("LOGIC ERROR!");
		}
		box.x += vel.X() * dt;
		box.y += vel.Y() * dt;
		fixCollisions(level);
	}
	public boolean checkCollisions(Level l) {
		if (box.x < 0 || box.y < 0 || box.x + box.w > l.tiles.length * Level.TILE_SIZE || box.y + box.h > l.tiles[0].length * Level.TILE_SIZE) {
			return true;
		}
		for (Entity e : l.entities) {
			if (id != e.id && box.intersects(e.box)) {
				return true;
			}
		}
		Rect2D tileBox = new Rect2D(0, 0, Level.TILE_SIZE, Level.TILE_SIZE);
		for (int a = (int) Math.max(box.x / Level.TILE_SIZE, 0.1); a < (int) Math.min((box.x + box.w + 1) / Level.TILE_SIZE, l.tiles.length + 0.1); a++) {
			for (int b = (int) Math.max((box.y - 1) / Level.TILE_SIZE, 0.1); b < (int) Math.min((box.y + box.h + 1) / Level.TILE_SIZE, l.tiles[0].length + 0.1); b++) {
				if (!l.tiles[a][b].passable) {
					tileBox.x = a * Level.TILE_SIZE;
					tileBox.y = b * Level.TILE_SIZE;
					if (box.intersects(tileBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public void fixCollisions(Level l) {
		if (box.x < 0) {
			box.x = 0;
			if (vel.X() < 0) {
				vel.setX(0);
			}
		}
		if (box.y < 0) {
			box.y = 0;
			if (vel.Y() < 0) {
				vel.setY(0);
			}
		}
		if (box.x + box.w > l.tiles.length * Level.TILE_SIZE) {
			box.x = (l.tiles.length * Level.TILE_SIZE) - box.w;
			if (vel.X() > 0) {
				vel.setX(0);
			}
		}
		if (box.y + box.h > l.tiles[0].length * Level.TILE_SIZE) {
			box.y = (l.tiles[0].length * Level.TILE_SIZE) - box.h;
			if (vel.Y() > 0) {
				vel.setY(0);
			}
		}
		for (Entity e : l.entities) {
			if (id != e.id && box.intersects(e.box)) {
				Rect2D intrsct = box.getIntersect(e.box);
				if (intrsct.w < intrsct.h) {
					if (box.x < e.box.x) {
						box.x -= intrsct.w;
						if (vel.X() > 0) {
							vel.setX(0);
						}
					} else {
						box.x += intrsct.w;
						if (vel.X() < 0) {
							vel.setX(0);
						}
					}
				} else {
					if (box.y < e.box.y) {
						box.y -= intrsct.h;
						if (vel.Y() > 0) {
							vel.setY(0);
						}
					} else {
						box.y += intrsct.h;
						if (vel.Y() < 0) {
							vel.setY(0);
						}
					}
				}
			}
		}
		Rect2D tileBox = new Rect2D(0, 0, Level.TILE_SIZE, Level.TILE_SIZE);
		for (int a = 0; a < l.tiles.length; a++) {
			for (int b = 0; b < l.tiles[0].length; b++) {
				if (!l.tiles[a][b].passable) {
					tileBox.x = a * Level.TILE_SIZE;
					tileBox.y = b * Level.TILE_SIZE;
					if (box.intersects(tileBox)) {
						Rect2D intrsct = box.getIntersect(tileBox);
						if (intrsct.w < intrsct.h) {
							if (box.x < tileBox.x) {
								box.x -= intrsct.w;
								if (vel.X() > 0) {
									vel.setX(0);
								}
							} else {
								box.x += intrsct.w;
								if (vel.X() < 0) {
									vel.setX(0);
								}
							}
						} else {
							if (box.y < tileBox.y) {
								box.y -= intrsct.h;
								if (vel.Y() > 0) {
									vel.setY(0);
								}
							} else {
								box.y += intrsct.h;
								if (vel.Y() < 0) {
									vel.setY(0);
								}
							}
						}
					}
				}
			}
		}
	}
	public void remove() {
		level.removeEntity(this);
	}
}
