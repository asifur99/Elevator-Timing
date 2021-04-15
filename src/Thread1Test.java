
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import schedulerSubsystem.Thread1;
import sharedObjects.Direction;
import sharedObjects.ElevatorRequest;
import sharedObjects.SchedulerFloorRequestBox;
/**
 * Test Thread1 capability to add packets to intermediary queue
 * 
 * @author Edmond Chow, Ashton Mohns
 *
 */
class Thread1Test {
	private static DatagramSocket FMsocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static SchedulerFloorRequestBox RB;
	
	/**
	 * Create new instance of thread1,intermediary box, and
	 * socket to send to thread 1 instance
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		RB = new SchedulerFloorRequestBox();
		Thread testthread = new Thread(new Thread1(RB));
		FMsocket = new DatagramSocket();
		testthread.start();
	}
	/**
	 * Send packet to thread 1 and evaluate that the thread 
	 * correctly adds ElevatorRequest object to request queue
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 */
	@Test
	void testAddRequest() throws UnknownHostException, InterruptedException{
		//create packet to send and send it to thread 1
		byte [] test = {1,0,1,1,1,0,0,0,1,1,0,0,0,5,0,0,0,0,0};
		sendpacket = new DatagramPacket(test,test.length,InetAddress.getLocalHost(), 5012) ;
		try {
			FMsocket.send(sendpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//get request from queue and then make sure that request has proper
		//attributes
		ElevatorRequest res = RB.getRequest(0);
		System.out.println(res);
		assert(res.getCarButton()== 5 && res.getFloor() == 1 && res.getDirection() == Direction.UP);
	}
}
