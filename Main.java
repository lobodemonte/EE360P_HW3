
public class Main {



	public static void main(String[] args) {
		
		PQueueTester.test(1, 10);
	}

	/*
	 * TODO: some thinking
	 * -remove all traces of UDP (comment out, at least)
	 * -carefully revisit input/output syntax (esp. see piazza question about localhost)
	 * -implement mutex using Lamport's Algorithm
	 * -once a process clears mutex, should communicate any changes to all other processes (& get ack?)
	 * before releasing mutex
	 * -i THINK that since we have a controlled "crash" scenario, we don't need to worry about a process
	 * dying while it has mutex???
	 * -coming back online: special request ("connect"?) "connect" should require mutex before copying over server data. 
	 * Upon receiving a "connect" request, processes should remove from queue any other requests
	 * from that process (it went offline, it doesn't remember any requests that it made)
	 * -don't think that "connect" needs to copy the queue also - by virtue of waiting for mutex, any preceding
	 * queue entries will be cleared by the time "connect" is granted mutex
	 * 
	 * 
	 * -big question: client knows to move on to different server after 100 ms. How does
	 * one process decide that another one has crashed (and gone offline)? idk
	 */
	

}
