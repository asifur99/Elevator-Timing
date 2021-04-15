package elevatorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import sharedObjects.Constants;

/**
 * This Thread takes door close notifications from scheduler thread 4 and
 * makes sure the information is sent to the appropriate elevator
 * @author Asifur Rahman, Edmond Chow
 */
public class ElevatorThread4 implements Runnable{
    private DoorBox doorBox;
    private DatagramSocket receiveSocketThreadFour, sendSocketThreadFour;
    private DatagramPacket receivePacketThreadFour, sendPacketThreadFour;
    private byte[] get_response;

    private byte[] success = {1, 0, 1, 1, 0}; // if the message is validated send back 0 to Thread 4
	private byte[] failure = {1, 0, 1, 1, 6}; // if the message is not validated send back 6 to Thread 4

    /**
	 * Default constructor to initialize instance variables
	 * @param box to connect with the elevator to open the door or not
	 */
    public ElevatorThread4(DoorBox box) {
		this.doorBox = box;

		try {
			receiveSocketThreadFour = new DatagramSocket(5018);
		} catch (SocketException e){
			e.printStackTrace();
			System.exit(1);
		}
	}

     /**
	 * method to run to receive information from thread 4
	 */
	private void receive_thread_four() {
		while (true) {
			if(Constants.debug) {
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"Listening to Thread 4 for info!");
			}

			// Constructing DatagramPacket to receive data
			byte data[] = new byte[100];
			receivePacketThreadFour = new DatagramPacket(data, data.length);

			// Wait till DatagramPacket is received in sendReceiveSocket
			try {
				receiveSocketThreadFour.receive(receivePacketThreadFour);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			get_response = receivePacketThreadFour.getData();

			// Check if the packet received is valid
			if (validate(get_response)) {
				if(Constants.debug) {
					System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"The received packet is valid.");
				}
			} else {
				if(Constants.debug) {
					System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"The received packet is invalid");
				}
				send_thread_four(failure);
				continue;
			}
			//get packet info
			ByteBuffer byteBuffer = ByteBuffer.wrap(get_response);
			byteBuffer.getInt(); // to pass through 1011
			byteBuffer.get(); // to pass through type T
			int eId = byteBuffer.getInt();
			//print out packet details in debug mode
			if(Constants.debug) {
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"Packet received from Thread 4 for processing:");
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"Containing Data:");
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"Elevator ID: " + eId);
			}
			send_thread_four(success);			

			// sending elevatorID to doorbox
			doorBox.placeElevator(eId);
		}
	}

    /**
	 * send 'msg' to thread 4 as response
	 * @param msg message to send to the thread
	 */
	private void send_thread_four(byte[] msg) {
		//socket to send the datagram packet
		try {
			sendSocketThreadFour = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//making packet so we can send the 'msg' to thread4
		try {
			sendPacketThreadFour = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), receivePacketThreadFour.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"Sending validation packet:");
		}

		// Send DatagramPacket
		try {
			sendSocketThreadFour.send(sendPacketThreadFour);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread4: " +"Validation Packet sent.");
		}

		sendSocketThreadFour.close();
	}

    /**
	 * method to validate the data
	 * @param data byte[] to validate
	 * @return true or false when it meets the condition
	 */
	private boolean validate(byte[] data) {
		if ((data[0] == (byte) 1 && data[1] == (byte) 0 && data[2] == (byte) 1 && data[3] == (byte) 1) && data[4] == (byte) 7) {
			return true;
		}
		return false;
	}
    
	/**
	 * method to run the thread
	 */
    public void run() {
		while(true){
			receive_thread_four();
		}
    }
}
