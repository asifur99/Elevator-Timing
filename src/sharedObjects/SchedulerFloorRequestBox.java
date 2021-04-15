package sharedObjects;

import java.util.LinkedList;
/**
 * Shared memory of jobs sent by floor subsystem to be processed 
 * by scheduler thread 2
 * @author Edmond Chow, Ashton Mohns
 *
 */
public class SchedulerFloorRequestBox {
	private LinkedList<ElevatorRequest> requestQueue;
	
	/**
	 * Default constructor initializing instance variables
	 */
	public SchedulerFloorRequestBox() {
		this.requestQueue = new LinkedList<ElevatorRequest>();
	}
	
	/**
	 * Method to add elevator request
	 * @param request passed in as a parameter
	 */
    public synchronized void addRequest(ElevatorRequest request) {
        requestQueue.add(request);
        
        if(requestQueue.size() == 1) {
        	notifyAll();
        }
		notifyAll();
    }
    
    /**
     * Method to get the elevator request at specific index
     * @return elevator request object containing job details
     * @throws InterruptedException
     */
    public synchronized ElevatorRequest getRequest(int index) throws InterruptedException {
        while (requestQueue.size() == 0) {
            wait();
        }
        
        return requestQueue.get(index);
    }
    
    /**
     * Get size of request queue
     * @return requestQueue.size -> queue size
     */
    public synchronized int getSize() {
    	return requestQueue.size();
    }
    
    /**
     * Remove job request at index
     * @param index at which request will be removed
     */
    public synchronized void remove(int index) {
    	requestQueue.remove(index);
    }
}

