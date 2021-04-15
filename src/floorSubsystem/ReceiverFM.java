package floorSubsystem;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import sharedObjects.FloorRequestBox;
import sharedObjects.Constants;
import sharedObjects.Error;

/**
 * This receiverFM constantly listens for UDP requests to open doors then notifies the floor
 * to handle this open door request.
 * 
 * @author Jyotsna Mahesh, Md Aiman Sharif
 */
public class ReceiverFM implements Runnable {
    private static byte[] getResponse; //Bytes received from Thread three
    private static byte[] floor = new byte[4];
    private static byte[] elevator = new byte[4];
    private static byte[] vrMessage = {1,0,1,1,0}; //if the message is validated send back 0 to Thread 3
    private static byte[] vwMessage = {1,0,1,1,1};// if the message is not validated send back 1 to Thread 3
    private static DatagramPacket rttPacket, sttPacket; //thread3
    private static DatagramSocket sgttSocket, rgttSocket; //thread3
    private FloorRequestBox FS_channel; //get request from floor request box
    
    /**
     * Default constructor initializing instance variables
     * @param box FloorRequestBox passed in as a parameter
     * @throws InterruptedException
     */
    public ReceiverFM(FloorRequestBox box) throws InterruptedException{
        this.FS_channel = box;
        try {
            rgttSocket = new DatagramSocket(5011);
        }
        catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    

	/**
	 * Method to receive data from thread three
	 */
    private void recieveThreadThree() {
        while (true) {
        	if(Constants.debug) {
        		System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread: Waiting for response from thread 3");
        	}
            // Constructing DatagramPacket to receive data
            byte data[] = new byte[100];
            rttPacket = new DatagramPacket(data, data.length);

            // Wait till DatagramPacket is received in sendReceiveSocket
            try {
                rgttSocket.receive(rttPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            getResponse = rttPacket.getData();
            
            // Check if the packet received is valid
            if (validate(getResponse)) {
            	if(Constants.debug) {
            		System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread: Packet from thread 3 is valid.");
            	}
            } else {
            	if(Constants.debug) {
            		System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread: Packet from thread 3 is invalid");
            		System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread: Sending response to Thread3");
            	}
                sendThreadThree(vwMessage);
                System.exit(1);
            }
            
            System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread: Packet received from Thread 3:");

            sendThreadThree(vrMessage);
            
            //1011 T EEEE FFFF e ffff
            for (int i = 0; i < 4; i++ ) {
                elevator[i] = getResponse[i+5];
            }
            int elevnum = 0;
            elevnum = bytesToInt(elevator);
            
            for (int i = 0; i < 4; i++ ) {
                floor[i] = getResponse[i+9];
            }
            int floornum = 0;
            floornum = bytesToInt(floor);
            
            Error errnum = Error.NONE;
            errnum = Error.getError(getResponse[13]);
            if(Constants.debug) {
				System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread : Sending Response to FloorResponse");
				System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread : Containing Data:");
				System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread : FileLoader Num:" + floornum);
				System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread : Elevator Num: " + elevnum);
				System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread : Error Type: " + errnum);
            }
            
            FS_channel.putResponse(floornum, elevnum, errnum);
        }

    }

    /**
     * Method to validate bytes
     * @param data entered as byte array
     * @return true if validated, false if not validated
     */
    private boolean validate(byte[] data) {
        boolean check1 = false, check2 = false;// checks
        // Check if first 4 elements are 1011
        if (data[0] == (byte) 1 && data[1] == (byte) 0 && data[2] == (byte) 1 && data[3] == (byte) 1) {
            check1 = true;
        }
        // Check if request type is 4
        if (data[4] == (byte) 4 ) {
            check2 = true;
        }

        return (check1 && check2);
    }

    /**
     * Method to send response to thread three
     * @param message to be passed in as a parameter of bytes
     */
    private void sendThreadThree(byte[] message) {
        try {
            sgttSocket = new DatagramSocket();
        }
        catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            sttPacket = new DatagramPacket(message, message.length, InetAddress.getLocalHost(), rttPacket.getPort());
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if(Constants.debug) {
        	System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread: Sending Response Packet to Thread3 after validation");
        }
        
        // Send DatagramPacket
        try {
            sgttSocket.send(sttPacket);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if(Constants.debug) {
        	System.out.println("Time:" + System.currentTimeMillis() + ": ReceiverFM Thread: Packet sent.\n");
        }
        sgttSocket.close();
    }

    /**
     * Method for converting bytes to int
     * @param arr passed in as a byte array
     * @return num converted bytes to int
     */
    private int bytesToInt( byte[] arr ) {
        ByteBuffer bb = ByteBuffer.wrap(arr);
        int num = bb.getInt();
        return num;
    }

    /**
     * Run method
     */
    public void run() {
        while(true) {
            recieveThreadThree();
        }
    }
}
