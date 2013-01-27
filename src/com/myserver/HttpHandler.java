package com.myserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class HttpHandler {
	static final int BUFFER_SIZE = 1024;
	private String base = Props.DOC_ROOT;
	
	  public static final int HTTP_OK = 200;
	  public static final int HTTP_BAD_REQUEST = 400;
	  public static final int HTTP_NOT_FOUND = 404;
	  public static final int HTTP_BAD_METHOD = 405;
	  public static final int HTTP_SERVER_ERROR = 500;
	  
	  
	public void processRequest(Socket socket,String path){
		

	    InputStream is = null;
	    PrintStream ps = null;
		try {
			is = new BufferedInputStream(socket.getInputStream());
			ps = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

	    byte[] buffer = new byte[BUFFER_SIZE];
	    
	    int offset = 0;
	    int end = -1;
	    while(true) {
	    	
	    	try{
	    	
	      int read = is.read(buffer, offset, BUFFER_SIZE - offset);
	      if (read == -1) break;
	      for (int i = offset; i < offset + read; i++) {
	        if (buffer[i] == (byte) '\n' || buffer[i] == (byte) ('\r')) {
	          end = i;
	          break;
	        }
	      }
	      if (end != -1) break;
	    	}catch(IOException e){
	    		e.printStackTrace();
	    		
	    	}
	    }
	    
	    if (end == -1) {
	      try {
	    	  printError(ps, HTTP_BAD_REQUEST, "Method not supported.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return;
	    }
	    
	    if (buffer[0] == (byte)'G' &&
	        buffer[1] == (byte)'E' &&
	        buffer[2] == (byte)'T' &&
	        buffer[3] == (byte)' ') {
	      
	      int uri_end = 4;
	      while(uri_end < end && buffer[uri_end] != (byte)' ') {
	        uri_end++;
	      }
	      String filename = null;
		try {
			filename = (new String(buffer, 4, uri_end - 4, "UTF-8")).replace("/", File.separator);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      if(!(path == null || path.equals("")))
	    	  base=path;
	      File file = new File(base, filename);
	      if (file.exists())
	        if (file.isDirectory()) {
	          getFolder(ps, file);} else {
	          try {
				getFile(ps, file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
	      else {
	        try {
	        	printError(ps, HTTP_NOT_FOUND, "File not found.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return;
	      }
	      
	    } else {
	      try {
	    	  printError(ps, HTTP_BAD_REQUEST, "Method not supported.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return;
	    }
	  
	}
	  private void printError(PrintStream ps, int code, String message) throws IOException{
	    message = "Error\r\n " + code + ": " + message;
	    byte[] data = message.getBytes("UTF-8");
	    printHeaders(ps, code, "text/plain; charset=utf-8", data.length);
	    ps.println(message);
	  }
	  
	  void getFile(PrintStream ps, File file) throws IOException {
	    String contentType = "text/html";
	    InputStream is = null;
	    try {
	      printHeaders(ps, HTTP_OK, contentType, file.length());
	      is = new FileInputStream(file.getAbsolutePath());
	      int n;
	      byte[] buffer = new byte[BUFFER_SIZE];
	      while ((n = is.read(buffer)) > 0) {
	          ps.write(buffer, 0, n);
	      }
	    }catch(IOException e){
	    	e.printStackTrace();
	    } 
	    finally {
	      if (is != null) is.close();
	    }
	  }
	  
	  private void getFolder(PrintStream ps, File file) {
		try{
		String html = listFolder(file);
	    byte[] data = html.getBytes("UTF-8");
	    printHeaders(ps, HTTP_OK, "text/html", data.length);
	    ps.write(data);
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
	  }
	  
	  private String listFolder(File folder) throws IOException {
	    String html = "<html><head><title>" + folder.getName() + "</title></head><body>";
	    html += "<a href=\"..\">Parent Folder</a><BR>";
	    String[] list = folder.list();
	    if (list != null) {
	      for (int i = 0; i < list.length; i++) {
	        File file = new File(folder, list[i]);
	        if (file.isDirectory()) {
	          html += "<a href=\"" + list[i] + "/\">" + list[i] + "/</a><BR>";
	        } else {
	          html += "<a href=\"" + list[i] + "\">" + list[i] + "</a><BR>";
	        }
	      }
	    }
	    html += "</body></html>";
	    return html;
	  }
	  
	  private void printHeaders(PrintStream ps, 
		      int code, 
		      String contentType, 
		      long contentLength) throws IOException {
		    ps.print("HTTP/1.1 " + code + "\r\n");
		    ps.print("Content-Type: " + contentType +"\r\n");
		    ps.print("Content-Length: " + contentLength +"\r\n");
		    ps.print("\r\n");
		  }

}
