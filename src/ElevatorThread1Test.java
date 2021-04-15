import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import elevatorSubsystem.ElevatorThread1;
import elevatorSubsystem.FailedJob;
import sharedObjects.Direction;
import sharedObjects.ElevatorRequest;
import sharedObjects.Error;

/**
 * Testing the ElevatorThread1 class
 * @author Edmond Chow, Ashton Mohns, Jyotsna Mahesh
 *
 */
class ElevatorThread1Test {
	
	private static DatagramSocket recsocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static FailedJob box;
	
	/**
	 * Set up thread to test, intermediary Queue it pulls from,
	 * and socket that thread will send to
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		box = new FailedJob();
		Thread testthread = new Thread(new ElevatorThread1(box));
		recsocket = new DatagramSocket(5012);
		testthread.start();
	}
	/**
	 * Load a response into intermediary queue and then check to see if packet sent
	 * matches the pre agreed response
	 */
	@Test
	void testSending() {
		//byte array to test against from thread
		byte [] inpacket = {1,0,1,1,1,0,0,0,3,1,0,0,0,8,0,0,0,0,0};
		//place response in intermediary queue
		ElevatorRequest testcase = new ElevatorRequest(3,8,Direction.UP, Error.NONE, 0);
		box.placeRequest(testcase);
		//get packet from ElevatorThread3
		byte data[] = new byte[100];
		receivepacket = new DatagramPacket(data, data.length);
        try {
           // Block until a datagram is received via sendReceiveSocket.  
           recsocket.receive(receivepacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }
        //Send response to thread
        byte [] outpacket = {1,0,1,1,0};
        sendpacket = new DatagramPacket(outpacket, outpacket.length,receivepacket.getAddress(),receivepacket.getPort());
        try {
			recsocket.send(sendpacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// Compare data from packet to test data
		byte[] recdata = Arrays.copyOf(receivepacket.getData(), receivepacket.getLength());
		assert (Arrays.equals(recdata, inpacket));
	}
}