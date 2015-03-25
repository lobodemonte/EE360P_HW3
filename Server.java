import java.net.*;
import java.io.*;
import java.util.PriorityQueue;

public class Server {
	
	private static final boolean DEBUG = true;
	static int servedCount = 0; 	//If k is greater than or equal to this private counter, the server
	//immediately 'crashes'; otherwise it will crash as soon as the private counter reaches k. 
	//resets to zero after it comes back up after the crash.
	static int bookOwner[];			//clientID possessing corresponding book, -1 for server having book
	static String servers[];		//Contains the address of own and other servers
	static int serverid;
	
	public static void main(String[] args) {
		
		final int nInstances, numBooks; 
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		
		try {
			s = in.readLine(); // TODO: how to handle no input? Do nothing?
			
			try {
				
				
				String[] parts = s.split(" ");
				
				if(parts.length != 3) {
					throw new IllegalArgumentException("Input must be of form <serverid n z> "); 
				}
				
				
				
				try {
					serverid   = Integer.parseInt(parts[0]);
					nInstances = Integer.parseInt(parts[1]);	//n
					numBooks   = Integer.parseInt(parts[2]);	//z

					if (serverid <= 0 || nInstances <= 0 || numBooks <= 0) { 
						throw new IllegalArgumentException("Must provide nonnegative, nonzero numbers");
					}
					
					bookOwner = new int[numBooks]; 
					servers = new String[nInstances];
					
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
					
					//TODO: crash k delta
					
					in.close();
					
					TCPServer(port);
					
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
        BufferedReader inFromPort;
        DataOutputStream outToPort;
        String clientRequest;
        PriorityQueue<Request> requests = new PriorityQueue<Request>();
        LClock clock = new LClock(serverid);
        
		try {
			servSocket = new ServerSocket(port);
			
			while(true){
				try {
					
					//accept message
					
					//if: client message
						//add to own queue
						//send messages
					
					//if: server message
						//case a: request received: add to queue, reply with timestamped ack
						//case b: ack received: record.
						//case c: releaseCS received: remove from queue
						//case other: recovery related ... leave as TODO for now
					
					//if appropriate to enter CS:
						//enter CS, do the book thing
						//reply to client
						//send release to all servers (include summary of change, if any)
					
					
					
					connectionSocket = servSocket.accept();
					/*
					inFromPort = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			        outToPort = new DataOutputStream(connectionSocket.getOutputStream());
			        
			        clientRequest = inFromPort.readLine();
			        //clientRequest = "" + inFromClient.read();
			        
			        if(DEBUG){
			        System.out.println("TCP received: " + clientRequest);
			        }
			         
			        String returnMsg = serveRequest(clientRequest);
			        outToPort.writeBytes(returnMsg + "\n");
			        */
			        
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
/*		
		if(servSocket != null) {
			try {
				servSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}

	
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

	private static boolean reserveBook (int book, int clientNum) {
		
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
	
	private static boolean returnBook (int book, int clientNum) {
		
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

	private static class Request implements Comparable{
		private int owner; //id of server that owns the request
		private LClock ts; //timestamp of request
		private int acks; //number of acknowledgements TODO: is it important to remember which threads have responded or no?
		
		public Request(int o, LClock t) {
			owner = o;
			ts = t;
			acks = 0;
			
			ts.setPID(owner); //just to be redundantly sure they're the same, should already be
		}
		
		public LClock getTimeStamp() {
			return ts.getTimeStamp();
		}
		
		public Request(String s) {
			String[] parts = s.split(" ");
			owner = Integer.parseInt(parts[0]);
			ts = new LClock(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			acks = 0; //in reality, if we construct in this manner, we won't care about the acks, bc not our request
		}

		@Override
		/**
		 * return value: see LClock.compareTo
		 */
		public int compareTo(Object o) {
			
			if(o instanceof Request) {
				return this.ts.compareTo(((Request) o).ts);
			}
			
			else { //TODO: this shouldn't happen???
				return -1;
			}
		}
	
		public String toString() {
			return "" + owner + " " + ts.toString();
		}
	}
	
	private static class LClock implements Comparable{
		private int clock;
		private int pid;
		
		public LClock(int id) {
			pid = id;
			clock = 0;
		}
		
		public void setPID(int id) {
			pid = id;
		}
		
		public LClock getTimeStamp() {
			LClock c = new LClock(pid);
			c.clock = this.clock;
			
			return c;
		}
		
		public LClock(int c, int i) {
			clock = c;
			pid = i;
		}
		
		public void tick() {
			clock++;
		}
		
		/**
		 * Updates clock to be 1 tick ahead of the max of this and other
		 * @param other: other clock to compare with
		 */
		public void updateClock(LClock other) {
			
			if(this.compareTo(other) < 0) {
				this.clock = other.clock + 1;
			}
			else {
				this.clock++;
			}
		}

		@Override
		/**
		 * returns a negative number if this < o
		 * returns 0 if this == o
		 * returns a positive number if this > o
		 */
		public int compareTo(Object o) {
	
			if(o instanceof LClock)
			{
				LClock other = (LClock) o;
				
				//compare based on clock value:
				int comp = ((Integer) this.clock).compareTo((Integer) other.clock);
				
				if(comp == 0) { //break tie with pid
					return ((Integer) this.pid).compareTo((Integer) other.pid);
				}
				else {
					return comp;
				}
			}
			else { //TODO: this shouldn't happen?
				return -1;
			}
		}
		
		public String toString() {
			return "" + clock + " " + pid;
		}
	}

}
