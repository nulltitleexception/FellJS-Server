package com.monolc.felljs.physics;

public class Rect2D {
	public double	x, y, w, h;
	public Rect2D(double xPos, double yPos, double width, double height) {
		x = xPos;
		y = yPos;
		w = width;
		h = height;
	}
	public Vector2D getCenter() {
		return new Vector2D(x + (w / 2), y + (h / 2));
	}
	public boolean intersects(Rect2D r) {
		if (x + w > r.x && y + h > r.y && x < r.x + r.w && y < r.y + r.h) {
			return true;
		}
		return false;
	}
	public Rect2D getIntersect(Rect2D r) {
		if (x + w > r.x && y + h > r.y && x < r.x + r.w && y < r.y + r.h) {
			return new Rect2D(Math.max(x, r.x), Math.max(y, r.y), Math.abs(Math.min(x + w, r.x + r.w) - Math.max(x, r.x)), Math.abs(Math.min(y + h, r.y + r.h) - Math.max(y, r.y)));
		}
		return null;
	}
}
