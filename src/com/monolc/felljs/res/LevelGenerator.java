package com.monolc.felljs.res;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.monolc.felljs.world.Level;

public class LevelGenerator {
	LuaValue	terrain;
	public LevelGenerator(String name) {
		Globals globals = JsePlatform.standardGlobals();
		terrain = globals.loadfile("res/terrain/" + name + ".lua");
	}
	public Level createDungeonLevel(int width, int height) {
		return new Level(width, height, terrain.call(LuaValue.valueOf(width), LuaValue.valueOf(height)).tojstring());
	}
}
