import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import schedulerSubsystem.ElevatorData;
import schedulerSubsystem.ElevatorServer;
import sharedObjects.Direction;
import sharedObjects.Pair;

/**
 * Test that ElevatorServer is correctly updating ElevatorMap and sending
 * Correct packets to ReceiverFM
 * @author Edmond Chow, Ashton Mohns
 *
 */
class ElevatorServerTest {
	private static DatagramSocket EMsocket;
	private static DatagramSocket FMsocket;
	DatagramPacket sendpacket;
	DatagramPacket receivepacket;
	private static ElevatorData map;
	
	/**
	 * Set up sockets for FM and EM, elevator map, and
	 * Elevator server thread
	 * @throws Exception
	 */
	@BeforeAll
	public static void setUp() throws Exception {
		map = ElevatorData.getInstance();
		Thread testthread = new Thread(new ElevatorServer(map));
		EMsocket = new DatagramSocket();
		testthread.start();
		FMsocket = new DatagramSocket(5011);
	}
	
	/**
	 * Test that proper response to EM packet is being sent by Elevator Server
	 * @throws UnknownHostException
	 */
	@Test
	void testResponse() throws UnknownHostException {
		//packet to send to ElevatorServer
		byte [] packet = {1,0,1,1,3,0,0,0,1,1,0,0,0,2,0,0,0,0,0,0};
		
		sendpacket = new DatagramPacket(packet,packet.length,InetAddress.getLocalHost(), 5014) ;
		try {
			EMsocket.send(sendpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//get Response
		byte data[] = new byte[100];
		receivepacket = new DatagramPacket(data, data.length);
        
        try {
           // Block until a datagram is received via sendReceiveSocket.  
           EMsocket.receive(receivepacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }
        //Evaluate response is correct
        assert(receivepacket.getData()[4]==(byte)0);
        
	}
	
	/**
	 * Evaluate that ElevatorServer is properly updating
	 * the mapping in ElevatorMap in response to EM packets
	 * @throws UnknownHostException
	 */
	@Test
	void testMapping() throws UnknownHostException {
		//packet with new elevator positional data
		byte [] packet = {1,0,1,1,3,0,0,0,1,1,0,0,0,8,0,0,0,0,0,0};
		//Sending packet to ElevatorServer
		sendpacket = new DatagramPacket(packet,packet.length,InetAddress.getLocalHost(), 5014) ;
		try {
			EMsocket.send(sendpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Getting info from map and verifying the correct location/direction
		Pair<Integer, Direction> p = map.getElevatorData(1);
		assert(p.getT()==8 && p.getV() == Direction.UP);
		
	}
	
	/**
	 * Test that ElevatorServer sends packet to FileLoader Manager 
	 * when correct packet is sent by Elevator Manager
	 * @throws UnknownHostException
	 */
	@Test
	void testOpen() throws UnknownHostException {
		byte [] outpacket = {1,0,1,1,3,0,0,0,1,1,0,0,0,10,1,0,0,0,0,0}; //Packet from EM to elevator server
		byte [] inpacket = {1,0,1,1,4,0,0,0,1,0,0,0,10,0}; // expected packet from Elevator Server
		//send packet to elevator server
		sendpacket = new DatagramPacket(outpacket,outpacket.length,InetAddress.getLocalHost(), 5014) ;
		try {
			EMsocket.send(sendpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//get elevator server packet to floor manager
		byte data[] = new byte[100];
		receivepacket = new DatagramPacket(data, data.length);
        
        try {
           // Block until a datagram is received via sendReceiveSocket.  
           FMsocket.receive(receivepacket);
        } catch(IOException e) {
           e.printStackTrace();
           System.exit(1);
        }
        //send floor manger's response to elevator server
        byte [] response = {1,0,1,1,0};
        sendpacket = new DatagramPacket(response,response.length,receivepacket.getAddress(), receivepacket.getPort()); 
        try {
            // Block until a datagram is received via sendReceiveSocket.  
            FMsocket.send(sendpacket);
         } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
         }
        //Compare packet from Elevator server to expected packet
        assert(Arrays.equals(inpacket, Arrays.copyOf(receivepacket.getData(), receivepacket.getLength())));
	}	
}
