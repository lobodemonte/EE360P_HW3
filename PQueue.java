import java.util.Iterator;
import java.util.LinkedList;

import autosynch.*;
/** Import ImplicitMonitor Library **/
import autosynch.*;


public class PQueue {        
        /** Create Monitor Object. **/
        private final AbstractImplicitMonitor monitor_2033567664 = 
            new NaiveImplicitMonitor();


	private final int maxSize_;
	Node head = null;
	
	LinkedList<Node> list;
	
	/**
	 * Creates a Priority Queue with maximum allowed size as m
	 * @param m
	 */
	public PQueue(int m) {
		if (m <= 0){ 
			throw new IllegalArgumentException(); 
		}
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
                /* monitor */
                monitor_2033567664.enter();
                try {
 
		if ((priority < 0 && priority > 9) || name.isEmpty()){ 
			throw new IllegalArgumentException("Empty name or unsupported priority level"); 
		}
                if (!(list.size() < maxSize_)) {
                  /* Create Condition Variable*/
                  AbstractCondition condition_2066139269 = monitor_2033567664.makeCondition(
                    new Assertion() {
                      public boolean isTrue() {
                        return (list.size() < maxSize_);
                      }
                    }
                  );
                  condition_2066139269.await();
                }

		
		if (list.contains(new Node(name, priority))){
                        {
                          int ret_944889298 =  -1;

                          return ret_944889298;
                        }

		}
		
		int position = 0;
		Iterator<Node> iter = list.iterator();
		while (iter.hasNext()){
			Node n = iter.next();
			if (priority > n.priority){ 
				break; 
			}
			++position;
			
		}
		list.add(position, new Node(name,priority));
                {
                  int ret_974804600 =  position;

                  return ret_974804600;
                }

                } finally {

                /* leave monitor */
                monitor_2033567664.leave();

                }

	}
	
	/**
	 * @param name
	 * @return the position of the name in the list, if not found return -1
	 */
	public int search(String name){
                /* monitor */
                monitor_2033567664.enter();
                try {

		int position = 0;
		Iterator<Node> iter = list.iterator();
		while (iter.hasNext()){
			Node n = iter.next();
			if (n.name.equals(name)){
                                {
                                  int ret_836627874 =  position;

                                  return ret_836627874;
                                }

			}
			++position;
		}
                {
                  int ret_1629241409 =  -1;

                  return ret_1629241409;
                }

                } finally {

                /* leave monitor */
                monitor_2033567664.leave();

                }

	}
	
	/**
	 * @return name with the highest priority in the list
	 * If the list is empty, then the method blocks.
	 * The name is deleted from the list.
	 */
	public String getFirst(){
                /* monitor */
                monitor_2033567664.enter();
                try {

                if (!(list.size() > 0)) {
                  /* Create Condition Variable*/
                  AbstractCondition condition_625166124 = monitor_2033567664.makeCondition(
                    new Assertion() {
                      public boolean isTrue() {
                        return (list.size() > 0);
                      }
                    }
                  );
                  condition_625166124.await();
                }

                {
                  String ret_1677044944 =  list.removeFirst().name;

                  return ret_1677044944;
                }

                } finally {

                /* leave monitor */
                monitor_2033567664.leave();

                }

	}

	
	public String toString(){
                /* monitor */
                monitor_2033567664.enter();
                try {

                {
                  String ret_1182061102 =  list.toString();

                  return ret_1182061102;
                }

                } finally {

                /* leave monitor */
                monitor_2033567664.leave();

                }

	}
	
	public class Node {
		
		final String name;
		final int priority;
		
		public Node(String name, int priority){
			this.name = name;
			this.priority = priority; 
		}
		
		
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
		   /* if (this.priority != other.priority) {
		        return false;
		    }*/
		    return true;
		}
		
		public String toString(){
			return this.name+" "+this.priority;
		}
	}
	
	
}
