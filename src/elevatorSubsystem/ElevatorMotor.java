package elevatorSubsystem;
import sharedObjects.Constants;
import sharedObjects.Error;

/**
 * Class used as substitute of actual elevator motor and arrival sensor
 * 
 * @author Edmond Chow
 *
 */
public class ElevatorMotor {
	/**
	 * Wait a period of time to simulate elevator movement and then return
	 * if the arrival sensor should fault
	 * @param position current position of elevator
	 * @param type type of error that might occur
	 * @param errorFloor floor where error is supposed to occur
	 * @return whether there was an error or not
	 */
	public boolean  move(int position, Error type,int errorFloor) {
		try {
			//if next floor is error floor and error if floor timer take 6 seconds
			if (type == Error.TIME) {
				Thread.sleep(Constants.TIMER_FAULT);
			}else {
				Thread.sleep(Constants.MOVE_TIME);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//if next floor is error floor and error is arrival sensor, return error
		if (type == Error.ARRIVAL) {
			return true;
		}
		
		return false;
	}
	
}
