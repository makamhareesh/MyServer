package com.myserver;

import java.io.IOException;

public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Server server = null;
		String path = null;
		
		if(args.length == 1){
			String[] parts = args[0].split(":");
			path = parts[0];
			}
		try {
			server = new Server(Props.START_PORT,Props.MAX_CLIENTS,path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		new Thread(server).start();
	
	}

}
