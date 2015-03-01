import java.net.*; import java.io.*; import java.util.Scanner;

public class Client {
	
	private static int clientID;
	private static String addressIP;
	
	static void main (String args[]) {
		
		if(args.length < 1) {
			throw new IllegalArgumentException("Must provide input");
		}
		
		String parts[] = args[0].split(" ");
		
		if(parts.length != 2) {
			throw new IllegalArgumentException("Must provide <clientID ip_server>");
		}
		
		addressIP = parts[1];
		
		try {
			clientID = Integer.parseInt(parts[0]);
		}
		catch(NumberFormatException e) {
			throw new IllegalArgumentException("Must provide valid numbers");
		}
		
		if(clientID <= 0) {
			throw new IllegalArgumentException("Must provide nonnegative, nonzero client ID");
		}
		
		//TODO: error check IP address
		
		//valid
		
		for(int i = 1; i < args.length; i++) {
			parts = args[i].split(" ");
			
			if(parts.length == 2 && parts[0].equals("sleep")) {
				long sleeptime = 0;
				
				try {
					sleeptime = Long.parseLong(parts[1]);
				}
				catch(NumberFormatException e) {
					continue;
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
				
				if(parts[3].equals("U")) { //UDP
					
					//clientNum b# reserve|return
			        String message = "" + clientID + " " + parts[0] + " " + parts[1];
					
					String returnMsg = sendUDP(portNum, message);
				}
				else if(parts[3].equals("T")) { //TCP
					
				}
				
				
			}
			else {
				//TODO: something??
			}
			
			
		}
		
	}
	
	private static String sendUDP(int port, String message) {
		String hostname = addressIP;
		String retstring = null;
        int len = 1024;
        byte[] rbuffer = new byte[len];
        DatagramPacket sPacket, rPacket;
        
        try {
            InetAddress ia = InetAddress.getByName(hostname);
            DatagramSocket datasocket = new DatagramSocket();
            Scanner sc = new Scanner(System.in);
            
            byte[] buffer = new byte[message.length()];
            buffer = message.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
            datasocket.send(sPacket);            	
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            datasocket.receive(rPacket);
            retstring = new String(rPacket.getData(), 0,
            		rPacket.getLength());
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        
        return retstring;
	}

}
