package main;

import elevatorSubsystem.DoorBox;
import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorMotor;
import elevatorSubsystem.ElevatorThread1;
import elevatorSubsystem.ElevatorThread2;
import elevatorSubsystem.ElevatorThread3;
import elevatorSubsystem.ElevatorThread4;
import elevatorSubsystem.FailedJob;
import sharedObjects.*;

/**
 * Main class to start up the elevator subsystem. 
 * This will create a number of elevators equal to the number in Constants.elevator.
 * 
 * @author Asifur Rahman, Edmond Chow
 */
public class MainEM {

    /**
     * Main method for running the elevator subsystem.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws InterruptedException{
    	SchedulerElevatorBox elevatorBox = new SchedulerElevatorBox();
		DoorBox doorBox = new DoorBox();
		FailedJob failedJob = new FailedJob();
    	
		new Thread(new ElevatorThread1(failedJob)).start();
    	new Thread(new ElevatorThread2(elevatorBox)).start();
    	
		for (int i = 1; i <= Constants.elevator; i++) {
			new Thread(new Elevator(elevatorBox, i, new ElevatorMotor(), doorBox, failedJob)).start();
		}

    	new Thread(new ElevatorThread3(elevatorBox)).start();
    	new Thread(new ElevatorThread4(doorBox)).start();
    }
}
