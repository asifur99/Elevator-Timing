package floorSubsystem;

import sharedObjects.FloorRequestBox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import sharedObjects.Constants;
import sharedObjects.Error;

/**
 * Thread is responsible for managing the requests from elevators to open
 * the doors when the elevators reach a target floor
 * @author Jyotsna Mahesh, Md Aiman Sharif
 *
 */
public class FloorResponse implements Runnable{
	private final FloorRequestBox REQUEST_BOX;
	private final FloorRequestBox RESPONSE_BOX;

	/**
	 * Construct a new floor
	 * Default constructor initializing instance variables
	 * 
	 * @param floorId that is passed in as a parameter
	 * @param requestBox instance of a FloorRequestBox being passed in
	 */
	public FloorResponse(FloorRequestBox requestBox, FloorRequestBox responseBox) {
		this.REQUEST_BOX = requestBox;
		this.RESPONSE_BOX = responseBox;
	}
	
	/**
	 * Run method
	 */
	public void run() {
		while(true) {
			int floorResponse = 0;
			int elevResponse = 0;
			Error error = Error.NONE;
			floorResponse = REQUEST_BOX.getFloorResponse(); //FileLoader on which the elevator is on
			elevResponse = REQUEST_BOX.getElevResponse(); //Elevator number whose doors are being opened
			error = REQUEST_BOX.getErrorResponse(); //Error type
			System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Elevator " + elevResponse + " Doors Opening on: " + floorResponse);	
			
			// wait 9.39 seconds 
			try {
				Thread.sleep(Constants.DOOR_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (error == Error.DOOR) {//Door Error has occured
				System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Elevator " + elevResponse + " Doors Stuck on: " + floorResponse);
				sendErrorToUI(elevResponse, Error.DOOR);// updated UI with error
				
				error = Error.NONE;
				try {// Simulate taking time to clear the errors with the door
					System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Cycling Door to clear error");
					Thread.sleep(Constants.DOOR_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				sendErrorToUI(elevResponse, Error.NONE); // update to ui that error was cleared
			} 
			System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Elevator " + elevResponse + " Doors Closing on: " + floorResponse);		
			RESPONSE_BOX.putDoorCloseResponse(elevResponse);
		}
	}
	
	/**
	 * Send an error message to the UI to update it about 
	 * its error status
	 * 
	 * @param lift the elevator in a door jam
	 * @param error the error type
	 */
	private void sendErrorToUI(int lift, Error error) {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[11]);
		buffer.putInt(Constants.HEADER).put((byte) 10).put((byte) 3).putInt(lift).put(error.getValue());
		
		System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Updating elevator error status for elevator " + lift + "\n");
		
		DatagramPacket packet = null;
		try {
			packet = new DatagramPacket(buffer.array(), 11, InetAddress.getLocalHost(), 5019);
		} catch (UnknownHostException e) {
			System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Encountered error getting localhost");
			e.printStackTrace();
			System.exit(1);
		}
		
		while(true) {
			try(DatagramSocket socket = new DatagramSocket()) {
				socket.setSoTimeout(2000);
				
				// Send update and wait on a response. If 2 seconds occurs, assume an error and retry.
				socket.send(packet);
				socket.receive(packet);
				if(packet.getData()[4] == 0) {
					// Valid response. Update occurred.
					break;
				}
				System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Encountered error response when updating UI.\n");
			} catch (IOException e) {
				System.out.println("Time:" + System.currentTimeMillis() + ": FloorResponse Thread : Encountered timeout with socket, retrying.\n");
			}
		}
	}
}
