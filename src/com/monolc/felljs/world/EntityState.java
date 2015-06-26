package com.monolc.felljs.world;

import org.json.simple.JSONObject;

import com.monolc.felljs.physics.Vector2D;
import com.monolc.felljs.res.EntitySchematic;

public class EntityState {
	private static final double	ATTACK_DIST	= 10;
	private static final double	ATTACK_DUR	= 0.2;
	public EntitySchematic		schematic;
	public int					health;
	public double				attackTime;
	public EntityState(EntitySchematic es) {
		schematic = es;
		health = schematic.maxHealth;
	}
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("type", schematic.name);
		ret.put("health", new Integer(health));
		if (attackTime > 0) {
			ret.put("weapon", getWeaponPosRelativ().toJSON());
		}
		return ret;
	}
	public void update(double dt) {
		attackTime -= dt;
	}
	public boolean attemptAttack() {
		if (attackTime > 0) {
			return false;
		}
		attackTime = ATTACK_DUR;
		return true;
	}
	public Vector2D getWeaponPosRelativ() {
		return new Vector2D(22, -2 - (ATTACK_DIST * (1.0 - Math.abs(((1.0 - (attackTime / ATTACK_DUR)) * 2) - 1))));
	}
}
