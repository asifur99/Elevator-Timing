package elevatorSubsystem;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import sharedObjects.*;

/**
 * This thread is responsible for taking the movement updates from the elevators
 * and sending it to the scheduler
 * @author Asifur Rahman, Edmond Chow
 */
public class ElevatorThread3 implements Runnable{
    private ArrayList<Byte> sendMessage;
    private SchedulerElevatorBox elevatorBox;
    private RequestHandler elevatorRequest;
    private DatagramPacket receivePacketThreadThree, sendPacketThreadThree; // thread3
	private DatagramSocket sendReceiveSocketThreadThree; // thread3

    /**
	 * Default Constructor to initialize instance variables
	 * @param box space where we communicate with the elevator
	 */
    public ElevatorThread3(SchedulerElevatorBox box) {
		this.elevatorBox = box;
	}

	/**
	 * method to send data to thread 3
	 * @throws InterruptedException
	 */
    public void sendData() throws InterruptedException {

		//get elevator responses
		elevatorRequest = elevatorBox.getMovementUpdate();

        // Formatting sendMessage		
		sendMessage = new ArrayList<Byte>();

        // Bytes to verify request
		sendMessage.add((byte) 1);
		sendMessage.add((byte) 0);
		sendMessage.add((byte) 1);
		sendMessage.add((byte) 1);

		// Byte representing request
		sendMessage.add((byte) 3);

		byte[] elevatorID = intToBytes(elevatorRequest.getElevatorId());
		for (byte b : elevatorID) {
			sendMessage.add(b);
		}
		
		sendMessage.add(elevatorRequest.getDirection().getValue());

		byte[] currFloor = intToBytes(elevatorRequest.getReqFloor());
		for (byte b : currFloor) {
			sendMessage.add(b);
		}

		sendMessage.add((byte) elevatorRequest.status());

		// Adding errorType byte to the packet
		sendMessage.add((byte) elevatorRequest.getErrorType().getValue());

		// Adding errorFloor byte to the packet
		byte[] errorFloor = intToBytes(elevatorRequest.getErrorFloor());
		for (byte b : errorFloor) {
			sendMessage.add(b);
		}
		sendMessage.add(elevatorRequest.getActual().getValue());
		send_receive_thread_three(sendMessage);
	}

	/**
	 * send message to thread three and get a response from it
	 * @param msg message to send to the thread
	 */
	private void send_receive_thread_three(ArrayList<Byte> msg) {
		try {
			sendReceiveSocketThreadThree = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}

		byte[] buffer_message = new byte[msg.size()];
		for (int i = 0; i < msg.size(); i++) {
			buffer_message[i] = msg.get(i);
		}

		// Encoding data into DatagramPacket which is sent
		try {
			sendPacketThreadThree = new DatagramPacket(buffer_message, buffer_message.length, InetAddress.getLocalHost(), 5014);
		} catch (UnknownHostException e) { // If sendPacket is invalid
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Sending packet:");
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Containing Data:");
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Elevator ID: " + elevatorRequest.getElevatorId());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Direction: " + elevatorRequest.getDirection());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Current Floor: " + elevatorRequest.getReqFloor());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Destination Floor: " + elevatorRequest.getDestFloor());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Error Type: " + elevatorRequest.getErrorType());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Error Floor Num: " + elevatorRequest.getErrorFloor());
			System.out.print("\n");
		}

		// Send sendPacket to server using sendRecieveSocket
		try {
			sendReceiveSocketThreadThree.send(sendPacketThreadThree);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + "Packet sent.");
		}
		// Constructing DatagramPacket to receive data
		byte data[] = new byte[100];
		receivePacketThreadThree = new DatagramPacket(data, data.length);

		// Wait till DatagramPacket is received in sendReceiveSocket
		try {
			sendReceiveSocketThreadThree.receive(receivePacketThreadThree);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread3: " + ": Response packet received from Thread3:");
		}

		sendReceiveSocketThreadThree.close(); // Closing socket
	}

	/**
	 * Converts an array of int to bytes
	 * @param i integer to input
	 * @return a byte array
	 */
	private byte[] intToBytes(final int i) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(i);
		return bb.array();
	}
	
	/**
	 * Run method
	 */
    public void run() {
        while(true){
			try {
				sendData();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
}