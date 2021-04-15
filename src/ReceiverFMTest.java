import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import floorSubsystem.ReceiverFM;
import sharedObjects.FloorRequestBox;

/**
 * Test the ReceiverFM Threads ability to process
 * responses from scheduler and add it to shared memory
 * 
 * @author Edmond Chow, Ashton Mohns
 *
 */
class ReceiverFMTest {
	
	private static DatagramSocket schedulerSocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static FloorRequestBox RB;
	/**
	 * set up scheduler socket, shared memory and ReciverFM thread
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		RB = new FloorRequestBox();
		Thread testthread = new Thread(new ReceiverFM(RB));
		schedulerSocket = new DatagramSocket();
		testthread.start();
	}
	/**
	 * Send packet to thread and test response in shared memory
	 * @throws UnknownHostException
	 */
	@Test
	void testResponseReception() throws UnknownHostException {
		byte [] outdata = {1,0,1,1,4,0,0,0,1,0,0,0,8,0,0,0,0,0};//packet to send
		sendpacket = new DatagramPacket(outdata,outdata.length,InetAddress.getLocalHost(), 5011) ;
		try {
			schedulerSocket.send(sendpacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//evaluate response
		assertEquals(RB.getFloorResponse(), 8);
	}
}
