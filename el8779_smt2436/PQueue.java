package el8779_smt2436;

public class PQueue {

	private final int maxSize_;
	private int size;
	Node head = null;
	
	/**
	 * Creates a Priority Queue with maximum allowed size as m
	 * @param m
	 */
	public PQueue(int m) {
		maxSize_ = m;
		size = 0;
	}
	
	/**
	 * Inserts the name with its priority in the PQueue
	 * This method blocks when full
	 * @param name
	 * @param priority
	 * @return -1 if the name is already present, 
	 * otherwise, returns the current position in the list where the name was inserted
	 */
	public int insert(String name, int priority){ 
	
		if (size >= maxSize_){
			//BLOCK
		}
		
		return -1;
	
	}
	
	/**
	 * @param name
	 * @return the position of the name in the list, if not found return -1
	 */
	public int search(String name){
		
		for (int i = 0; i < size; i++){
			
		}
		return -1;
	}
	
	/**
	 * @return name with the highest priority in the list
	 * If the list is empty, then the method blocks.
	 * The name is deleted from the list.
	 */
	public String getFirst(){ 
		return "";
	}

	
	class Node {
		final String name;
		final int priority;
		Node next;
		public Node(String name, int priority, Node next){
			this.name = name;
			this.priority = priority; 
			this.next = next;
		}
		
	}
	
	
}
