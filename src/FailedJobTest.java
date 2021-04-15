import sharedObjects.ElevatorRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import elevatorSubsystem.FailedJob;

/**
 * Testing the FailedJob class for any failed jobs
 * @author Jyotsna Mahesh
 *
 */
class FailedJobTest {
	private ElevatorRequest request;
	private FailedJob box;
	
	/**
	 * Set up before testing
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		request = ElevatorRequest.parse("13:05:15.000 2 Up 4 None 0");
		box = new FailedJob();
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
	 * 
	 * @throws InterruptedException
	 */
	@Test
	void test() throws InterruptedException {
		// test placeRequest
		box.placeRequest(request);
		assertEquals(request, box.takeRequest());
	}
}
