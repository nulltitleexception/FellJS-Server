package com.monolc.felljs.console;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.monolc.felljs.Client;

public class Console {
	private static String			pass	= "invalid";
	public static ArrayList<Client>	clients	= new ArrayList<Client>();
	public static void init() {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("/var/java/consoleadmin.pass");
			br = new BufferedReader(fr);
			pass = br.readLine();
		} catch (Exception e) {
			println("Error loading remote console password.");
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				println("Error closing file streams after loading remote console password.");
			}
		}
	}
	public static void println(String msg) {
		boolean error = false;
		System.out.println(msg);
		for (int i = 0; i < clients.size(); i++) {
			while (clients.get(i) == null || clients.get(i).connection == null || !clients.get(i).connection.isOpen()) {
				clients.remove(i);
				error = true;
			}
			if (clients.size() > i) {
				clients.get(i).connection.send(msg);
				clients.get(i).connection.send("ignoreme");
			}
		}
		if (error) {
			Console.println("Console Error");
		}
	}
	public static boolean validatePass(String p) {
		return !pass.equals("invalid") && pass.equals(p);
	}
}
