package com.monolc.felljs.res;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.monolc.felljs.world.Level;

public class LevelGenerator {
	LuaValue	terrain;
	public LevelGenerator(String name) {
		Globals globals = JsePlatform.standardGlobals();
		terrain = globals.load(globals.loadfile("res/terrain/" + name + ".lua").tojstring().replace("!=", "~="));
	}
	public Level createDungeonLevel(int width, int height) {
		return new Level(width, height, terrain.call(LuaValue.valueOf(width), LuaValue.valueOf(height)).tojstring());
	}
}
