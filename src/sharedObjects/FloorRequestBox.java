package sharedObjects;

import java.util.LinkedList;
import java.util.Queue;

/**
 * FloorRequestBox is used to pass requests between the Floor subsystem and the SenderFM or ReceiverFM.
 * This will allow requests to be passed to the UDP endpoints and sent to the scheduler subsystem.
 * 
 * @author Jyotsna Mahesh
 */
public class FloorRequestBox {

	/*
	 * Queues to store requests and responses
	 */
	private Queue<ElevatorRequest> queue = new LinkedList<ElevatorRequest>();
	private Queue<Integer> floorResponseQueue = new LinkedList<Integer>();
	private Queue<Integer> elevatorResponseQueue = new LinkedList<Integer>();
	private Queue<Integer> doorResponseQueue = new LinkedList<Integer>();
	private Queue<Error> errResponseQueue = new LinkedList<Error>();
	
	
	/**
	 * Put or add request to the queue
	 * @param request which is passed as a parameter
	 */
    public synchronized void putRequest(ElevatorRequest request) {
        queue.add(request);
        notifyAll();
    }

    /**
     * Gets the Request from queue. 
     * 
     */
    public synchronized ElevatorRequest getRequest() {
        while (queue.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        ElevatorRequest elevatorRequest = queue.remove();
        return elevatorRequest;
    }
    
    /**
	 * Take responses received by ReceiverFM and adds it to queue.
	 * 
	 * @param response: response made after processing request
	 */
	public synchronized void putResponse(int floor_num, int elev_num, Error err_num) {
		floorResponseQueue.add(floor_num);
		elevatorResponseQueue.add(elev_num);
		errResponseQueue.add(err_num);
        notifyAll();
	}
	
	/**
	 * Gets the FileLoader number on which the elevator is to the FloorResponse
	 * @return floor number response from elevator
	 */
	public synchronized int getFloorResponse() {
		while (floorResponseQueue.size()==0) {
			try {
                wait();
            } catch (InterruptedException e) {
                return 0;
            }
		}
		
		int response = floorResponseQueue.remove();
        return response;
	}
	
	/**
	 * Gets the elevator number whose doors will be opened to the FloorResponse
	 * @return elevator number response from elevator
	 */
	public synchronized int getElevResponse() {
		while (elevatorResponseQueue.size()==0) {
			try {
                wait();
            } catch (InterruptedException e) {
                return 0;
            }
		}
		
		int response = elevatorResponseQueue.remove();
        return response;
	}
	
	/**
	 * Gets the Error encountered
	 * 
	 * @return error num response from elevator
	 */
	public synchronized Error getErrorResponse() {
		while (errResponseQueue.size()== 0) {
			try {
                wait();
            } catch (InterruptedException e) {
                return Error.NONE;
            }
		}
		
		Error response = errResponseQueue.remove();
        return response;
	}
	
	/**
	 * If door close error,  add response to queue be sent to thread 4
	 * @param response: response made after processing request
	 */
	public synchronized void putDoorCloseResponse(int elev_num) {
		doorResponseQueue.add(elev_num);
        notifyAll();
	}
	
	/**
	 * Get response from queue to send to thread 4
	 * @return response from elevator
	 */
	public synchronized int getDoorCloseResponse() {
		while (doorResponseQueue.size()==0) {
			try {
                wait();
            } catch (InterruptedException e) {
                return 0;
            }
		}
		
		int response = doorResponseQueue.remove();
        return response;
	}	
}
