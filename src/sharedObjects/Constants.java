package sharedObjects;

/**
 * This class contains constants required across the different classes.
 *
 * @author Ashton Mohns, Edmond Chow
 */
public final class Constants {
	
	//Enable full debug content in console output
	public static final boolean debug = false;
	
	//Amount of time required for a socket timeout to occur
    public static final int TIMEOUT_MILLIS = 60000;

    //Port for floor subsystem
    public static final int FLOOR_PORT = 5011;
    
    //Port for receiving in scheduler from elevator
	public static final int ELEVATOR_SERVER_PORT = 5014;
	
	// Ensure that the data sent is not corrupted by ensuring the matching expected header.
	// Equivalent to bytes 1011
	public static final int HEADER = 16777473;
	//Number of Elevators that will be used by the system
	public static final int elevator = 4;
	//Set how long it takes an elevator to move between floors
	public static final int MOVE_TIME = 4000;
	//used in timer faults to have elevator intentionally fail on timer test
	public static final int TIMER_FAULT = MOVE_TIME + 2000;
	//Amount of times for doors to open and then close
	public static final int DOOR_TIME = 9390;
	//Name of input file to be read by system
	public static final String FILENAME= "input.txt";
	/**
     * Ensure that an instance of Constants cannot be created.
     */
    private Constants() {}
}
