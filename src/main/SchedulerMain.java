package main;

import schedulerSubsystem.DoorIntermediateHost;
import schedulerSubsystem.ElevatorData;
import schedulerSubsystem.ElevatorServer;
import schedulerSubsystem.Thread1;
import schedulerSubsystem.Thread2;
import sharedObjects.SchedulerFloorRequestBox;
/**
 * Main class to start up the scheduler subsystem.
 * The threads created will handle incoming requests, distribute requests to elevators,
 * handle elevator move requests and handle door open / close requests.
 * 
 * @author Md Aiman Sharif
 */
public class SchedulerMain {
	
	/**
     * Main method for running the scheduler subsystem.
     *
     * @param args command line arguments
     */
	public static void main(String[] args) {
		SchedulerFloorRequestBox FRB = new SchedulerFloorRequestBox();
		ElevatorData map = ElevatorData.getInstance();
    	
    	new Thread(new Thread1(FRB)).start();
    	new Thread(new Thread2(map,FRB)).start();
    	new Thread(new ElevatorServer(map)).start();
    	new Thread(new DoorIntermediateHost()).start();
	}
}
