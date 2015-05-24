package com.monolc.felljs.res;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Resources {
	public static boolean addUser(String name, String pass) {
		Path p = Paths.get(new File("").getAbsolutePath() + "/users/" + name
				+ ".pass");
		if (!Files.notExists(p)) {
			return false;
		}
		Charset charset = Charset.forName("US-ASCII");
		try {
			if (!Files.exists(Paths.get(new File("").getAbsolutePath()
					+ "/users"))) {
				Files.createDirectory(Paths.get(new File("").getAbsolutePath()
						+ "/users"));
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
		Path p = Paths.get(new File("").getAbsolutePath() + "/users/" + name
				+ ".pass");
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