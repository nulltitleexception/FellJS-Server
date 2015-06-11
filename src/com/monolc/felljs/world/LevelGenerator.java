package com.monolc.felljs.world;

import java.util.Random;

public class LevelGenerator {
	public static Level createDungeonLevel(Random rand, int width, int height){
		Level ret = new Level(width, height);
		return ret;
	}
}
