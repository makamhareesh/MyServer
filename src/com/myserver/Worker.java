package com.myserver;

import java.net.Socket;

public class Worker implements Runnable {
private Socket clientSocket;
private String path;
	public Worker(Socket clientSocket,String path){
        this.clientSocket = clientSocket;
        this.path = path;
    }
	
	@Override
	public void run() {
		
		HttpHandler handler = new HttpHandler();
		handler.processRequest(clientSocket,path);
	
	}

}
