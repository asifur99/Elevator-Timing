package elevatorSubsystem;

import sharedObjects.*;
import sharedObjects.Error;

/**
 * Request handler is a data type class used to hold job information
 * between the scheduler and the elevators
 * @author Asifur Rahman,Edmond Chow
 */
public class RequestHandler {
	private int reqFloor, destFloor, open, elevatorId, errorFloor;
	private Direction direction, actual;
	private Error errorType ;

    /**
	 * Default constructor to initialize the instant variables
	 * @param e unique elevator ID 
	 * @param d direction of the elevator
	 * @param reqFloor current floor of the elevator
	 * @param destFloor where the elevator should be going after picking up the user
	 * @param open should the door on the floor be open or remain closed
	 * @param errorType what kind of error being passed
	 * @param errorFloor which floor the error happened on
	 */
    public RequestHandler(int e, Direction d, int reqFloor, int destFloor, int open, Error errorType, int errorFloor){
		this.elevatorId = e;
		this.direction = d;
		this.reqFloor = reqFloor;
		this.destFloor = destFloor;
		this.open = open;
		this.errorType = errorType;
		this.errorFloor = errorFloor;
		this.actual = Direction.STATIONARY;
	}
    /**
     * Constructor variant used when sending information from elevator to scheduler
	 * @param e unique elevator ID 
	 * @param d direction of the elevator
	 * @param reqFloor current floor of the elevator
	 * @param destFloor where the elevator should be going after picking up the user
	 * @param open should the door on the floor be open or remain closed
	 * @param errorType what kind of error being passed
	 * @param errorFloor which floor the error happened on
	 * @param actual direction that the elevator actually moved
     */
    public RequestHandler(int e, Direction d, int reqFloor, int destFloor, int open, Error errorType, int errorFloor,Direction actual){
		this.elevatorId = e;
		this.direction = d;
		this.reqFloor = reqFloor;
		this.destFloor = destFloor;
		this.open = open;
		this.errorType = errorType;
		this.errorFloor = errorFloor;
		this.actual = actual;
	}

	/**
	 * Get method to get the requested floor
	 * @return reqFloor requested floor
	 */
	public int getReqFloor() {
		return reqFloor;
	}
	
	/**
	 * Get method for returning the destination floor
	 * @return destFloor destination floor
	 */
	public int getDestFloor() {
		return destFloor;
	}
	
	/**
	 * Get method for returning the direction
	 * @return direction direction of the elevator
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Get method to get the car button
	 * @return carButton car button pressed
	 */
	public int getElevatorId() {
		return elevatorId;
	}
	
	/**
	 * Method to check for open or not open
	 * @return open open or not
	 */
	public int status() {
		return open;
	}

	/**
	 * Method to get errorType value
	 * @return errorType type of error
	 */
	public Error getErrorType() {
		return errorType;
	}

	/**
	 * Method to get errorFloor value
	 * @return errorFloor which floor error happened
	 */
	public int getErrorFloor() {
		return errorFloor;
	}
	
	/**
	 * Method to get actual direction of movement
	 * @return return direction elevator actually moved
	 */
	public Direction getActual() {
		return actual;
	}
}