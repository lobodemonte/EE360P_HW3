import java.util.LinkedList;
import autosynch.*;

public monitor class PQueue {

	private final int maxSize_;
	Node head = null;
	
	LinkedList<Node> list;
	
	/**
	 * Creates a Priority Queue with maximum allowed size as m
	 * @param m
	 */
	public PQueue(int m) {
		if (m <= 0){ throw new IllegalArgumentException(); }
		list = new LinkedList<Node>();
		maxSize_ = m;
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
		if ((priority < 0 && priority > 9) || name.isEmpty()){ 
			throw new IllegalArgumentException("Empty name or unsupported priority level"); 
		}
		waituntil(list.size() < maxSize_);
		
		if (list.contains(new Node(name, priority))){return -1;}
		
		int position = 0;
		for (Node n: list){
			if (priority > n.priority){ break; }
			++position;
			
		}
		list.add(new Node(name,priority));
		
		return position;
	}
	
	/**
	 * @param name
	 * @return the position of the name in the list, if not found return -1
	 */
	public int search(String name){
		int position = 0;
		for (Node n: list){
			if (n.name.equals(name)){
				return position;
			}
			++position;
		}
		return -1;
	}
	
	/**
	 * @return name with the highest priority in the list
	 * If the list is empty, then the method blocks.
	 * The name is deleted from the list.
	 */
	public String getFirst(){ 
		waituntil(list.size() > 0);
		return list.removeFirst().name;
	}

	
	public class Node {
		
		final String name;
		final int priority;
		
		public Node(String name, int priority){
			this.name = name;
			this.priority = priority; 
		}
		
		@Override 
		public boolean equals(Object obj) {
		    if (obj == null) {
		        return false;
		    }
		    if (getClass() != obj.getClass()) {
		        return false;
		    }
		    final Node other = (Node) obj;
		    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
		        return false;
		    }
		    if (this.priority != other.priority) {
		        return false;
		    }
		    return true;
		}
		
	}
	
	
}
