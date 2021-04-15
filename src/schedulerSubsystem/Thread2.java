package schedulerSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import sharedObjects.Constants;
import sharedObjects.Direction;
import sharedObjects.ElevatorRequest;
import sharedObjects.Pair;
import sharedObjects.SchedulerFloorRequestBox;


/**
 * Class takes requests received from the floor manager
 * and selects elevators to chose
 * 
 * @author Edmond Chow
 *
 */
public class Thread2 implements Runnable {
	private ElevatorData map;
	private SchedulerFloorRequestBox FS_channel;//FileLoader Scheduler box here
	private ElevatorRequest request;
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	private byte [] header = {1,0,1,1};
	private int count = 0;
	private long start, time;
    
	/**
	 * Create a scheduler connected to a shared memory space for both
	 * floor subsystem and elevator subsystem
	 * @param fschannel floor scheduler shared object
	 * @param elevators number of elevators in the system
	 * @param map reference to map of elevator locations
	 */
	public Thread2(ElevatorData map, SchedulerFloorRequestBox fschannels) {
		FS_channel = fschannels;//assign FileLoader Request Box to channel
		this.map = map;
		try {
			// Construct a datagram socket and bind it to any available 
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
			sendReceiveSocket.setSoTimeout(Constants.TIMEOUT_MILLIS);
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
 	* Send job packet to elevator manager (on port 5015)
 	* @param elevator elevator job is being sent to
 	*/
	public void sendJob(int elevator) {
		byte [] msg = createBytes(elevator);
		try {
				sendPacket = new DatagramPacket(msg, msg.length,  InetAddress.getLocalHost(), 5015);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
		if (Constants.debug) {
			System.out.println("Destination host port: " + sendPacket.getPort());
			int len = sendPacket.getLength();
			System.out.println("Length: " + len);
		}
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (Constants.debug) {
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
			System.out.println("Packet sent.\n");	
		}
	}
	
	/**
	 * Create a packet to send to the elevator manager
	 * @param elevator elevator packet will be sent to
	 * @return byte [] contents of packet to send
	 */
	public byte[] createBytes(int elevator){
    	/*
    	 * method sending type 2 packet of format
    	 * 1011 2 EEEE D FFFF CCCC
    	 */
    	ByteBuffer BB = ByteBuffer.allocate(23);
    	BB.put(header); //header used as checksum
    	BB.put((byte) 2); //type of packet bit
    	BB.putInt(elevator);
		BB.put(request.getDirection().getValue());
		BB.putInt(request.getFloor()); // requester floor
    	BB.putInt(request.getCarButton()); //destination floor
    	BB.put(request.getError().getValue());
    	BB.putInt(request.getErrorFloor());
    	System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
    	if(Constants.debug) {
    		System.out.println("Sending job to Elevator Manager:");
    		System.out.println("Containing Data:");
    		System.out.println("Target Elevator: " + elevator);
    		System.out.println("Requestee FileLoader: " + request.getFloor());
    		System.out.println("Destination FileLoader: " + request.getCarButton());
    		System.out.print("With Error type: " + request.getError());
    		System.out.println(" on floor " + request.getErrorFloor());
    	}else {
    		System.out.println("Sending job to Elevator " + elevator + " go from " 
    					+ request.getFloor() + " to " + + request.getCarButton()+"\n");
    	}
    	
    		
 	    return BB.array();
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
        if(Constants.debug) {
        	System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
        	System.out.println("Validation Packet received:");
	        //expand for other type of errors
	        if (response==0) {
	        	System.out.println("Packet was validated");
	        	return true;} 
	        else {
	        	System.out.println("Packet was not validated");
	    		return false;}
        }else {
        	if (response==0) {
	        	return true;} 
	        else {
	    		return false;}
        }
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
	 * Calculate the current distance between elevator in question and requesting floor
	 * @param eleLoc Pair indicating location and direction of a elevator
	 * @return distance between elevator and floor, if directions don't match returns -1
	 */
	public int calculateDistance(Pair<Integer,Direction> eleLoc) {
		int start = request.getFloor();
		Direction dirReq = request.getDirection();
		Direction dirEle =(Direction) eleLoc.getV();
		int eleFloor =(int) eleLoc.getT();
		/*
		 * Elevator can not handle multi job allotments yet
		 */
		switch(dirEle) {//calculate distance based on which way the elevator is moving
			case UP: //if elevator is moving up or down, make sure it matches the direction of request
				if(dirReq==dirEle) {
					return ((start-eleFloor)>=0)?(start-eleFloor+10):-1;
				}else {//if elevator movement != direction of request, do not chose elevator
					return -1;
				}
			case DOWN:
				if(dirReq==dirEle) {
					return ((eleFloor-start)>=0)?(eleFloor-start+10):-1;
				}else {//if elevator movement != direction of request, do not chose elevator
					return -1;
				}
			case STATIONARY://if elevator is stationary get absolute value of distance
				return 0;
			default:
				return -1;
		}
	}
	
	/**
	 * Look at current location of elevators and select which elevator is best for the job
	 * @return int indicating elevator to send job to, -1 if no suitable elevator found
	 */
	public int chooseElevator() {
		Pair<Integer,Direction> eleLoc;
		HashMap<Integer, Pair<Integer, Direction>> mapClone = map.getElevatorsData(); //get map of up to date elevator locations
		int distance;
		int minDist=9999;
		int choice = -1;
		//iterate over all elevators in the system
		for(int i = 1;i<=Constants.elevator;i++) {
			eleLoc = mapClone.get(i);//get elevators current system
			distance =calculateDistance(eleLoc);
			//System.out.println("Elevator " + i + " is " + distance + " away from next job");
			//if distance is optimal
			if(distance >=0 && distance <minDist) {
				choice = i;
				minDist = distance;
			}
		}
		
		return choice;
	}
	
	/**
	 * Run method
	 */
	public void run(){
		while(true) {
			try {
				//System.out.println("Scheduler Thread2:Getting request..");
				request = FS_channel.getRequest(count);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			start = System.nanoTime();
			//System.out.println("Scheduler Thread2: Selecting Elevator to allocate job");
			int lift = chooseElevator();
			time = System.nanoTime()-start;
			
			
			if (lift < 0) { //if no suitable elevator found increment count and loop, checking next request
				//System.out.println("Scheduler Thread2:No Elevator found proceeding to evaluate next job");
				count = (count+1)%FS_channel.getSize();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
			System.out.println("Elevator Selection time: " + time);
			// if elevator found, reset
			start = System.nanoTime();
			FS_channel.remove(count);
			count = 0;
			map.updateElevatorLocation(lift, map.getElevatorData(lift).getT(), request.getDirection());
			time = System.nanoTime()-start;
			System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
			System.out.println("Elevator Map update time: " + time);
			boolean acknowledged = false;
			//if acknowledgement failed, resend packet
			while(!acknowledged) {
				start = System.nanoTime();
				sendJob(lift);
				time = System.nanoTime()-start;
				System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
				System.out.println("Packet Send Time: " + time);
				try {
					start = System.nanoTime();
					acknowledged = receiveResponse();
					time = System.nanoTime()-start;
					System.out.print("Time: " + System.currentTimeMillis() + ": Scheduler Thread 2: ");
					System.out.println("Packet Acknowledgement time Time: " + time);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
