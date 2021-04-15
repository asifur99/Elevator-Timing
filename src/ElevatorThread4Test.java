import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import elevatorSubsystem.ElevatorThread4;
import elevatorSubsystem.DoorBox;

/**
 * Tests that ElevatorThread4 is properly interpreting the packets from thread2 
 * and adding them to intermediary box
 * 
 * @author Edmond Chow, Ashton Mohns, Jyotsna Mahesh
 *
 */
class ElevatorThread4Test {
	private static DatagramSocket sendsocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static DoorBox box;
	/**
	 * Set up ElevatorThread2 thread and shared memory to test
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		box = new DoorBox();
		Thread testthread = new Thread(new ElevatorThread4(box));
		sendsocket = new DatagramSocket();
		testthread.start();
	}

 	/**
 	 * Send a test packet to the thread and see if valid response is sent
 	 * back to tester
 	 * @throws UnknownHostException
 	 */
	@Test
	void testGoodResponse() throws UnknownHostException {
		byte [] packet = {1,0,1,1,7,0,0,0,1};
		
		sendpacket = new DatagramPacket(packet,packet.length,InetAddress.getLocalHost(), 5018) ;
		try {
			sendsocket.send(sendpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte data[] = new byte[100];
		receivepacket = new DatagramPacket(data, data.length);
        
        try {
           // Block until a datagram is received via sendReceiveSocket.  
           sendsocket.receive(receivepacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }
        
		assert (receivepacket.getData()[4] == (byte) 0);
	}
	
	/**
	 * tests that request added in testGoodResponse() was added to intermediary box properly
	 */
	@Test
	void testRequestAdd() {
		box.placeElevator(1);
		assertEquals(true,box.getClosedSignal(1));
	}
}
