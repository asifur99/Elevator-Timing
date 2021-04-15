package elevatorSubsystem;

import java.util.HashSet;

/**
 * Class is a shared memory object
 * used to pass close door notifications to
 * elevators
 * @author Edmond Chow, Asifur Rahman 
 *
 */
public class DoorBox {
    private HashSet<Integer> ts;

    /**
     * Default Constructor for the DoorBox
     */
    public DoorBox(){
        ts = new HashSet<Integer>();
    }

    /**
     * Add elevator ID to the hashset, indicating that door
     * close notification has arrived for given elevator
     * @param elevator elevator who can continue moving
     */
    public synchronized void placeElevator(int elevator){
        ts.add(elevator);
        notifyAll();
    }

    /**
     * To remove the elevator id given from the HashSet
     * used by elevators requesting permission to continue moving
     * @param elevator
     */
    public synchronized boolean getClosedSignal(int elevator){
        while(!ts.contains(elevator)){
            try{
                wait();
            } catch(InterruptedException err){
                err.printStackTrace();
            }
        }
        return ts.remove(elevator);
    }
}
