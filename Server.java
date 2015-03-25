import java.net.*;
import java.io.*;

public class Server {
	
	private static final boolean DEBUG = true;
	static int servedCount = 0; 	//If k is greater than or equal to this private counter, the server
	//immediately 'crashes'; otherwise it will crash as soon as the private counter reaches k. 
	//resets to zero after it comes back up after the crash.
	static int bookOwner[];			//clientID possessing corresponding book, -1 for server having book
	static String servers[];		//Contains the address of own and other servers
	
	public static void main(String[] args) {
		
		final int serverid, nInstances, numBooks; 
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		
		try {
			s = in.readLine(); // TODO: how to handle no input? Do nothing?
			
			try {
				
				in.close();
				String[] parts = s.split(" ");
				
				if(parts.length != 3) {
					throw new IllegalArgumentException("Input must be of form <serverid n z> "); 
				}
				
				
				
				try {
					serverid   = Integer.parseInt(parts[0]);
					nInstances = Integer.parseInt(parts[1]);	//n
					numBooks   = Integer.parseInt(parts[2]);	//z
					
					bookOwner = new int[numBooks]; 
					servers = new String[nInstances];
					
					if (serverid <= 0 || nInstances <= 0 || numBooks <= 0) { 
						throw new IllegalArgumentException("Must provide nonnegative, nonzero numbers");
					}
					
					for(int i = 0; i < bookOwner.length; i++) {
						bookOwner[i] = -1;
					}
					
					for (int i =0; i < serverid; ++i){ 		//<ipaddress:port>
						servers[i] = in.readLine();
						if (servers[i] == null) { 
							throw new IllegalArgumentException("Must provide valid number of server address lines");
						}
					}
					
					//TODO more detailed validation of input
					//input is valid!!
					
					String address[] = servers[serverid-1].split(":");
					int port = Integer.parseInt(address[1]);
					TCPServer(port);
					
					/*
					if(DEBUG) {	
						System.out.println("***************************");
						System.out.println("input was: " + s);
						System.out.println("number of books: " + numBooks);
						System.out.println("UDP port: " + portUDP);
						System.out.println("TCP port: " + portTCP);
						System.out.println("intitializations done");
						System.out.println("***************************");			
					}
					*/	
				}
				catch(NumberFormatException e) {
					throw new IllegalArgumentException("Must provide valid numbers");
				}
			} catch (IOException e) { }		
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private static void TCPServer(int port) {
		
        ServerSocket servSocket = null;
        Socket connectionSocket;
        BufferedReader inFromClient;
        DataOutputStream outToClient;
        String clientRequest;
        
		try {
			servSocket = new ServerSocket(port);
			
			while(true){
				try {
					connectionSocket = servSocket.accept();
					
					inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			        
			        clientRequest = inFromClient.readLine();
			        //clientRequest = "" + inFromClient.read();
			        
			        if(DEBUG){
			        System.out.println("TCP received: " + clientRequest);
			        }
			         
			        String returnMsg = serveRequest(clientRequest);
			        outToClient.writeBytes(returnMsg + "\n"); 
			        
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(servSocket != null) {
			try {
				servSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	private static void UDPServer(int portUDP) {
		
		while(true) {
			
			DatagramPacket datapacket, returnpacket;
			datapacket = null;
			byte[] returnMsg = null;
			int len = 1024; //TODO: need to change/set this ?????
			
			DatagramSocket datasocket = null;
			
			try {
				datasocket = new DatagramSocket(portUDP);
				byte[] buf = new byte[len];
				while (true) {	
					datapacket = new DatagramPacket(buf, buf.length);
					datasocket.receive(datapacket);
					
					
					String request = new String(datapacket.getData(), 0, datapacket.getLength());
					
					if(DEBUG) {
						System.out.println("UDP received: " + request);
					}
					returnMsg = serveRequest(request).getBytes();	
					
					returnpacket = new DatagramPacket(
							returnMsg,
							returnMsg.length,
							datapacket.getAddress(),
							datapacket.getPort());
					datasocket.send(returnpacket);
				}
			} catch (SocketException e) {
				System.err.println(e);
			} catch (IOException e) {
				System.err.println(e);
			}
			
			if(datasocket != null) {
				datasocket.close(); //forgot to close it
			}
			
		}
	}
	*/
	
	private static String serveRequest(String request){

		//TODO handle the concurrent side of this hw >:?
		
		String[] msgParts = request.split(" ");
		int bookNum, clientNum;
		
		if(DEBUG) {
			/*
			System.out.println("msgParts[0]: " + msgParts[0]);
			System.out.println("msgParts[1]: " + msgParts[1]);
			System.out.println("msgParts[1].substring(1): " + msgParts[1].substring(1));
			System.out.println("msgParts[2]: " + msgParts[2]);
			*/
		}
		
		if(msgParts.length != 3) {
			return "error";
		}
		if(msgParts[1].length() < 2 || msgParts[1].charAt(0) != 'b'){//TODO check regex
			return "error";
		}
			
		try {
			clientNum = Integer.parseInt(msgParts[0]);
			bookNum = Integer.parseInt(msgParts[1].substring(1));
			
			if(msgParts[2].equals("reserve")) {
				if(reserveBook(bookNum, clientNum)) {
					return ("c" + clientNum + " " + "b" + bookNum);
				}
				else {
					return ("fail c" + clientNum + " " + "b" + bookNum);
				}
			}
			else if(msgParts[2].equals("return")) {
				if(returnBook(bookNum, clientNum)) {
					return ("free c" + clientNum + " " + "b" + bookNum);
				}
				else {
					return ("fail c" + clientNum + " " + "b" + bookNum);
				}
			}
			else {
				return "error";
			}
		}
		catch(NumberFormatException e) {
			return "error";
		}
	}

	private static synchronized boolean reserveBook (int book, int clientNum) {
		
		if(book < 1 || book > bookOwner.length) {
			return false; //out of range
		}
		//TODO concurrent feature goes here
		//-1 because indices 0-9 represent books 1-10, etc.
		if(bookOwner[book - 1] == -1) {
			bookOwner[book - 1] = clientNum; //check out book to client
			return true;
		}
		
		//else book is checked out
		return false;
	}
	
	private static synchronized boolean returnBook (int book, int clientNum) {
		
		if(book < 1 || book > bookOwner.length) {
			return false; //out of range
		}
		//TODO concurrent feature goes here
		if(bookOwner[book - 1] == clientNum) {
			bookOwner[book - 1] = -1; //return book successfully
			return true;
		}
		
		//else, something was wrong
		return false;
	}


}
