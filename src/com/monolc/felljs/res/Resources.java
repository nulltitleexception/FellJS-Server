package com.monolc.felljs.res;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.monolc.felljs.ai.BasicAI;
import com.monolc.felljs.ai.EntityAI;

public class Resources {
	private static HashMap<String, EntitySchematic>	entitySchematics	= new HashMap<String, EntitySchematic>();
	private static HashMap<String, LevelGenerator>	levelGenerators	= new HashMap<String, LevelGenerator>();
	public static EntitySchematic getEntitySchematic(String name) {
		if (!entitySchematics.containsKey(name)) {
			entitySchematics.put(name, new EntitySchematic(name));
		}
		return entitySchematics.get(name);
	}
	public static EntityAI getEntityAI(String AI) {
		if (AI.equals("BasicAI")){
			return new BasicAI();
		}
		return null;
	}
	public static LevelGenerator getLevelGenerator(String name) {
		if (!levelGenerators.containsKey(name)){
			levelGenerators.put(name, new LevelGenerator(name));
		}
		return levelGenerators.get(name);
	}
	public static boolean addUser(String name, String pass) {
		Path p = Paths.get(new File("").getAbsolutePath() + "/users/" + name + ".pass");
		if (!Files.notExists(p)) {
			return false;
		}
		Charset charset = Charset.forName("US-ASCII");
		try {
			if (!Files.exists(Paths.get(new File("").getAbsolutePath() + "/users"))) {
				Files.createDirectory(Paths.get(new File("").getAbsolutePath() + "/users"));
			}
			Files.createFile(p);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try (BufferedWriter writer = Files.newBufferedWriter(p, charset)) {
			writer.write(pass, 0, pass.length());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static boolean isValidUser(String name, String pass) {
		Path p = Paths.get(new File("").getAbsolutePath() + "/users/" + name + ".pass");
		if (!Files.exists(p)) {
			return false;
		}
		Charset charset = Charset.forName("US-ASCII");
		String filePass = null;
		try (BufferedReader reader = Files.newBufferedReader(p, charset)) {
			filePass = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return pass.equals(filePass);
	}
}
