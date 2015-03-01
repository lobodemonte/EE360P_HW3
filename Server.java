import java.net.*;
import java.io.*;

public class Server {
	
	private static int bookAvail[];
	
	public static void main(String[] args) {
		
		if(args.length == 0) {
			throw new IllegalArgumentException("Must provide <#books UDPport TCPport>");
		}
		
		String parts[] = args[0].split(" ");
		
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
		
		bookAvail = new int[numBooks];
		for(int i = 0; i < bookAvail.length; i++) {
			bookAvail[i] = 0;
		}
		
		//SERVER STUFF:
		//TODO: separate thread for TCP
		
		//UDP:
		
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

	private static synchronized boolean reserveBook (int book, int clientNum) {
		
		if(book < 1 || book > bookAvail.length) {
			return false; //out of range
		}
		
		//-1 because indices 0-9 represent books 1-10, etc.
		if(bookAvail[book - 1] == 0) {
			bookAvail[book - 1] = clientNum; //check out book to client
			return true;
		}
		
		//else book is checked out
		return false;
	}
	
	private static synchronized boolean returnBook (int book, int clientNum) {
		
		if(book < 1 || book > bookAvail.length) {
			return false; //out of range
		}
		
		if(bookAvail[book - 1] == clientNum) {
			bookAvail[book - 1] = 0; //return book successfully
			return true;
		}
		
		//else, something was wrong
		return false;
	}


}
