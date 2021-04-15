package main;

/**
 * Main class if you want to run the full program through one call.
 * When running through this, the console will print all messages from all subsystems.
 * If you want unique consoles per subsystem, run the other main classes in the following order:
 * 1. UserInterfaceMain
 * 2. SchedulerMain
 * 3. MainEM (elevator subsystem)
 * 4. MainFM (floor subsystem)
 * 
 * @author Ashton Mohns
 */
public class Main {

	/**
	 * Main method
	 * 
	 * @param args command line arguments
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// Start up the UI
		UserInterfaceMain.main(args);
		
		// Start up scheduler subsystem
		SchedulerMain.main(args);
    	
    	// Start up elevator subsystem
    	MainEM.main(args);
    	
    	// Start up floor subsystem
    	MainFM.main(args);
	}
}
