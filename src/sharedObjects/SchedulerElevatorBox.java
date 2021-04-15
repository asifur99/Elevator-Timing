package sharedObjects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import elevatorSubsystem.RequestHandler;

/**
 * This box is a shared memory space used to pass information between
 * Elevator Thread 2, Elevator, and Elevator Thread 3
 * @author Edmond Chow, Ashton Mohns
 *
 */
public class SchedulerElevatorBox {
	private Queue<RequestHandler> queue;
	private HashMap<Integer,RequestHandler> map = new HashMap<>();
	
	/**
	 * Default constructor initializing instance variables
	 * create a new box for processing requests and responses from Scheduler and Elevator
	 */
	public SchedulerElevatorBox() {
		queue = new LinkedList<RequestHandler>();
	}
	
	/**
	 * Used by Elevator Thread2 to pass job requests to the appropriate elevator
	 * @param request job request to be serviced
	 * @param id elevator that will be assigned job
	 */
	public synchronized void placeRequest(RequestHandler request, int id) {
        while (map.containsKey(id)) {
            try {
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        map.put(id, request);
        notifyAll();
    }
	
	/**
	 * Used by Elevator to take Job Requests added to it by
	 * Elevator Thread 2
	 * @param id elevator requesting job requests
	 * @return Request from scheduler to be done
	 */
	public synchronized RequestHandler takeRequest(int id) {
		while (!map.containsKey(id)) {
			try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
		}
		
		RequestHandler item = map.remove(id);
		notifyAll();
		return item;	
	}
	
	/**
	 * Used by all elevator to queue up movement data to be sent to scheduler
	 * @param response: information about and elevators change in position and status
	 */
	public synchronized void placeMovementUpdate(RequestHandler response) {
		queue.add(response);
		notifyAll();
	}
	
	/**
	 * Used by Elevator Thread 3 to get new position data to package and send to scheduler
	 * @return information about an elevators change in position and status
	 */
	public synchronized RequestHandler getMovementUpdate() throws InterruptedException{	
		while (queue.size() == 0) {
			wait();
		}
		notifyAll();
		return queue.remove();
	}
	
	/**
	 * Returns size of movement updates to send to scheduler
	 * @return number of movement updates in queue
	 */
	public int getSize(){	
		return queue.size();
	}
	
	/**
	 * Used by elevator to check shared memory for new requests while not entering waiting loop
	 * @param id elevator checking for new job
	 * @return new job to be processed or null
	 */
	public synchronized RequestHandler peekGet(int id) {
		while (!map.containsKey(id)) {
			return null;
		}
		RequestHandler item = map.remove(id);
		notifyAll();
		return item;	
	}
}
