package elevatorSubsystem;

import java.util.LinkedList;
import java.util.Queue;

import sharedObjects.ElevatorRequest;
/**
 * This box is where any elevator that failed to process all their jobs before stopping
 * will submit them so that they can be reprocessed by the scheduler
 * 
 * @author Edmond
 *
 */
public class FailedJob {
	private Queue<ElevatorRequest> queue = new LinkedList<ElevatorRequest>();
	/**
	 * Add a new job to be resent to scheduler
	 * @param request
	 */
	public synchronized void placeRequest(ElevatorRequest request) {
        queue.add(request);
        notifyAll();
    }
	/**
	 * get job added to queue
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized ElevatorRequest takeRequest() throws InterruptedException{	
		while (queue.size() == 0) {
			wait();
		}
		notifyAll();
		return queue.remove();
	}
}
