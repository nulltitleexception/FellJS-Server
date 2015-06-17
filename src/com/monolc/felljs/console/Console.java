package com.monolc.felljs.console;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.monolc.felljs.Client;

public class Console {
	private static String			pass	= "asdf";
	public static ArrayList<Client>	clients	= new ArrayList<Client>();
	public static void init() {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			System.out.println("Working Directory = " + System.getProperty("user.dir"));
			fr = new FileReader("consoleadmin.pass");
			br = new BufferedReader(fr);
			pass = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
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
			clients.get(i).connection.send(msg);
		}
		if (error) {
			Console.println("Console Error");
		}
	}
	public static boolean validatePass(String p) {
		return pass.equals(p);
	}
}
