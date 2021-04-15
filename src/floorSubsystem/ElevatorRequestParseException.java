package floorSubsystem;

/**
 * ElevatorRequestParseException occurs when a there is a failed parse of the input file
 * 
 * @author Ashton Mohns
 *
 */
public class ElevatorRequestParseException extends FileInputException {
	private static final long serialVersionUID = 5782975719015452175L;

	/**
	 * Default constructor
	 * 
	 * @param msg to be passed
	 */
	public ElevatorRequestParseException(String msg) {
		super(msg);
	}
	
	/**
	 * Overloaded constructor
	 * 
	 * @param msg to be passed
	 * @param t exception
	 */
	public ElevatorRequestParseException(String msg, Throwable t) {
		super(msg, t);
	}	
}
