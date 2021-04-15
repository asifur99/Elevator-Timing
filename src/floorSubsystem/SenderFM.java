package floorSubsystem;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

import sharedObjects.Constants;
import sharedObjects.ElevatorRequest;
import sharedObjects.FloorRequestBox;

/**
 * The SenderFM listens to a box of requests to be loaded to the scheduler.
 * It will load these requests into the scheduler in the order that they are received.
 * 
 * @author Jyotsna Mahesh, Md Aiman Sharif
 */
public class SenderFM implements Runnable{
    private static ArrayList<Byte> sendRequest; //Bytes from request to be sent to Request box
    private static DatagramPacket stoPacket, rPacket; //thread1
    private static DatagramSocket srtoSocket; //thread1
    private ElevatorRequest request;
    private FloorRequestBox FS_channel; //get request from floor request box
    private static int threadOnePort = 5012;

    /**
     * Default constructor initializing instance variables
     * @throws InterruptedException
     */
    public SenderFM(FloorRequestBox box) throws InterruptedException{
        this.FS_channel = box;
    }

    /**
     * This method adds data into an ArrayList of bytes to be sent as a packet to Thread 1
     */
    public void sendData() {
        request = FS_channel.getRequest();

        // Formatting send_request
        sendRequest = new ArrayList<Byte>();
        //Bytes to verify request
        sendRequest.add((byte) 1);
        sendRequest.add((byte) 0);
        sendRequest.add((byte) 1);
        sendRequest.add((byte) 1);

        //Byte representing request , from FM to Thread1
        sendRequest.add((byte) 1);

        byte[] x = intToBytes(request.getFloor());
        for (byte b : x){
            sendRequest.add(b);
        }

        sendRequest.add(request.getDirection().getValue());

        byte[] y = intToBytes(request.getCarButton());
        for (byte b : y) {
            sendRequest.add(b);
        }
        
        sendRequest.add(request.getError().getValue());
        
        byte[] z = intToBytes(request.getErrorFloor());
        for (byte b : z) {
            sendRequest.add(b);
        }
        
    }

    /**
     * Method for sending and receiving packets
     */
    public void sendReceivePacket() {
        sendReceiveThreadOne(sendRequest);
    }
    
    /**
	 * Print the data when the datagram packet is sent
	 */
	public void printDatagramPacketSent() {
		System.out.println("Time:" + System.currentTimeMillis() + "SenderFM Thread : Packet Sent to Thread 1 for processing");	
		// String
		System.out.println("Time:" + System.currentTimeMillis() + ": SenderFM Thread : Containing Data:");
		System.out.println("Time:" + System.currentTimeMillis() + ": SenderFM Thread : Current FileLoader Num: " + request.getFloor());
		System.out.println("Time:" + System.currentTimeMillis() + ": SenderFM Thread : Direction: " + request.getDirection());
		System.out.println("Time:" + System.currentTimeMillis() + ": SenderFM Thread : Car Button number: " + request.getCarButton());
		System.out.println("Time:" + System.currentTimeMillis() + ": SenderFM Thread : Error Type: " + request.getError());
		System.out.println("Time:" + System.currentTimeMillis() + ": SenderFM Thread : Error Floor Num: " + request.getErrorFloor());
		System.out.print("\n");
	}

    /**
     * Method to send and receive data to and from thread 1
     * @param request msg being passed in as a parameter
     */
    private void sendReceiveThreadOne(ArrayList<Byte> msg) {
        try {
            srtoSocket = new DatagramSocket();
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
            stoPacket = new DatagramPacket(buffer_message, buffer_message.length, InetAddress.getLocalHost(),
                    threadOnePort);
        } catch (UnknownHostException e) { // If sendPacket is invalid
            e.printStackTrace();
            System.exit(1);
        }
        
        // Print datagram packet sent data
        if(Constants.debug) {
        	printDatagramPacketSent();
        }
        try {
            srtoSocket.send(stoPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if(Constants.debug) {
        	System.out.println("Time:" + System.currentTimeMillis() + "SenderFM Thread : Packet sent to Thread 1.\n");
        }

     // Constructing DatagramPacket to receive data
        byte data[] = new byte[100];
        rPacket = new DatagramPacket(data, data.length);

        // Wait till DatagramPacket is received
        try {
            srtoSocket.receive(rPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        // Decoding Packet received
        if(Constants.debug) {
        	System.out.println("Time:" + System.currentTimeMillis() + "SenderFM Thread : Validation Response Packet received from Thread1:");
        	System.out.print("\n");
    

	        if(data[4] != (byte)0) {
				System.out.println("Time:" + System.currentTimeMillis() + "SenderFM Thread : Received failure response");
			}
        }
        srtoSocket.close(); // Closing socket
        
    }

    /**
     * Method for converting integer to bytes
     * @param i integer to be passed in
     * @return byte array
     */
    private byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    /**
     * Run method
     */
    public void run() {
        while(true) {
            sendData(); //adding data to message 
            sendReceivePacket();
        }
    }
}

