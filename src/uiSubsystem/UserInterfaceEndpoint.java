package uiSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import sharedObjects.Constants;
import sharedObjects.Direction;
import sharedObjects.Error;

/**
 * This class listens for any requests made to the user interface, parses them then updates the 
 * values currently displayed in the UI.
 * 
 * @author Ashton
 *
 */
public class UserInterfaceEndpoint implements Runnable {
	
	private DatagramSocket socket;
	private FrontEndInterface ui;
	
	/**
	 * Constructor for endpoint
	 * @param ui
	 */
	public UserInterfaceEndpoint(FrontEndInterface ui) {
		this.ui = ui;
		try {
			this.socket = new DatagramSocket(5019);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Run method, always wait for updates from other subsystems
	 */
	@Override
	public void run() {
		while(true) {
			listenForUpdate();
		}
	}

	/**
	 * Listen for updates and send responses
	 */
	private void listenForUpdate() {
		DatagramPacket inPacket = new DatagramPacket(new byte[100], 100);
		
		try {
			socket.receive(inPacket);
		} catch (IOException e) {
			System.out.println("Encountered an input error. Possibly socket timeout.");
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("UserInterfaceEndpoint: Received packet");
		
		DatagramPacket outPacket = handlePacket(inPacket);
		
		System.out.println("UserInterfaceEndpoint: Sending response\n");
		try {
			socket.send(outPacket);
		} catch (IOException e) {
			System.out.println("Encountered an output error. Possibly socket timeout.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Parse the requests and update the UI accordingly
	 * 
	 * @param packet contains data for updating the UI
	 * @return response packet
	 */
	private DatagramPacket handlePacket(DatagramPacket packet) {
		ByteBuffer b = ByteBuffer.wrap(packet.getData());
		if(Constants.HEADER != b.getInt() || b.get() != (byte) 10) {
			return new DatagramPacket(new byte[] {1, 0, 1, 1, 1}, 5, packet.getAddress(), packet.getPort());
		}
		
		switch(b.get()) {
			case 0:
				System.out.println("UserInterfaceEndpoint: Updating elevator location");
				ui.updateElevatorLocation(b.getInt(), b.getInt(), Direction.getDirection(b.get()));
				break;
			case 1:
				System.out.println("UserInterfaceEndpoint: Updating elevator direction");
				ui.updateElevatorDirection(b.getInt(), Direction.getDirection(b.get()));
				break;
			case 2:
				System.out.println("UserInterfaceEndpoint: Updating elevator doors open status");
				ui.updateElevatorDoorsOpen(b.getInt(), b.get() == 1);
				break;
			case 3:
				System.out.println("UserInterfaceEndpoint: Updating elevator error state");
				ui.handleElevatorError(b.getInt(), Error.getError(b.get()));
				break;
			default:
				return new DatagramPacket(new byte[] {1, 0, 1, 1, 1}, 5, packet.getAddress(), packet.getPort());
		}
		
		return new DatagramPacket(new byte[] {1, 0, 1, 1, 0}, 5, packet.getAddress(), packet.getPort());
	}
}
