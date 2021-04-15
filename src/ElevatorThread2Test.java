import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import elevatorSubsystem.ElevatorThread2;
import elevatorSubsystem.RequestHandler;
import sharedObjects.SchedulerElevatorBox;
/**
 * Tests that ElevatorThread2 is properly interpreting the packets from thread2 
 * and adding them to intermediary box
 * 
 * @author Edmond Chow, Ashton Mohns
 *
 */
class ElevatorThread2Test {
	
	private static DatagramSocket sendsocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static SchedulerElevatorBox EB;
	/**
	 * Set up ElevatorThread2 thread and shared memory to test
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		EB = new SchedulerElevatorBox();
		Thread testthread = new Thread(new ElevatorThread2(EB));
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
		byte [] packet = {1,0,1,1,2,0,0,0,1,1,0,0,0,2,0,0,0,6,0,0,0,0,0};
		
		sendpacket = new DatagramPacket(packet,packet.length,InetAddress.getLocalHost(), 5015) ;
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
		RequestHandler req = EB.takeRequest(1);
		assert (1 == req.getElevatorId() && 2 == req.getReqFloor() && 6 == req.getDestFloor());
	}
}
