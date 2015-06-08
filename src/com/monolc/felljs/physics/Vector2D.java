package com.monolc.felljs.physics;

public class Vector2D {
	private double x, y;

	public Vector2D() {
		x = 0;
		y = 0;
	}

	public Vector2D(double a) {
		x = a;
		y = a;
	}

	public Vector2D(double a, double b) {
		x = a;
		y = b;
	}

	public double X() {
		return x;
	}

	public double Y() {
		return y;
	}

	public Vector2D add(Vector2D v) {
		return new Vector2D(this.x + v.x, this.y + v.y);
	}

	public Vector2D mult(Vector2D v) {
		return new Vector2D(this.x * v.x, this.y * v.y);
	}

	public Vector2D mult(double d) {
		return new Vector2D(this.x * d, this.y * d);
	}

	public double dot(Vector2D v) {
		return (this.x * v.x) + (this.y * v.y);
	}
}
