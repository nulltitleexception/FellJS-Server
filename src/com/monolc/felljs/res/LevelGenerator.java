package com.monolc.felljs.res;

import java.util.Random;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.monolc.felljs.physics.Rect2D;
import com.monolc.felljs.world.Entity;
import com.monolc.felljs.world.Level;

public class LevelGenerator {
	LuaValue	terrain;
	public LevelGenerator(String name) {
		Globals globals = JsePlatform.standardGlobals();
		terrain = globals.loadfile("res/terrain/" + name + ".lua");
	}
	public Level createDungeonLevel(int width, int height) {
		Level ret = new Level(width, height, terrain.call(LuaValue.valueOf(width), LuaValue.valueOf(height)).tojstring());
		Random r = new Random();
		for (int a = 0; a < ret.tiles.length; a++) {
			for (int b = 0; b < ret.tiles[0].length; b++) {
				if (ret.tiles[a][b].passable && r.nextInt(100) == 1) {
					new Entity(ret, new Rect2D(a * Level.TILE_SIZE, b * Level.TILE_SIZE, 14, 14), "#FF0000", "slime");
				}
			}
		}
		return ret;
	}
}
