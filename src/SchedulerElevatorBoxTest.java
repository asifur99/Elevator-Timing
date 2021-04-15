import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevatorSubsystem.RequestHandler;
import sharedObjects.Direction;
import sharedObjects.SchedulerElevatorBox;
import sharedObjects.Error;

/**
 * @author Md Aiman Sharif, Ashton Mohns
 * 
 * Testing class SchedulerElevatorBox
 */
class SchedulerElevatorBoxTest {
	private SchedulerElevatorBox box;
	private RequestHandler request;

	/**
	 * Set up before testing
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		request = new RequestHandler(2, Direction.UP, 4, 6, 0, Error.NONE, 0);
		box = new SchedulerElevatorBox();
	}

	/**
	 * tear down after testing
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		request = null;
		box = null;
	}

	/**
	 * test adding place request and taking the request
	 * test place response and take response method
	 * @throws InterruptedException
	 */
	@Test
	void test() throws InterruptedException {
		// test placeRequest
		box.placeRequest(request, 2);
		assertEquals(request, box.takeRequest(2));
		
		// test placeResponse
		box.placeMovementUpdate(request);
		assertEquals(request, box.getMovementUpdate());
	}
}
