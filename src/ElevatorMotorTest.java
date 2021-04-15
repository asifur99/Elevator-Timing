import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import elevatorSubsystem.ElevatorMotor;
import sharedObjects.Error;

/**
 * Testing the ElevatorMotor class to check if its giving
 * the correct response when given an error
 * @author Jyotsna Mahesh, Edmond Chow
 *
 */

class ElevatorMotorTest {
	private ElevatorMotor EM;
	private boolean b = false;
	
	/**
	 * Set up before testing
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		EM = new ElevatorMotor();
	}
	
	/**
	 * tear down after testing
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		EM = null;
	}
	
	/**
	 * test Elevator Motor Move UP No Error
	 */
	@Test
	public void testMove() {
		
		long before, after;
		
		before = System.nanoTime();
		b = EM.move(2, Error.NONE, 3);
		after = System.nanoTime();
		
		assert(!b);
		assert((after-before)<5000000000.0);
	}

	/**
	 * test Elevator Motor Move UP Time Error
	 */
	@Test
	public void testTimerError() {
	
		long before, after;
		
		before = System.nanoTime();
		b = EM.move(2, Error.TIME, 3);
		after = System.nanoTime();
		
		assert(!b);
		assert((after-before)>5000000000.0);
	}

	/**
	 * test Elevator Motor Move DOWN Arrival error
	 */
	@Test
	public void testArrivalError() {
		b = EM.move(2, Error.ARRIVAL, 3);
		assert(b);	
	}
}
