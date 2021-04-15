package uiSubsystem;

import sharedObjects.Direction;
import sharedObjects.Error;

/**
 * An interface for the backend to update the UI
 *
 * @author Ashton Mohns
 */ 
public interface FrontEndInterface {

    /**
     * Update the location of the elevator and it's current direction
     *
     * @param elevatorId elevator whose location is moved
     * @param floor the current floor of the elevator
     * @param direction the direction it is currently moving
     */
    void updateElevatorLocation(int elevatorId, int floor, Direction direction);
    
    /**
     * Update the current direction of the elevator
     *
     * @param elevatorId elevator whose location is moved
     * @param direction the direction it is currently moving
     */
    void updateElevatorDirection(int elevatorId, Direction direction);

    /**
     * Start or stop the elevator based on doors closing or opening, respectively
     *
     * @param elevatorId the elevator to update
     * @param stopped true if stopped, else moving
     */
    void updateElevatorDoorsOpen(int elevatorId, boolean stopped);

    /**
     * Place elevator in the error state based on the input error
     *
     * @param elevatorId the elevator to update
     * @param error the error type to update the elevator with
     */
    void handleElevatorError(int elevatorId, Error error);
}
