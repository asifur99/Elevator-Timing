import static org.junit.Assert.*;
import java.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sharedObjects.ElevatorRequest;
/**
 * 
 * @author Md Aiman Sharif, Ashton Mohns
 * Test class for testing class ElevatorRequest
 */
public class ElevatorRequestTest {
	private ElevatorRequest elevatorRequest;
	private int floor;
	private int carButton;
	private LocalTime time;
	private enum Direction{
		UP, DOWN
	}
	
	/**
	 * Setup the test
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		elevatorRequest = ElevatorRequest.parse("13:05:15.000 4 Up 6 None 0");
		time = LocalTime.parse("13:05:15.000");
		floor = 4;
		carButton = 6;
	}
	
	/**
	 * tear down after the testing is done
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		elevatorRequest = null;
		time = LocalTime.parse("00:00:00.000");
		floor = 0;
		carButton = 0;
	}
	
	/**
	 * test get floor actual with expected
	 */
	@Test
	public void testGetFloor() {
		assertEquals(floor, elevatorRequest.getFloor());
	}
	
	/**
	 * test get time actual vs expected
	 */
	@Test
	public void testGetTime() {
		assertEquals(time, elevatorRequest.getTime());
	}
	
	/**
	 * test the direction actual vs expected
	 */
	@Test
	public void testGetDirection() {
		assertEquals((Direction.UP).toString(), elevatorRequest.getDirection().toString());
	}
	
	/**
	 * test the car button actual vs expected
	 */
	@Test
	public void testGetcarButton() {
		assertEquals(carButton, elevatorRequest.getCarButton());
	}
}
