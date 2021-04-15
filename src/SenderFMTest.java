
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import floorSubsystem.SenderFM;
import sharedObjects.Direction;
import sharedObjects.Error;
import sharedObjects.ElevatorRequest;
import sharedObjects.FloorRequestBox;

/**
 * Tests that SenderFM thread will properly send packets containing Elevator request data 
 * to port 5012 in the correct format
 * 
 * @author Edmond Chow, Ashton Mohns
 *
 */
class SenderFMTest {
	private static DatagramSocket SchedulerSocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static FloorRequestBox RB;
	
	/**
	 * set up new instance of SenderFM, box thread pulls requests from
	 * and socket for SenderFM to send to
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		RB = new FloorRequestBox();
		Thread testthread = new Thread(new SenderFM(RB));
		SchedulerSocket = new DatagramSocket(5012);
		testthread.start();
	}

	/**
	 * Have senderFM send packet to scheduler and compare against the output
	 */
	@Test
	void testSending() {
		//Packet data to compare against
		byte [] expected = {1,0,1,1,1,0,0,0,3,1,0,0,0,8,0,0,0,0,0};
		ElevatorRequest testcase = new ElevatorRequest(3,8,Direction.UP, Error.NONE, 0);
		RB.putRequest(testcase);
		//get packet from SenderFM
		byte data[] = new byte[100];
		receivepacket = new DatagramPacket(data, data.length);
        
        try {
           // Block until a datagram is received via sendReceiveSocket.  
           SchedulerSocket.receive(receivepacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }
        //Compare data from SenderFM to expected data
        byte [] recdata = Arrays.copyOf(receivepacket.getData(), receivepacket.getLength());
        assert(Arrays.equals(recdata, expected));
	}
}
