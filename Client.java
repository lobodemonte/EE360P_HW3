import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
	
	private static final boolean DEBUG = false;
	
	private static String clientID;
	private static String[] servers;
	private static int nServers; 
	private static final int timeout_ = 100;
	
	public static void main(String[] args) {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		
		try {
			s = in.readLine();
			String[] parts = s.split(" ");
			
			if(parts.length != 2) {
				in.close();	
				throw new IllegalArgumentException("Must provide <clientID ip_server>");
			}
			
			clientID = parts[0];
			
		
				nServers = Integer.parseInt(parts[1]);
			
			
			if(clientID.charAt(0) != 'c') { //TODO how to check that the characters after c are proper numbers 
				//close b/c leaving early
				in.close();
				throw new IllegalArgumentException("Must provide nonnegative, nonzero client ID");
			}
			
			for (int i = 0; i < nServers; i++){
				servers[i] = in.readLine();
			}
			
			
			if(DEBUG) {
				System.out.println("***************************");
				System.out.println("input was: " + s);
				System.out.println("clientID: " + clientID);
				//System.out.println("IP address: " + addressIP);
				System.out.println("intitializations done");
				System.out.println("***************************");
			}
			
			
			while(true) {
				
				s = in.readLine();
				
				if(s == null) {
					break;
				}
				
				if(DEBUG) {
					System.out.println("read input line: " + s);
				}
				
				parts = s.split(" ");
				
				if(parts[0].equals("sleep")) {
					long sleeptime = 0;
					
					try {
						sleeptime = Long.parseLong(parts[1]);
					}
					catch(NumberFormatException e) {	
						continue;
					}
					
					if(DEBUG) {
						System.out.println("sleeping for " + sleeptime + " ms ...");
						System.out.println("***************************");
					}
					
					try {
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {
						continue;
					}
				}
				else if (parts[1].equals("reserve") || parts[1].equals("return")){
					sendTCPRequest(s);
				}
				else {
					//TODO: anything???
					if(DEBUG) {
						System.out.println("input deemed incorrect, skipping");
						System.out.println("***************************");
					}
				}			
			}
		} catch (IOException e) {
			// TODO: how handle no input?
			e.printStackTrace();
			s = "";
		}	
		catch(NumberFormatException e) {
			//close b/c leaving early
			try {
				in.close();
			} catch (IOException e1) { }
			
			throw new IllegalArgumentException("Must provide valid numbers");
		}
		
	}
	
	private static void sendTCPRequest(String message) {
		Socket clientSocket;
		int i = 0;
		try {
			while(true){
				try{
					clientSocket = new Socket(getHostname(i), getPort(i));
					i = ((i + 1) % nServers);
					
					clientSocket.setSoTimeout(timeout_);
					DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
					BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					
					if(DEBUG) {
						System.out.println("message plus newline is: " + message + '\n');
					}
					
					outToServer.writeBytes(message + "\n");
					outToServer.flush();
					if(DEBUG) {
						System.out.println("finished outToServer.writeBytes");
					}
	
					System.out.println(inFromServer.readLine());
					
					clientSocket.close();
					break;
				} catch(SocketTimeoutException e){
					if (DEBUG){
						System.out.println("Server assummed crashed");
					}
				}	
			}
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		  
	}
	
	private static InetAddress getHostname(int i) throws UnknownHostException{	
		String[] components = servers[i].split(":");
		if (components[i].equals("localhost"))
			return InetAddress.getLocalHost();
		else
			return InetAddress.getByName(components[i]);
	}
	private static int getPort(int i){
		String[] components = servers[i].split(":");
		int port = Integer.parseInt(components[1]);
		return port;
	}

}
