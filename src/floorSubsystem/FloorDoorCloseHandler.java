package floorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import sharedObjects.Constants;
import sharedObjects.FloorRequestBox;

/**
 * This class will be a thread which tracks doors closing and notify 
 * the scheduler whenever the box contains a door close event.
 * These will be sent to the scheduler to update elevators to resume as well
 * as update the UI with status of the elevators.
 * 
 * @author Jyotsna Mahesh, Md Aiman Sharif
 */
public class FloorDoorCloseHandler implements Runnable {
	private static ArrayList<Byte> sendRequest; //Bytes from elevator response to be sent to Scheduler
    private static DatagramPacket stoPacket, rPacket; //, rtoPacket;thread4
    private static DatagramSocket srtoSocket; //thread4
    private int doorResponse;
    private FloorRequestBox FS_channel; //get request from floor request box
    private static int threadFourPort = 5017;

    /**
     * Default constructor initializing instance variables
     * @throws InterruptedException
     */
    
    public FloorDoorCloseHandler(FloorRequestBox box) {
    	this.FS_channel = box;
    }
    
    /**
     * Adding data to Arraylist of bytes to be sent as a packet to thread4
     */
    public void sendData() {
    	doorResponse = FS_channel.getDoorCloseResponse();

        // Formatting send_request
        sendRequest = new ArrayList<Byte>();
        //Bytes to verify request
        sendRequest.add((byte) 1);
        sendRequest.add((byte) 0);
        sendRequest.add((byte) 1);
        sendRequest.add((byte) 1);

        //Byte representing request , from FM to Thread4
        sendRequest.add((byte) 6);

        byte[] x = intToBytes(doorResponse);
        for (byte b : x){
            sendRequest.add(b);
        }

    }
    
    /**
     * Method to send and receive data to and from Thread4
     * @param msg being passed in as a parameter
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
                    threadFourPort);
        } catch (UnknownHostException e) { // If sendPacket is invalid
            e.printStackTrace();
            System.exit(1);
        }
        if(Constants.debug) {
    
        	System.out.println("Time: " + System.currentTimeMillis() + ": FloorDoorCloseHandler Thread: Sending Packet to Thread 4 with Elevator Num:" + doorResponse);

        }
        try {
            srtoSocket.send(stoPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
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
        	System.out.println("Time: " + System.currentTimeMillis() + ": FloorDoorCloseHandler Thread: Validation Response Packet received from Thread 4");

        }
        if(data[4] != (byte)0) {
			System.out.println("Time: " + System.currentTimeMillis() + ": FloorDoorCloseHandler Thread: Received failure response");
		}
        
        srtoSocket.close(); // Closing socket
    }
    
    /**
     * Method for sending and receiving packet
     */
    public void sendReceivePacket() {
    	sendData();
        sendReceiveThreadOne(sendRequest);
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
     * Gets responses from floor response and sends out
     */
    public void run() {
    	while(true) {
    		sendReceivePacket();
    	}
    }
}
