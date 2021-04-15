package elevatorSubsystem;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.lang.String;
import sharedObjects.*;
import sharedObjects.Error;

/**
 * This thread is responsible for taking the packets sent by scheduler thread 2
 * and making sure it is sent to the proper elevator
 * @author Asifur Rahman, Edmond Chow
 */
public class ElevatorThread2 implements Runnable{
    private SchedulerElevatorBox elevatorBox;
    private byte[] get_response; // Bytes received from Thread 2
    private DatagramPacket sendPacketThreadTwo, receivePacketThreadTwo; // thread 2
	private DatagramSocket sendSocketThreadTwo, receiveSocketThreadTwo; // thread 2
	private byte[] success = {1, 0, 1, 1, 0}; // if the message is validated send back 0 to Thread 2
	private byte[] failure = {1, 0, 1, 1, 6}; // if the message is not validated send back 6 to Thread 2

    /**
	 * Default constructor initializing instance variables
	 * @param box space where we communicate with the elevator
	 */
    public ElevatorThread2(SchedulerElevatorBox box) {
		this.elevatorBox = box;
		
		try {
			receiveSocketThreadTwo = new DatagramSocket(5015);
		} catch (SocketException e){
			e.printStackTrace();
			System.exit(1);
		}
	}

    /**
	 * method to run the receive thread to receive information from thread2
	 */
	private void receive_thread_two() {
		while (true) {
			if(Constants.debug) {
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Listening for Thread 2 for info!");
			}
			// Constructing DatagramPacket to receive data
			byte data[] = new byte[100];
			receivePacketThreadTwo = new DatagramPacket(data, data.length);

			// Wait till DatagramPacket is received in sendReceiveSocket
			try {
				receiveSocketThreadTwo.receive(receivePacketThreadTwo);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			get_response = receivePacketThreadTwo.getData();
			int len = receivePacketThreadTwo.getLength();

			// Check if the packet received is valid
			if (validate(get_response)) {
				if(Constants.debug) {
					System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "The received packet is valid.");
				}
			} else {
				if(Constants.debug) {
					System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "The received packet is invalid");
					System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "string: " + new String(data, 0, len));
					System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "bytes: " + Arrays.toString(get_response));
				}
				send_thread_two(failure);
				continue;
			}

			ByteBuffer byteBuffer = ByteBuffer.wrap(get_response);
			byteBuffer.getInt(); // to pass through 1011
			byteBuffer.get(); // to pass through type T
			//get information in packet
			int eId = byteBuffer.getInt();
			Direction d = Direction.getDirection(byteBuffer.get());
			int reqFloor = byteBuffer.getInt();
			int destFloor = byteBuffer.getInt();
			Error err = Error.getError(byteBuffer.get());
			int errFloor = byteBuffer.getInt();
			if(Constants.debug) {//print out details of packet in debug mode
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Packet received from Thread 2 for processing:");		
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Containing Data:");
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Elevator ID: " + eId);
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Direction: " + d);
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Requested Floor: " + reqFloor);
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Destination Floor: " + destFloor);
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Error Type: " + err);
				System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Error Floor Num: " + errFloor);
				System.out.print("\n");
			}
			send_thread_two(success);

			// RequestHandler(elevator id, direction, reqFloor, destFloor, open/close, errorType, errorFloor)
			RequestHandler tempReq = new RequestHandler(eId, d, reqFloor, destFloor, 0, err, errFloor);
			
			// sending request to elevator
			elevatorBox.placeRequest(tempReq, eId);
		}
	}

	/**
	 * sending success or failure message to thread2 and receiving a response back
	 * @param msg message to send to the thread
	 */
	private void send_thread_two(byte[] msg) {
		try {
			sendSocketThreadTwo = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			sendPacketThreadTwo = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), receivePacketThreadTwo.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
		System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Elevator Manager [to Thread 2]: Sending Validation packet:");
		}

		// Send DatagramPacket
		try {
			sendSocketThreadTwo.send(sendPacketThreadTwo);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
		System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread2: " + "Elevator Manager: Validation Packet sent.");
		}
		sendSocketThreadTwo.close();
	}

    /**
	 * method to validate the data
	 * @param data byte[] to validate
	 * @return true or false when it meets the condition
	 */
	private boolean validate(byte[] data) {
		if ((data[0] == (byte) 1 && data[1] == (byte) 0 && data[2] == (byte) 1 && data[3] == (byte) 1) && data[4] == (byte) 2) {
			return true;
		}
		return false;
	}

	/**
	 * run method
	 */
    public void run() {
		while(true){
			receive_thread_two();
		}
    }
}