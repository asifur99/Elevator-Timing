package schedulerSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import sharedObjects.Constants;
import sharedObjects.Direction;
import sharedObjects.Error;

//Thread 3
/**
 * This is a thread which will constantly listen on port 5014 for any information that
 * was sent from an Elevator containing an updated location information.
 * 
 * @author Ashton Mohns
 * 
 */
public class ElevatorServer implements Runnable {
	private ElevatorData elevatorData;
	private DatagramSocket socket;
	
	/**
	 * Default constructor initializing instance variables
	 * @param elevatorData elevator data passed in as a parameter
	 */
	public ElevatorServer(ElevatorData elevatorData) {
		this.elevatorData = elevatorData;
		try {
            socket = new DatagramSocket(Constants.ELEVATOR_SERVER_PORT);
        } catch (SocketException se) {
            se.printStackTrace();
            if(socket != null) socket.close();
            System.exit(1);
        } 
	}
	
	/**
	 * Run method for the elevator server 
	 */
	@Override
	public void run() {
		try {
			while(true) {
				byte[] data = new byte[21];
				DatagramPacket received = receiveData(data);
				if(Constants.debug) {
					System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
					System.out.println("sending response");
				}
				sendResponse(handleData(data), received);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(socket != null) socket.close();
			System.exit(1);
		}
	}
	
	/**
     * Receive data and print information on the packet received
     * @param data the byte array to fill with the request
     * @return the DatagramPacket received
     * @throws IOException 
     */
	private DatagramPacket receiveData(byte[] data) throws IOException {
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		
		try {
			if(Constants.debug) {
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
				System.out.println("ElevatorServer: Waiting for information from an elevator"); // so we know we're waiting
			}

            socket.receive(receivePacket);
        } catch (IOException e) {
        	System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            throw e;
        }
		
		// Process the received datagram.
		System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
        System.out.println("Elevator " + data[5] + data[6] + data[7] + data[8] + 
        				   " Moved to: "+ data[10] + data[11] + data[12] + data[13] + 
    					   " Direction: " + Direction.getDirection(data[9]).name()+"\n");
        System.out.println();
        
        return receivePacket;
	}
	
	/**
	 * Expected format: Total 14 bytes
	 * byte[0-3]: header
	 * byte[4]: type (3 indicates a request to scheduler from elevator manager)
	 * byte[5-8]: elevator id (int)
	 * byte[9]: direction of the elevator
	 * byte[10-13]: floor number the elevator arrived at
	 * byte[14]: should open doors (i.e. if the elevator is just passing or stopping at this floor) 
	 * byte[15]: error code
	 * byte[16-19]: error floor
	 * 
	 * @param input data
	 * @return false if the input data was invalid
	 */
	private boolean handleData(byte[] input) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(input);
		
		//Expected to start with header and a type byte.
		if(input.length != 21 || buffer.getInt() != Constants.HEADER || buffer.get() != 3) {
			return false;
		}
		
		//Parse the remainder of byte array
		int elevatorId = buffer.getInt();
		Direction direction = Direction.getDirection(buffer.get());
		int floor = buffer.getInt();
		boolean openDoors = buffer.get() != 0;
		boolean error = buffer.get() == (byte) 3 && buffer.getInt() == floor;
		
		//Update location
		elevatorData.updateElevatorLocation(elevatorId, floor, direction);
		sendUpdateMessage(elevatorId, floor, Direction.getDirection(input[20]));
		
		if(direction == Direction.STOP) {
			// Direction.STOP occurs when an elevator hits an error to shutdown that elevator.
			sendErrorMessage(elevatorId, Error.getError(input[15]));
		} else if (openDoors) {
			sendDoorOpenNotification(elevatorId);
			return sendDataToFloorSubsystem(elevatorId, floor, error);
		}
		
		return true;
	}
	
	/**
	 * Return 1 to indicate that the request was handled or 0 to indicate a failure to handle request.
	 * 
	 * @param success if the request succeeded
	 * @param received the received datagram packet
	 * @throws IOException
	 */
	private void sendResponse(boolean success, DatagramPacket received) throws IOException {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DatagramPacket response = new DatagramPacket(
				new byte[] {1,0,1,1,(byte) (success ? 0 : 1)},
				5,
				received.getAddress(),
				received.getPort());
		if(Constants.debug) {
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
			System.out.println("Sending response:");
	        System.out.println("Containing: " );
	        System.out.println("Header: 1011");
	        System.out.println("Success: " + success);
	        System.out.println("\n");
		}
		try {
            // Send 0 on success, 1 on failure.
			socket.send(response);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
		
	}
	
	/**
	 * Send a message to the floor subsystem notifying that the door must open for a given elevator.
	 * @throws IOException
	 * @return true if response is success
	 */
	private boolean sendDataToFloorSubsystem(int elevator, int floor, boolean error) throws IOException {
		ByteBuffer data = ByteBuffer.wrap(new byte[14]);
		data.putInt(Constants.HEADER).put((byte) 4).putInt(elevator).putInt(floor).put(error ? (byte) 3 : (byte) 0);
		
		//Create data to forward to floor manager
		DatagramPacket packet = new DatagramPacket(data.array(), 14, InetAddress.getLocalHost(), Constants.FLOOR_PORT);
		
		try {
            // Send data to the floor manager
			socket.send(packet);
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
			if(Constants.debug) {
				System.out.println("Sending Door open Packet to floor manager ");
				System.out.print("Containing:");
				System.out.print(" Elevator: " + elevator);
				System.out.print(" FileLoader: " + floor);
				System.out.print(" Error: " + error);
				System.out.println("\n");
			}else {
				System.out.println("Telling FileLoader " + floor + " To open door for elevator " + elevator);
			}
			// Receive a response from floor manager
			byte[] response = new byte[5];
			DatagramPacket receivePacket = new DatagramPacket(response, 5);
			socket.receive(receivePacket);
			if(Constants.debug) {
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
				System.out.println("Received response:");
				System.out.println("Containing: " );
		        System.out.println("Header: 1011");
			}
			
			if(response[4] != (byte)0) {
				if(Constants.debug) {
					System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
					System.out.println("Success: false");
					System.out.println("Received failure response");
					System.out.println("\n");
				}
				return false;
			}
			if(Constants.debug) {
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
		        System.out.println("Success: true");
		        System.out.println("\n");
			}
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
		return true;
	}
	
	/**
	 * Send a request to the UI to update the location of the elevator
	 * 
	 * @param lift id of the elevator
	 * @param floor the floor that the elevator has arrived at
	 * @param direction direction of the elevator
	 */
	private void sendUpdateMessage(int lift, int floor, Direction direction) {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[15]);
		buffer.putInt(Constants.HEADER).put((byte) 10).put((byte) 0).putInt(lift).putInt(floor).put(direction.getValue());
		
		System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
		System.out.println("Updating elevator location for elevator " + lift + "\n");
		
		sendMessageToUI(buffer.array());
	}
	
	/**
	 * Send a request to the UI to update the error state of the elevator
	 * 
	 * @param lift id of the elevator
	 * @param error the error type that the elevator is in
	 */
	private void sendErrorMessage(int lift, Error error) {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[11]);
		buffer.putInt(Constants.HEADER).put((byte) 10).put((byte) 3).putInt(lift).put(error.getValue());
		
		System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
		System.out.println("Updating elevator error status for elevator " + lift + "\n");
		
		sendMessageToUI(buffer.array());
	}
	
	/**
	 * Send a notification to the UI saying that the doors are opening for the elevator with id lift
	 * 
	 * @param lift the id of the elevator
	 */
	private void sendDoorOpenNotification(int lift) {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[11]);
		buffer.putInt(Constants.HEADER).put((byte) 10).put((byte) 2).putInt(lift).put((byte) 1);
		
		System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
		System.out.println("Updating elevator door status for elevator " + lift + "\n");
		
		sendMessageToUI(buffer.array());
	}
	
	/**
	 * Send a request to the UI containing the input byte array
	 * 
	 * @param input the data send to the UI
	 */
	private void sendMessageToUI(byte[] input) {
		DatagramPacket packet = null;
		try {
			packet = new DatagramPacket(input, input.length, InetAddress.getLocalHost(), 5019);
		} catch (UnknownHostException e) {
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
			System.out.println("Encountered error getting localhost");
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
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 3 (ElevatorServer): ");
				System.out.println("Encountered error response when updating UI.\n");
			} catch (IOException e) {
				System.out.println("Encountered timeout with socket, retrying.\n");
			}
		}
	}
}
