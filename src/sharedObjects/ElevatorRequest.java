package sharedObjects;

import java.time.LocalTime;

import floorSubsystem.ElevatorRequestParseException;

/**
 * This is used for creating data from an input string.
 * 
 * @author Ashton Mohns, Jyotsna Mahesh
 *
 */
public class ElevatorRequest {
	private LocalTime time;
	private int floor;
	private Direction direction;
	private int carButton;
	private Error errorType; //error encountered 
	private int errfloor; // floor having the error
	/**
	 * Private constructor. Should only create through parse method.
	 */
	public ElevatorRequest(int floor, int carButton, Direction direction, Error errorType, int errfloor) {
		this.floor = floor;
		this.carButton = carButton;
		this.direction = direction;
		this.errorType = errorType;
		this.errfloor = errfloor;
	}
	
	private ElevatorRequest() {
		// Should only be constructible through parsing.
	}
	
	/**
	 * Create an ElevatorRequest from an input string.
	 * 
	 * @param input a string to parse
	 * @return an ElevatorRequest parsed from the string
	 * @throws ElevatorRequestParseException failed to parse elevator request
	 */
	public static ElevatorRequest parse(String input) throws ElevatorRequestParseException {
		String[] split = input.split(" ");
		if(split.length != 6) {
			throw new ElevatorRequestParseException("Input did not fit requried format: [Time int Direction int Error int]");
		}
		
		ElevatorRequest elevatorRequest = new ElevatorRequest();
		try {
		elevatorRequest.time = LocalTime.parse(split[0]);
		elevatorRequest.floor = Integer.parseInt(split[1]);
		elevatorRequest.direction = Direction.valueOf(split[2].toUpperCase());
		elevatorRequest.carButton = Integer.parseInt(split[3]);
		elevatorRequest.errorType = Error.valueOf(split[4].toUpperCase());
		elevatorRequest.errfloor = Integer.parseInt(split[5]);
		
		} catch (IllegalArgumentException e) {
			throw new ElevatorRequestParseException("Cannot parse string: " + input, e);
		}
		
		return elevatorRequest;
	}
	
	/**
	 * Time getter
	 * @return time
	 */
	public LocalTime getTime() {
		return time;
	}

	/**
	 * Get method for getting floor
	 * @return floor
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * Get method for getting direction
	 * @return direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Get method for getting car button
	 * @return carButton
	 */
	public int getCarButton() {
		return carButton;
	}
	
	/**
	 * Get method for getting error Type
	 * @return Error Type
	 */
	public Error getError() {
		return errorType;
	}
	
	/**
	 * Get method for getting error floor
	 * @return floor having the error
	 */
	public int getErrorFloor() {
		return errfloor;
	}
	
	/**
	 * toString method for a string representation of instance variables
	 */
	@Override
	public String toString() {
		return time + " " + floor + " " + direction.toString() + " " + carButton + " " + errorType + " " + errfloor;
	}
}
