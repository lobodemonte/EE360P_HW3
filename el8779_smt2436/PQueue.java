package el8779_smt2436;

public class PQueue {

	/**
	 * Creates a Priority Queue with maximum allowed size as m
	 * @param m
	 */
	public PQueue(int m) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Inserts the name with its priority in the PQueue
	 * This method blocks when full
	 * @param name
	 * @param priority
	 * @return -1 if the name is already present, 
	 * otherwise, returns the current position in the list where the name was inserted
	 */
	public int insert(String name, int priority){ return -1;}
	/**
	 * @param name
	 * @return the position of the name in the list, if not found return -1
	 */
	public int search(String name){return -1;}
	/**
	 * @return name with the highest priority in the list
	 * If the list is empty, then the method blocks.
	 * The name is deleted from the list.
	 */
	public String getFirst(){ return "";}

}
