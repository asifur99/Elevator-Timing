import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import floorSubsystem.FloorResponse;
import sharedObjects.FloorRequestBox;
import sharedObjects.Constants;
import sharedObjects.Error;
/**
 * Test validates that the floor subsystem will properly process door errors
 * 
 * @author Edmond Chow
 *
 */
class FloorResponseTest {
	static FloorRequestBox floorBox = new FloorRequestBox();
    static FloorRequestBox responseBox = new FloorRequestBox();
    
    static DatagramSocket socket;
    /**
     * method used to fake UDP communication with UI, receiving packets
     * from floor system and sending response
     */
    private void mockReponse() {
		DatagramPacket inPacket = new DatagramPacket(new byte[100], 100);
		
		try {
			socket.receive(inPacket);
		} catch (IOException e) {
			System.out.println("Encountered an input error. Possibly socket timeout.");
			e.printStackTrace();
			System.exit(1);
		}
		
		DatagramPacket outPacket = new DatagramPacket(new byte[] {1, 0, 1, 1, 0}, 5, inPacket.getAddress(), inPacket.getPort());
		
		try {
			socket.send(outPacket);
		} catch (IOException e) {
			System.out.println("Encountered an output error. Possibly socket timeout.");
			e.printStackTrace();
			System.exit(1);
		}
	}
    
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		new Thread(new FloorResponse(floorBox, responseBox)).start(); 
		socket = new DatagramSocket(5019);
	}


	/**
	 * Sends packet to floor system indicating no error should occur
	 * and validating that door cycles in normal amount of time
	 */
	@Test
	void testNoError() {
		long start;
		floorBox.putResponse(1, 1, Error.NONE);
		start = System.currentTimeMillis();
		responseBox.getDoorCloseResponse();
		assert((System.currentTimeMillis()-start)<Constants.DOOR_TIME + 1000); 

	}
	
	/**
	 * Send a door error to floor response and validate that the
	 * floor response takes time to validate the error
	 */
	@Test
	void testDoorError() {
		long start;
		floorBox.putResponse(1, 1, Error.DOOR);
		start = System.currentTimeMillis();
		mockReponse();//Floor response updates UI of error
		mockReponse();//Floor response updates UI of return to normal
		responseBox.getDoorCloseResponse();
		assert((System.currentTimeMillis()-start)>Constants.DOOR_TIME + 1000); 
	}
}
