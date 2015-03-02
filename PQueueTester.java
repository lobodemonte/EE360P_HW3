import java.util.concurrent.TimeUnit;

public class PQueueTester {
	
	public static PQueue queue = new PQueue(5);
	private static enqThread[]    enqs;
	private static deqThread[]    deqs;
	private static searchThread search;
	private static int attempts = 10;
	
	private static class enqThread extends Thread {
		public void run(){
			for(int i = 0; i < attempts; i++){
				int priv = (int)(Math.random()*10);
				System.out.println("OBJ"+i+": "+priv+" Inserted at: "+queue.insert("OBJ"+i, priv));

				try {
					TimeUnit.MILLISECONDS.sleep(50*this.getId());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(int i = 0; i < attempts; i++){
				int priv = (int)(Math.random()*10);
				System.out.println("OBJ"+i+": "+priv+" Inserted at: "+queue.insert("OBJ"+i, priv));

				try {
					TimeUnit.MILLISECONDS.sleep(50*this.getId());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(queue);
		}
	}
	private static class deqThread extends Thread {
		public void run(){
			
			for(int i = 0; i < attempts; i++){
				System.out.println("Dequeued: "+queue.getFirst()+" by "+this.getId());
				
				try {
					TimeUnit.MILLISECONDS.sleep(50*this.getId());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(int i = 0; i < attempts; i++){
				System.out.println("Dequeued: "+queue.getFirst()+" by "+this.getId());
				
				try {
					TimeUnit.MILLISECONDS.sleep(50*this.getId());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
	}
	private static class searchThread extends Thread {
		public void run(){
			
			for(int i = 0; i < attempts; i++){
				System.out.println("OBJ:"+i+" at "+queue.search("OBJ"+i));
				
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void test(int numDeq, int numEnq){
		enqs   = new enqThread[numEnq];
		deqs   = new deqThread[numDeq];
		
		search = new searchThread();
		
		for(int i = 0; i < enqs.length; i++) {
			enqs[i] = new enqThread();
		}	
		for(int i = 0; i < deqs.length; i++) {
			deqs[i] = new deqThread();
		}
		
		
		for(int i = 0; i < enqs.length; i++) {
			enqs[i].start();
		}
		for(int i = 0; i < enqs.length; i++) {
			deqs[i].start();
		}
		/*for(int i = 0; i < enqs.length; i++) {
			try {
				enqs[i].join();	//
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < deqs.length; i++) {
			try {
				deqs[i].join();	//
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		//search.start();
		
		
		
		
		
		
		
	}
	
}
