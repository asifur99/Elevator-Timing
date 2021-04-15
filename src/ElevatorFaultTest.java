import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import elevatorSubsystem.DoorBox;
import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorMotor;
import elevatorSubsystem.FailedJob;
import elevatorSubsystem.RequestHandler;
import sharedObjects.Direction;
import sharedObjects.Error;
import sharedObjects.SchedulerElevatorBox;

/**
 * Test case to verify that elevators will go into a stop state when encountering an arrival error or a 
 * timer error
 * @author Edmond Chow
 *
 */
class ElevatorFaultTest {
	static SchedulerElevatorBox elevatorBox = new SchedulerElevatorBox();
	static DoorBox doorBox = new DoorBox();
	static FailedJob fjq = new FailedJob();
	/**
	 * Create two instances of elevators to test errors
	 * @throws Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
    	new Thread(new Elevator(elevatorBox, 1,new ElevatorMotor(),doorBox,fjq)).start();
    	new Thread(new Elevator(elevatorBox, 2,new ElevatorMotor(),doorBox,fjq)).start();
	}

	/**
	 * Tear Down method
	 * @throws Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}
	/**
	 * Send Timer errors to elevator and verify that elevator 
	 * tells system it is in a stop state
	 */
	@Test
	void testTimerError() {
		RequestHandler tempReq = new RequestHandler(1, Direction.UP, 2, 4, 0, Error.TIME, 2);
		elevatorBox.placeRequest(tempReq, 1);
		try {
			tempReq = elevatorBox.getMovementUpdate();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Elevator 1 is in state " + tempReq.getDirection());
		assert(tempReq.getDirection()==Direction.STOP);
	}
	/**
	 * Send Arrival errors to elevator and verify that elevator 
	 * tells system it is in a stop state
	 */
	@Test
	void testArrivalError() {
		RequestHandler tempReq = new RequestHandler(2, Direction.UP, 2, 4, 0, Error.ARRIVAL, 2);
		elevatorBox.placeRequest(tempReq, 2);
		try {
			tempReq = elevatorBox.getMovementUpdate();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Elevator 2 is in state " + tempReq.getDirection());
		assert(tempReq.getDirection()==Direction.STOP);
	}
}
