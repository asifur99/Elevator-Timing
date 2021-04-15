import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import elevatorSubsystem.DoorBox;

/**
 * Testing the DoorBox class 
 * @author Jyotsna Mahesh
 *
 */
class DoorBoxTest {
	private DoorBox box;
	private int num;
	
	/**
	 * Set up before testing
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.num = 1;
		this.box = new DoorBox();
	}
	
	/**
	 * tear down after testing
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		num = 0;
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
		box.placeElevator(num);
		assertEquals(true,box.getClosedSignal(num));
	}
}
