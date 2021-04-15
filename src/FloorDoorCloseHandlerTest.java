
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import floorSubsystem.FloorDoorCloseHandler;
import sharedObjects.FloorRequestBox;

/**
 * Tests that SenderFM thread will properly send packets containing Elevator request data 
 * to port 5012 in the correct format
 * 
 * @author Edmond Chow, Jyotsna Mahesh
 *
 */
class FloorDoorCloseHandlerTest {
	private static DatagramSocket DoorSocket;
	private static FloorRequestBox RB;
	DatagramPacket sendpacket, receivepacket;
	
	/**
	 * set up new instance of SenderFM, box thread pulls requests from
	 * and socket for SenderFM to send to
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		RB = new FloorRequestBox();
		Thread testthread = new Thread(new FloorDoorCloseHandler(RB));
		DoorSocket = new DatagramSocket(5017);
		testthread.start();
	}

	/**
	 * Have senderFM send packet to scheduler and compare against the output
	 */
	@Test
	void testSending() {
		//Packet data to compare against
		byte [] expected = {1,0,1,1,6,0,0,0,1};
		int testcase = 1;
		RB.putDoorCloseResponse(testcase);
		//get packet from SenderFM
		byte data[] = new byte[100];
		receivepacket = new DatagramPacket(data, data.length);
        
        try {
           // Block until a datagram is received via sendReceiveSocket.  
           DoorSocket.receive(receivepacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }
        //Compare data from SenderFM to expected data
        byte [] recdata = Arrays.copyOf(receivepacket.getData(), receivepacket.getLength());
        assert(Arrays.equals(recdata, expected));
	}
}