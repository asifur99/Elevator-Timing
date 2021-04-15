import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import sharedObjects.ElevatorRequest;
import sharedObjects.FloorRequestBox;
import sharedObjects.Error;

/**
 * @author Md Aiman Sharif, Ashton Mohns
 * 
 * Test class for testing FloorRequestBox
 */
public class FloorRequestBoxTest {
	private ElevatorRequest request;
	private FloorRequestBox box;
	private int floor_num;
	
	/**
	 * set up the test
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		request = ElevatorRequest.parse("13:05:15.000 2 Up 4 None 0");
		box = new FloorRequestBox();
		floor_num = 2;
		System.out.println("This is box: " + box);
	}
	
	/**
	 * Test adding request, getting request
	 * putting response, and get response
	 */
	@Test
	public void test() {
		// test putRequest
		box.putRequest(request);
		assertEquals(request, box.getRequest());
		
		box.putResponse(floor_num, 0, Error.TIME);
		
		assertEquals(floor_num, box.getFloorResponse());
		assertEquals(Error.TIME, box.getErrorResponse());
		assertEquals(0, box.getElevResponse());
	}
}
