import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sharedObjects.ElevatorRequest;
import sharedObjects.SchedulerFloorRequestBox;

/**
 * @author Md Aiman Sharif, Ashton
 * 
 * Test class for testing SchedulerFloorRequestBox
 */
public class SchedulerFloorRequestBoxTest {
	private ElevatorRequest request;
	private SchedulerFloorRequestBox box;
	  
	/**
	 * set up before testing
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		request = ElevatorRequest.parse("13:05:15.000 2 Up 4 None 0");
		box = new SchedulerFloorRequestBox();
		System.out.println("This is box: " + box);
	}

	/**
	 * tear down after testing is done
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		request = null;
		box = null;
	}
	
	/**
	 * test add request and get request using the methods
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws InterruptedException {
		// test addRequest
		box.addRequest(request);
		assertEquals(request, box.getRequest(0));
	}
}
