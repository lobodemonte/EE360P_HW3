
public class Client {
	
	static void main (String args[]) {
		
		if(args.length < 1) {
			throw new IllegalArgumentException("Must provide input");
		}
		
		String parts[] = args[0].split(" ");
		
		if(parts.length != 2) {
			throw new IllegalArgumentException("Must provide <clientID ip_server>");
		}
		
		int clientID;
		String addressIP = parts[1];
		
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
		
	}

}
