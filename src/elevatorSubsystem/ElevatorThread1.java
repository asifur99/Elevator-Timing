package elevatorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import sharedObjects.Constants;
import sharedObjects.ElevatorRequest;

/**
 * Thread is responsible for resending jobs that can no longer be completed 
 * by assigned elevator to scheduler system
 * @author Edmond Chow
 *
 */
public class ElevatorThread1 implements Runnable{
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
    private ElevatorRequest request;
    private FailedJob reSend; //get request from floor request box
    private byte [] header = {1,0,1,1};
    
    public ElevatorThread1(FailedJob rs) {
    	reSend = rs;
    	try {
			sendReceiveSocket = new DatagramSocket();
			sendReceiveSocket.setSoTimeout(Constants.TIMEOUT_MILLIS);
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
    }
    /**
     * Create type one packet to be resent to the scheduler
     * @return data of type 1 packet
     */
    public byte[] createBytes(){
    	/*
    	 * format of packet 
    	 * [1,0,1,1] packet header
    	 * [1] type of packet, one in this case
    	 * [ffff], requestee floor
    	 * [D] Direction of job
    	 * [cccc], destination floor
    	 * [E], Type of Error
    	 * [eeee], floor error should happen on
    	 */
    	ByteBuffer BB = ByteBuffer.allocate(19);
    	BB.put(header); //header used as checksum
    	BB.put((byte) 1); //type of packet bit
    	BB.putInt(request.getFloor());
		BB.put(request.getDirection().getValue());
		BB.putInt(request.getCarButton()); // requester floor
    	BB.put(request.getError().getValue());
    	BB.putInt(request.getErrorFloor());
    		
 	    return BB.array();
    }
    
    public void sendRequest() {
		byte [] msg = createBytes();
		try {
				sendPacket = new DatagramPacket(msg, msg.length,  InetAddress.getLocalHost(), 5012);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(Constants.debug) {
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " +"Sending failed job to Scheduler:");
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " +"Containing Data:");
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " +"Current Floor Num: " + request.getFloor());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " +"Direction: " + request.getDirection());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " +"Car Button number: " + request.getCarButton());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " +"Error Type: " + request.getError());
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " +"Error Floor Num: " + request.getErrorFloor());
			System.out.print("\n");
		} else {
			System.out.println("Time: " + System.currentTimeMillis() + ": " +  "Elevator_Thread1: " + "Sending failed job to Scheduler: Floor:" + request.getFloor() + " to Destination: " + request.getCarButton());
		}
		
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
    
    /**
	 * Get response packet from socket and process data
	 * @return true if packet is good, false otherwise
	 * @throws IOException
	 */
	public boolean receiveResponse() throws IOException{
		byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);
        
        try {
           // Block until a datagram is received via sendReceiveSocket.  
           sendReceiveSocket.receive(receivePacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }
        int response = handleData(receivePacket.getData());
        //expand for other type of errors
        if (response==0) {return true;} else {return false;}
	}
	
	/**
	 * Processes the data of an incoming packet by type byte in packet
	 * @param input data of incoming packet
	 * @return an int indicating the status of the packet
	 * @throws IOException
	 */
	private int handleData(byte[] input) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(input);
		//if data dosen't start with preagreed header, fail packet
		if( buffer.getInt() != Constants.HEADER) {
			return 1;
		}
		//expand to handle different type of responses
		switch(buffer.get()) {
			case (byte) 0:
				return 0;
			default:
				return 1;
		}
	}
	
	/**
	 * Run method
	 */
	public void run(){
		while(true) {
			try {
				request = reSend.takeRequest();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendRequest();
			try {
				receiveResponse();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
