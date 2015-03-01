
public class Server {
	
	static void main (String args[]) {
		
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
		
		boolean bookAvail[] = new boolean[numBooks];
		for(int i = 0; i < bookAvail.length; i++) {
			bookAvail[i] = true;
		}
		
		//SERVER STUFF:
		//TODO:
		
	}

}
