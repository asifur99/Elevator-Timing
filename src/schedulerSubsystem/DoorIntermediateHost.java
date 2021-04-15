package schedulerSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import sharedObjects.Constants;

/**
 * DoorIntermediateHost is a thread which is constantly listening for UDP requests
 * from the floor subsystem to close doors. Whenever this occurs, the UI is updated
 * and the elevator which was waiting is notified that it can resume as the doors
 * have been successfully closed.
 * 
 * @author Ashton Mohns
 */
public class DoorIntermediateHost implements Runnable {
	private DatagramSocket socket;
	
	/**
	 * Default constructor initializing instance variables
	 * @param elevatorData elevator data passed in as a parameter
	 */
	public DoorIntermediateHost() {
		try {
            socket = new DatagramSocket(5017);
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
				byte[] data = new byte[9];
				DatagramPacket received = receiveData(data);
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
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
				System.out.println("DoorIntermediateHost: Waiting for information from a floor"); // so we know we're waiting
			}

            socket.receive(receivePacket);
        } catch (IOException e) {
        	System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            throw e;
        }
		
		// Process the received datagram.
		System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
		if(Constants.debug) {
	        System.out.println("DoorIntermediateHost: Packet received:");
	        System.out.println("From host: " + (receivePacket.getPort() == 5017 ? "FloorManager" : "Unknown"));
	        System.out.print("Containing: " );
	        System.out.println("Header: 1011");
	        System.out.println("Type (expected = 6): " + data[4]);
	        System.out.println("elevatorId: " + data[5] + data[6] + data[7] + data[8]);
		}else {
			System.out.println("Passing message that elevator " + data[5] + data[6] + data[7] + data[8] + " should close its door");
		}
        return receivePacket;
	}
	
	/**
	 * Expected format: Total 9 bytes
	 * byte[0-3]: header
	 * byte[4]: type byte
	 * byte[5-8]: elevator id (int)
	 * 
	 * @param input data
	 * @return false if the input data was invalid
	 */
	private boolean handleData(byte[] input) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(input);
		//Expected to start with header and a type byte.
		if(input.length != 9 || buffer.getInt() != Constants.HEADER || buffer.get() != (byte) 6) {
			return false;
		}
		
		sendDoorCloseMessage(buffer.getInt());
		
		// Update type bit for the forwarding request
		input[4] = (byte) 7;
		
		forwardData(input);
		
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
			System.exit(1);
		}
		DatagramPacket response = new DatagramPacket(
				new byte[] {1,0,1,1,(byte) (success ? 0 : 1)},
				5,
				received.getAddress(),
				received.getPort());
		if(Constants.debug) {
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
			System.out.println("DoorIntermediateHost: Sending response:");
	        System.out.println("To host: " + response.getAddress());
	        System.out.println("Host port: " + response.getPort());
	        System.out.println("Length: " + response.getLength());
	        System.out.print("Containing: " );
	        System.out.println("Header: 1011");
	        System.out.println("Success: " + success);
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
	 * Send a message to the elevator subsystem notifying that the door must close for a given elevator.
	 * @throws IOException
	 * @return true if response is success
	 */
	private boolean forwardData(byte[] data) throws IOException {
		//Create data to forward to elevator manager
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 5018);
		
		try {
            // Send data to the elevator manager
			socket.send(packet);
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
			System.out.println("Sending Packet to elevator manager");
			// Receive a response from elevator manager
			byte[] response = new byte[5];
			DatagramPacket receivePacket = new DatagramPacket(response, 5);
			socket.receive(receivePacket);
			
			if (response[4] != (byte) 0) {
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
				System.out.println("Received failure response");
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	
	/**
	 * Send a request to the UI to close the doors for the given elevator
	 * 
	 * @param lift id of the elevator
	 */
	private void sendDoorCloseMessage(int lift) {
		ByteBuffer b = ByteBuffer.wrap(new byte[11]);
		b.putInt(Constants.HEADER).put((byte) 10).put((byte) 2).putInt(lift).put((byte) 0);
		
		DatagramPacket packet = null;
		try {
			packet = new DatagramPacket(b.array(), 11, InetAddress.getLocalHost(), 5019);
		} catch (UnknownHostException e) {
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
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
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
				System.out.println("Encountered error response when updating UI.\n");
			} catch (IOException e) {
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 4 (DoorIntermediateHost): ");
				System.out.println("Encountered timeout with socket, retrying.\n");
			}
		}
	}
}
