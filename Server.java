import java.net.*;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.io.*;

public class Server {
	
	private static final boolean DEBUG = false;
	private static int bookOwner[]; //clientID possessing corresponding book, -1 for server having book
	
	
	public static void main(String[] args) {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		
		try {
			s = in.readLine();
			
			try {
				
				in.close();
				String[] parts = s.split(" ");
				if(parts.length != 3) {
					throw new IllegalArgumentException("Must provide <#books UDPport TCPport>");
				}
				
				
				final int numBooks, portUDP, portTCP;
				
				try {
					numBooks = Integer.parseInt(parts[0]);
					portUDP = Integer.parseInt(parts[1]);
					portTCP = Integer.parseInt(parts[2]);
					
					if(numBooks <= 0 || portUDP <= 0 || portTCP <= 0) {
						throw new IllegalArgumentException("Must provide nonnegative, nonzero numbers");
					}
					
					//input is valid!!
					
					bookOwner = new int[numBooks];
					
					for(int i = 0; i < bookOwner.length; i++) {
						bookOwner[i] = -1;
					}
					
					if(DEBUG) {
						System.out.println("***************************");
						System.out.println("input was: " + s);
						System.out.println("number of books: " + numBooks);
						System.out.println("UDP port: " + portUDP);
						System.out.println("TCP port: " + portTCP);
						System.out.println("intitializations done");
						System.out.println("***************************");
					}
						
					//run both TCP and UDP at same time:
						
					Thread t0 = new Thread() {
						public void run() {
							TCPServer(portTCP);
						}
					};
					
					t0.start();		
					UDPServer(portUDP);
				}
				catch(NumberFormatException e) {
					throw new IllegalArgumentException("Must provide valid numbers");
				}
			} catch (IOException e) { }
			
		} catch (IOException e) {
			// TODO: how handle no input? Do nothing?
			e.printStackTrace();
			s = "";
		}	
	}
	
	private static void UDPServer(int portUDP) {
		
		while(true) {
			
			DatagramPacket datapacket, returnpacket;
			datapacket = null;
			byte[] returnMsg = null;
			int len = 1024; //TODO: need to change/set this ?????
			
			try {
				DatagramSocket datasocket = new DatagramSocket(portUDP);
				byte[] buf = new byte[len];
				while (true) {	
					datapacket = new DatagramPacket(buf, buf.length);
					datasocket.receive(datapacket);
					datasocket.close(); //forgot to close it
					
					String request = new String(datapacket.getData(), 0, datapacket.getLength());
					
					if(DEBUG) {
						System.out.println("a message has been received: " + request);
					}
					returnMsg = serveRequest(request);	
					
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
		}
	}
	
	private static void TCPServer(int port) {
		
        ServerSocket servSocket;
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
			        System.out.println("Received: " + clientRequest);
			        
			        String returnMsg = new String( serveRequest(clientRequest), "UTF-8"); //TODO this scares me
			        
			        outToClient.writeBytes(returnMsg); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*finally{
			if (servSocket != null) {
	            try {
	                servSocket.close();
	            } catch (IOException e) {
	                // log error just in case
	            }
	        }
		}*/	
	}
	
	private static byte[] serveRequest(String request){

		String[] msgParts = request.split(" ");
		int bookNum, clientNum;
		
		if(DEBUG) {
			System.out.println("msgParts[0]: " + msgParts[0]);
			System.out.println("msgParts[1]: " + msgParts[1]);
			System.out.println("msgParts[1].substring(1): " + msgParts[1].substring(1));
			System.out.println("msgParts[2]: " + msgParts[2]);
		}
		
		if(msgParts.length != 3) {
			return "error".getBytes();
			//continue;
		}
		if(msgParts[1].length() < 2 || !Pattern.matches( msgParts[1], "^([b][0-9]+)$")){//TODO check regex
			return "error".getBytes();
			//continue;
		}
			
		try {
			clientNum = Integer.parseInt(msgParts[0]);
			bookNum = Integer.parseInt(msgParts[1].substring(1));
			
			if(msgParts[2].equals("reserve")) {
				if(reserveBook(bookNum, clientNum)) {
					return ("c" + clientNum + " " + "b" + bookNum).getBytes();
					//continue;
				}
				else {
					return ("fail c" + clientNum + " " + "b" + bookNum).getBytes();
					//continue;
				}
			}
			else if(msgParts[2].equals("return")) {
				if(returnBook(bookNum, clientNum)) {
					return ("free c" + clientNum + " " + "b" + bookNum).getBytes();
					//continue;
				}
				else {
					return ("fail c" + clientNum + " " + "b" + bookNum).getBytes();
					//continue;
				}
			}
			else {
				return "error".getBytes();
				//continue;
			}
		}
		catch(NumberFormatException e) {
			return "error".getBytes();
			//continue;
		}
	}

	private static synchronized boolean reserveBook (int book, int clientNum) {
		
		if(book < 1 || book > bookOwner.length) {
			return false; //out of range
		}
		
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
		
		if(bookOwner[book - 1] == clientNum) {
			bookOwner[book - 1] = -1; //return book successfully
			return true;
		}
		
		//else, something was wrong
		return false;
	}


}
