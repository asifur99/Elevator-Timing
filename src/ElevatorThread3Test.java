import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import elevatorSubsystem.ElevatorThread3;
import elevatorSubsystem.RequestHandler;
import sharedObjects.Direction;
import sharedObjects.SchedulerElevatorBox;
import sharedObjects.Error;

/**
 * Test that ElevatorThread3 is properly sending information
 * found in intermediary queue to port 5014
 * 
 * @author Edmond Chow, Ashton Mohns
 *
 */
public class ElevatorThread3Test {
	private static DatagramSocket recsocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static SchedulerElevatorBox EB;
	
	/**
	 * Set up thread to test, intermediary Queue it pulls from,
	 * and socket that thread will send to
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		EB = new SchedulerElevatorBox();
		Thread testthread = new Thread(new ElevatorThread3(EB));
		recsocket = new DatagramSocket(5014);
		testthread.start();
	}
	/**
	 * Load a response into intermediary queue and then check to see if packet sent
	 * matches the pre agreed response
	 */
	@Test
	void testSending() {
		//byte array to test against from thread
		byte [] inpacket = {1,0,1,1,3,0,0,0,1,0,0,0,0,2,1,0,0};
		//place response in intermediary queue
		EB.placeMovementUpdate(new RequestHandler(1, Direction.STATIONARY, 2,5, 1, Error.NONE, 0));
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
        } catch(IOException e) {
        	e.printStackTrace();
            System.exit(1);
        }
        //Compare data from packet to test data
        byte [] recdata = Arrays.copyOf(receivepacket.getData(), receivepacket.getLength());
        assert(Arrays.equals(recdata, inpacket));
	}
}
