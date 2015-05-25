package com.monolc.felljs.world;

import com.monolc.felljs.physics.Rect2D;

public class Entity {
	public Rect2D box;
	public String color = null;
	public int health;
	public Entity (Rect2D b, String c, int h){
		box = b;
		color = c;
		health = h;
	}
}
