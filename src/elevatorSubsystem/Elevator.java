package elevatorSubsystem;

import sharedObjects.Constants;
import sharedObjects.Direction;
import sharedObjects.ElevatorRequest;
import sharedObjects.SchedulerElevatorBox;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import sharedObjects.Error;

/**
 * Class is core of each elevator thread, it calls its motor to move floors
 * and will hard fault incase of errors.
 * 
 * @author Edmond Chow
 *
 */
public class Elevator implements Runnable {
	private RequestHandler request;
	private RequestHandler additional;
	private Queue<RequestHandler> jobs = new LinkedList<>();
	private SortedSet<Integer> locations = new TreeSet<Integer>();
	private SchedulerElevatorBox channel;
	private FailedJob reschedule;
	private int id;
	private int position;
	private Direction direction = Direction.STATIONARY;
	private ElevatorMotor motor;
	private DoorBox doorChannel;
	private HashMap<Integer,Error> map = new HashMap<Integer,Error>();
	private Error type = Error.NONE;
	private int errorFloor = 0;
	private Direction move = Direction.STATIONARY;
	
	/**
	 * Default constructor initializing instance variables
	 * @param sechannel instance of SchedulerElevatorBox
	 * @param id of the scheduler elevator
	 */
	public Elevator (SchedulerElevatorBox sechannel, int id, ElevatorMotor motor, DoorBox dc, FailedJob rc) {
		this.channel = sechannel;
		this.request = null;
		this.id = id;
		this.position = 1;
		this.motor = motor;
		doorChannel = dc;
		reschedule = rc;
	}
	
	/**
	 * Move elevator up or down one floor based on current direction state
	 * @return true if no faults while moving, false if fault while moving
	 */
	public boolean move() {
		boolean status;
		long start,time; 
		int target;
		
		if (direction == Direction.UP) {
			target = locations.first();
		} else {
			target = locations.last();
		}
		if (target > position) {
			move = Direction.UP;
			start = System.currentTimeMillis();
			status = motor.move(position, type, errorFloor);
			time = start = System.currentTimeMillis() - start;
			position++;

		} else if (target < position) {
			move = Direction.DOWN;
			start = System.currentTimeMillis();
			status = motor.move(position, type, errorFloor);
			time = System.currentTimeMillis() - start;
			position--;
		} else {
			move = Direction.STATIONARY;
			return true;
		}
		if (time > (Constants.MOVE_TIME + 1000)) {
			System.out.println("Time: " + System.currentTimeMillis() + ": " + "Elevator " + id
					+ " floor timer failed took " + time);
			return false;
		}
		if (status) {
			System.out.println(
					"Time: " + System.currentTimeMillis() + ": " + "Elevator " + id + " Arrival Sensor failed");
			return false;
		}
		return true;
	}
	
	/**
	 * Send update to scheduler about new position, and if hard faulted send update about stop state
	 * @return if door open packet was sent or not,
	 */
	public boolean sendUpdate() {
		int nextOpen = 0;
		
		//if elevator going up, next stop floor is smallest value in set
		if (direction == Direction.UP) {
			nextOpen = locations.first();
			
		}
		//if elevator is going down, next stop floor is largest value in set
		if (direction == Direction.DOWN) {
			nextOpen = locations.last();
		}
		if(nextOpen!=position) {
			channel.placeMovementUpdate(new RequestHandler(id, direction, position, 0, 0, type,errorFloor,move));
			return false;
		} else {
			move = Direction.STATIONARY;
			//if on stop floor send door open signal
			channel.placeMovementUpdate(new RequestHandler(id, direction, position, 0, 1, type,errorFloor,move));//Maybe remove doors open state
			locations.remove(nextOpen);
			return true;
		}
	}
	
	/**
	 * Check if any job has both its requester floor and destination floor visited, if it was remove it from jobs queue
	 */
	public void checkJobCompletion() {
		Iterator<RequestHandler> i = jobs.iterator();
		while(i.hasNext()) {
			RequestHandler job = (RequestHandler) i.next();
			if(!locations.contains(job.getDestFloor()) && !locations.contains(job.getReqFloor()) ){
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator " + id +" done job " + job.getReqFloor() + " to " + job.getDestFloor());
				i.remove();
			}
		}
	}
	
	/**
	 * If the elevator has hard faulted, submit jobs to be resent to scheduler
	 */
	public void uploadFailedJobs() {
		Iterator<RequestHandler> i = jobs.iterator();
		while (i.hasNext()) {
			RequestHandler job = (RequestHandler) i.next();
			if (map.containsKey(job.getErrorFloor())) {// if error has yet to be handled, put error in the rerequest
				reschedule.placeRequest(new ElevatorRequest(job.getReqFloor(), job.getDestFloor(), job.getDirection(),
						job.getErrorType(), job.getErrorFloor()));
			} else {// if error has been handled, resend request without error
				reschedule.placeRequest(
						new ElevatorRequest(job.getReqFloor(), job.getDestFloor(), job.getDirection(), Error.NONE, 0));
			}
		}
	}
	
	/**
	 * get if there will be an error as the elevator moves a floor up or down
	 */
	public void getError() {
		int shiftedPosition = position;
		int target;
		
		if (direction == Direction.UP) {
			target = locations.first();
		} else {
			target = locations.last();
		}
		if (target > position) {
			shiftedPosition = position + 1;
		} else if (target < position) {
			shiftedPosition = position - 1;
		}
		if (map.containsKey(shiftedPosition)) {
			type = map.remove(shiftedPosition);
			errorFloor = shiftedPosition;
		} else {// if no error on next floor, set variables as such
			type = Error.NONE;
			errorFloor = 0;
		}
	}
	/**
	 * run method
	 */
	public void run(){
		while(direction!=Direction.STOP) {
			// get job, add the target floors to queue and job to job queue
			request = channel.takeRequest(id);
			locations.add(request.getReqFloor());
			locations.add(request.getDestFloor());
			jobs.add(request);
			System.out.println("Time: " + System.currentTimeMillis() + ": " + "Elevator " + id + " taking job "
					+ request.getReqFloor() + " to " + request.getDestFloor());
			// get direction of job and errors to handle
			direction = request.getDirection();
			if (request.getErrorType() != Error.NONE) {
				map.put(request.getErrorFloor(), request.getErrorType());
			}
			boolean result;
			// while there are still target floors to visit and no faults have occured
			while (locations.size() > 0 && direction != Direction.STOP) {
				// peek for new job and don't wait if there isn't one
				additional = channel.peekGet(id);
				if (additional != null) {
					jobs.add(additional);
					locations.add(additional.getReqFloor());
					locations.add(additional.getDestFloor());
					System.out.println("Time: " + System.currentTimeMillis() + ": " + "Elevator " + id + " taking job "
							+ additional.getReqFloor() + " to " + additional.getDestFloor());
					// if new job has error, get error details
					if (additional.getErrorType() != Error.NONE) {
						map.put(additional.getErrorFloor(), additional.getErrorType());
					}
				}
				getError();
				// actually move the elevator one floor
				result = move();
				if (!result) {
					direction = Direction.STOP;
				}
				// send updated position and direction to scheduler
				result = sendUpdate();
				checkJobCompletion();
				// if reached stop floor wait for floor to send door closed signal
				if (result) {
					doorChannel.getClosedSignal(id);
				}
			}
			if (direction != Direction.STOP) {
				direction = Direction.STATIONARY;
				channel.placeMovementUpdate(new RequestHandler(id, Direction.STATIONARY, position, 0, 0, Error.NONE,0 ));
			}

		}
		//elevator hard fault resend requests to thread 1
		uploadFailedJobs();
	}
}
