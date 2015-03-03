import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
	
	private static final boolean DEBUG = false;
	
	private static int clientID;
	private static String addressIP;
	
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
		
		String[] parts = s.split(" ");
		
		if(parts.length != 2) {
			//close b/c leaving early
			try {
				in.close();
			} catch (IOException e) { }
			
			throw new IllegalArgumentException("Must provide <clientID ip_server>");
		}
		
		addressIP = parts[1];
		
		try {
			clientID = Integer.parseInt(parts[0]);
		}
		catch(NumberFormatException e) {
			//close b/c leaving early
			try {
				in.close();
			} catch (IOException e1) { }
			
			throw new IllegalArgumentException("Must provide valid numbers");
		}
		
		if(clientID <= 0) {
			//close b/c leaving early
			try {
				in.close();
			} catch (IOException e) { }
			
			throw new IllegalArgumentException("Must provide nonnegative, nonzero client ID");
		}
		
		//TODO: error check IP address??
		
		
		
		if(DEBUG) {
			System.out.println("***************************");
			System.out.println("input was: " + s);
			System.out.println("clientID: " + clientID);
			System.out.println("IP address: " + addressIP);
			System.out.println("intitializations done");
			System.out.println("***************************");
		}
		
		
		while(true) {
			
			try {
				s = in.readLine();
			} catch (IOException e) {
				break;
			}
			
			if(s == null) {
				break;
			}
			
			if(DEBUG) {
				System.out.println("read input line: " + s);
			}
			
			parts = s.split(" ");
			
			if(parts.length == 2 && parts[0].equals("sleep")) {
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
			else if(parts.length == 4) {
				int portNum = 0;
				
				try {
					portNum = Integer.parseInt(parts[2]);
				} catch (NumberFormatException e) {
					continue;
				}

				//clientNum b# reserve|return
		        String message = "" + clientID + " " + parts[0] + " " + parts[1];
				
				if(parts[3].equals("U")) { //UDP
					
					if(DEBUG) {
						System.out.println("entering UDP with msg: " + message);
					}
					
					sendUDP(portNum, message);
				}
				else if(parts[3].equals("T")) { //TCP
					
					if(DEBUG) {
						System.out.println("entering TCP with msg: " + message);
					}
					
					sendTCP(portNum, message);
				}
				else {
					//TODO: anything???
					if(DEBUG) {
						System.out.println("input deemed incorrect, skipping");
						System.out.println("***************************");
					}
				}	
				
				
			}
			else {
				//TODO: anything???
				if(DEBUG) {
					System.out.println("input deemed incorrect, skipping");
					System.out.println("***************************");
				}
			}			
		}		
	}
	
	private static void sendUDP(int port, String message) {
		String hostname = addressIP;
		String retstring = null;
        int len = 1024;
        byte[] rbuffer = new byte[len];
        DatagramPacket sPacket, rPacket;
        
        try {
            InetAddress ia = InetAddress.getByName(hostname);
            DatagramSocket datasocket = new DatagramSocket();
            Scanner sc = new Scanner(System.in);
            
            byte[] buffer = new byte[len];
            buffer = message.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
            datasocket.send(sPacket);            	
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            datasocket.receive(rPacket);
            retstring = new String(rPacket.getData(), 0, rPacket.getLength());
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        
        System.out.println(retstring);
	}
	
	private static void sendTCP(int port, String message) {
		Socket clientSocket;
		String hostname = addressIP;
		
		try {
			clientSocket = new Socket(hostname, port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer.writeBytes(message + '\n');
			System.out.println(inFromServer.readLine());
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	}

}
