package com.myserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
	   private final ServerSocket serverSocket;
	   private final ExecutorService threadPool;
	   private final String path;
	
	   public Server(int port, int poolSize, String path)
		       throws IOException {
		     serverSocket = new ServerSocket(port);
		     threadPool = Executors.newFixedThreadPool(poolSize);
		     this.path = path;
		   }
	   
	@Override
	public void run() {
		
		try{
			Socket clientSocket = null;
			while(true){
				clientSocket = serverSocket.accept();
	            this.threadPool.execute(new Worker(clientSocket,path));
			}
			
		}catch(IOException e){
			e.printStackTrace();
			
		}
		
		
	}

}
