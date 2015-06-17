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
		System.out.println(msg);
		for (Client c : clients) {
			c.connection.send(msg);
		}
	}
	public static boolean validatePass(String p) {
		return pass.equals(p);
	}
}
