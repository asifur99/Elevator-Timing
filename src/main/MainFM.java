package main;

import floorSubsystem.FileLoader;
import floorSubsystem.FloorDoorCloseHandler;
import floorSubsystem.FloorResponse;
import floorSubsystem.ReceiverFM;
import floorSubsystem.SenderFM;
import sharedObjects.FloorRequestBox;

/**
 * Main class to start up the floor subsystem.
 * This will create threads to load the data and handle requests for doors to open on specific floors. 
 * 
 * @author Jyotsna Mahesh
 */
public class MainFM {

	/**
     * Main method for running the floor subsystem.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws InterruptedException{
        FloorRequestBox floorBox = new FloorRequestBox();
        FloorRequestBox responseBox = new FloorRequestBox();
        
        new Thread(new SenderFM(floorBox)).start();
        new Thread(new FileLoader(floorBox)).start();
        new Thread(new ReceiverFM(floorBox)).start();
        new Thread(new FloorResponse(floorBox, responseBox)).start(); 
        new Thread(new FloorDoorCloseHandler(responseBox)).start();
    }
}
