package main;

import uiSubsystem.FrontEndInterface;
import uiSubsystem.UserInterfaceEndpoint;
import uiSubsystem.View;

/**
 * Main class for UserInterface
 * Only thread is for the user interface endpoint, which listens for requests sent to update the UI.
 * 
 * @author Ashton Mohns
 */
public class UserInterfaceMain {

	/**
	 * Main method for running the UserInterface
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		FrontEndInterface i = new View();
		
		System.out.println("User Interface running\n");
		
		new Thread(new UserInterfaceEndpoint(i)).start();
	}
}
