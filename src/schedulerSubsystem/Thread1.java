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
import sharedObjects.ElevatorRequest;
import sharedObjects.Error;
import sharedObjects.SchedulerFloorRequestBox;

/**
 * Thread 1 of the scheduler takes the job packets from the floor system,
 * parses it, and then passes the info into a shared memory space for the
 * scheduler thread 2 to process
 * @author Jyotsna Mahesh, Edmond Chow, Md Aiman Sharif
 *
 */
public class Thread1 implements Runnable{
	private static DatagramSocket rSocket, sgfmSocket; //sSocket,
	private static DatagramPacket rPacket, sfmPacket; // sPacket, 
	private static byte[] rMessage; //stores data from SenderFM
	private static byte[] reqf; 
	private static byte[] reqc;
	private static byte[] reqErr = new byte [4];
	private static byte[] vrMessage = {1,0,1,1,0}; //if the message is validated send back 0 to Thread 3
    private static byte[] vwMessage = {1,0,1,1,1};// if the message is not validated send back 1 to Thread 3
	private final SchedulerFloorRequestBox REQUEST_BOX;
	private ElevatorRequest request1;
	
	/**
	 * Default constructor initializing instance variables
	 * @param requestBox instance of SchedulerFloorRequestBox passed in
	 */
	public Thread1(SchedulerFloorRequestBox requestBox) {
		this.REQUEST_BOX = requestBox;
		reqf = new byte[]{0, 0, 0, 0};
		reqc = new byte[]{0, 0, 0, 0};
		
		try {
			rSocket = new DatagramSocket(5012);
		} 
		catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Method to send and receive DatagramPacket from SenderFM
	 */
	public void sendReceive() {
		while (true) {
			// Constructing DatagramPacket to receive data
			byte data[] = new byte[100];
			rPacket = new DatagramPacket(data, data.length);

			// Wait till DatagramPacket is received in sendReceiveSocket
			try {
				rSocket.receive(rPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			rMessage = rPacket.getData();
			
			// Check if the packet received is valid
			if (validate(rMessage)) {
				if(Constants.debug) {
					System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 1: ");
					System.out.println("The received packet from floor manager is valid.");
				}
			} else {
				if(Constants.debug) {
					System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 1: ");
					System.out.println("The received packet from floor manager is invalid");
					System.out.println("Sending response to Sender FM");
				}
				sendSenderFM(vwMessage);
				System.exit(1);
			}

			// Decoding and printing received message
			if(Constants.debug) {
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 1: ");
				System.out.println("Packet received from Floor Manager:");
				System.out.print("\n");
			}
			//Send verification message to Floor subsystem
			sendSenderFM(vrMessage);
			
			//get requester floor
			for (int i = 0; i < 4; i++) {
				reqf[i] = rMessage[i + 5];
			}
			int floornum = 0;
			floornum = bytesToInt(reqf);
			
			//get destination floor
			for (int i = 0; i < 4; i++ ) {
				reqc[i] = rMessage[i+10];
			}
			int carButton = 0;
			carButton = bytesToInt(reqc);
			//get direction of request
			Direction direction = Direction.getDirection(rMessage[9]);
			//get error type
			Error err = Error.getError(rMessage[14]);
			//get error floor
			for (int i = 0; i < 4; i++) {
				reqErr[i] = rMessage[i + 15];
			}
			int errFloor =0;
			errFloor = bytesToInt(reqErr);
			//if debug print out packet details
			if(Constants.debug) {
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 1: ");
				System.out.println("Containing Data: ");
				System.out.println("Current FileLoader Num: " + floornum);
				System.out.println("Direction: " + direction);
				System.out.println("Car Button number: " + carButton);
				System.out.println("Error Type: " + err);
				System.out.println("Error FileLoader Num: " + errFloor);
				System.out.print("\n");
			}
			//Send request to Scheduler thread 2
			ElevatorRequest request = new ElevatorRequest(floornum, carButton, direction, err, errFloor);
			request1 = request;
			if(Constants.debug) {
				System.out.println("Sending Unpacked Request to Thread 2");
			}
			REQUEST_BOX.addRequest(request1);
		}
	}
	
	/**
     * Sends response message to SenderFM
     * 
     * @param message to be passed in as a parameter of bytes
     */
    private void sendSenderFM(byte[] message) {
        try {
            sgfmSocket = new DatagramSocket();
        }
        catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            sfmPacket = new DatagramPacket(message, message.length, InetAddress.getLocalHost(), rPacket.getPort());
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if(Constants.debug) {
        	System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 1: ");
        	System.out.println("Sending Response Packet to SenderFM after validation");
        }
        // Send DatagramPacket
        try {
            sgfmSocket.send(sfmPacket);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if(Constants.debug) {
        	System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 1: ");
        	System.out.println("Packet sent.\n");
        }
        sgfmSocket.close();
    }
	
	/**
	 * Method to validate the data
	 * @param data passed in as an array
	 * @return true if validated, false if not validated
	 */
	private boolean validate(byte[] data) {
		boolean check1 = false, check2 = false;// checks
		// Check if first 4 elements are 1011
		if (data[0] == (byte) 1 && data[1] == (byte) 0 && data[2] == (byte) 1 && data[3] == (byte) 1) {
			check1 = true;
		}
		// Check if request type is 1
		if (data[4] == (byte) 1 ) {
			check2 = true;
		}
		
		return (check1 && check2);
	}
	
	
	/**
	 * Method to convert bytes to int
	 * @param arr passed in as a parameter
	 * @return num converted to int from bytes
	 */
	private int bytesToInt( byte[] arr ) {
	    ByteBuffer bb = ByteBuffer.wrap(arr); 
	    int num = bb.getInt();
	    return num;
	}

	/**
	 * Run method
	 */
	@Override
	public void run() {
		sendReceive();
	}
}