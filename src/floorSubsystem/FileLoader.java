package floorSubsystem;

import java.util.*;

import sharedObjects.Constants;
import sharedObjects.ElevatorRequest;
import sharedObjects.FloorRequestBox;

/**
 * A floor will input from the given file and send requests to the scheduler through the FloorRequestBox.
 * 
 * @author Ashton Mohns,Jyotsna Mahesh
 *
 */
public class FileLoader implements Runnable{
	private final FloorRequestBox REQUEST_BOX;

	/**
	 * Construct a new floor
	 * Default constructor initializing instance variables
	 *
	 * @param requestBox instance of a FloorRequestBox being passed in
	 */
	public FileLoader(FloorRequestBox requestBox) {
		//this.FLOOR_ID = floorId;
		this.REQUEST_BOX = requestBox;
	}
	
	/**
	 * Run method
	 */
	public void run() {
		List<ElevatorRequest> input;
		try {
			input = new FileReader(Constants.FILENAME).readFromFile();
		} catch (FileInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		for(ElevatorRequest request : input) {
			REQUEST_BOX.putRequest(request);
			System.out.println("Time: " + System.currentTimeMillis() + ": FileLoader Thread: Added request from floor to box. ");
		}
		System.out.println("Time: " + System.currentTimeMillis()+ ": FileLoader Thread: All requests have been handled.");
	}
}
