package com.monolc.felljs.ai;

import com.monolc.felljs.world.Entity;

public class BasicAI implements EntityAI {
	double	anglegoal		= 0;
	double	timetonewangle	= 0;
	@Override
	public void update(Entity e, double dt) {
		double speed = 25;
		boolean foundenemy = false;
		int eid = -1;
		for (int i = 0; i < e.level.entities.size() && !foundenemy; i++) {
			Entity en = e.level.entities.get(i);
			foundenemy = (Math.abs(en.box.x - e.box.x) < 100 && Math.abs(en.box.y - e.box.y) < 100 && !en.state.schematic.faction.equals(e.state.schematic.faction));
			if (foundenemy) {
				eid = i;
			}
		}
		if (foundenemy) {
			Entity en = e.level.entities.get(eid);
			anglegoal = -(Math.atan((en.box.getCenter().Y() - e.box.getCenter().Y()) / (en.box.getCenter().X() - e.box.getCenter().X())));
			if (en.box.getCenter().X() < e.box.getCenter().X()) {
				anglegoal += Math.PI;
			}
			e.angle = anglegoal;
		} else {
			timetonewangle -= dt;
			if (timetonewangle <= 0) {
				anglegoal = Math.random() * 2 * Math.PI;
				timetonewangle = (Math.random() * 4) + 1.5;
			}
			if (Math.abs(e.angle - anglegoal) < Math.PI / 40) {
				e.angle = anglegoal;
			} else {
				e.angle = (e.angle * 0.95) + (anglegoal * 0.05);
			}
		}
		double xmod = Math.cos(e.angle) * speed;
		double ymod = Math.sin(e.angle) * -speed;
		e.move(xmod, ymod, dt);
	}
}
