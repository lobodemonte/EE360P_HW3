import java.net.*;
import java.io.*;

public class Server {
	
	private static final boolean DEBUG = true;
	private static int bookOwner[]; //clientID possessing corresponding book, -1 for server having book
	
	
	public static void main(String[] args) {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		
		try {
			s = in.readLine();
		} catch (IOException e) {
			// TODO: how handle no input?
			e.printStackTrace();
			s = "";
		}
		
		try {
			in.close();
		} catch (IOException e) { }
		
		String[] parts = s.split(" ");
		
		if(parts.length != 3) {
			throw new IllegalArgumentException("Must provide <#books UDPport TCPport>");
		}
		
		
		int numBooks, portUDP, portTCP;
		
		try {
			numBooks = Integer.parseInt(parts[0]);
			portUDP = Integer.parseInt(parts[1]);
			portTCP = Integer.parseInt(parts[2]);
		}
		catch(NumberFormatException e) {
			throw new IllegalArgumentException("Must provide valid numbers");
		}
		
		if(numBooks <= 0 || portUDP <= 0 || portTCP <= 0) {
			throw new IllegalArgumentException("Must provide nonnegative, nonzero numbers");
		}
		
		//input is valid!!
		
		bookOwner = new int[numBooks];
		for(int i = 0; i < bookOwner.length; i++) {
			bookOwner[i] = 0;
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
				TCPServer();
			}
		};
		t0.start();		
		UDPServer(portUDP);
			
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
				
				//if statement basically allows me to skip over this on the first time
				if(returnMsg != null) {
					returnpacket = new DatagramPacket(
							returnMsg,
							returnMsg.length,
							datapacket.getAddress(),
							datapacket.getPort());
					datasocket.send(returnpacket);
				}
				
				datapacket = new DatagramPacket(buf, buf.length);
				datasocket.receive(datapacket);
			
				////////////////
				
					if(DEBUG) {
						System.out.println("a message has been received");
					}
				
				
				String msg = new String(datapacket.getData());
				String[] msgParts = msg.split(" ");
				
				if(msgParts.length != 3) {
					returnMsg = "error".getBytes();
					continue;
				}
				
				if(msgParts[1].length() < 2 || msgParts[0].charAt(0) != 'b') {
					returnMsg = "error".getBytes();
					continue;
				}
				
				int bookNum, clientNum;
				
				try {
					clientNum = Integer.parseInt(msgParts[0]);
					bookNum = Integer.parseInt(msgParts[1].substring(1));
				}
				catch(NumberFormatException e) {
					returnMsg = "error".getBytes();
					continue;
				}
				
				if(msgParts[1].equals("reserve")) {
					if(reserveBook(bookNum, clientNum)) {
						returnMsg = ("c" + clientNum + " " + "b" + bookNum).getBytes();
						continue;
					}
					else {
						returnMsg = ("fail c" + clientNum + " " + "b" + "bookNum").getBytes();
						continue;
					}
				}
				else if(msgParts[1].equals("return")) {
					if(returnBook(bookNum, clientNum)) {
						returnMsg = ("free c" + clientNum + " " + "b" + bookNum).getBytes();
						continue;
					}
					else {
						returnMsg = ("fail c" + clientNum + " " + "b" + "bookNum").getBytes();
						continue;
					}
				}
				else {
					returnMsg = "error".getBytes();
					continue;
				}				

			}
		} catch (SocketException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		
		}

	}
	
	private static void TCPServer() {
		
		while(true){
			
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
