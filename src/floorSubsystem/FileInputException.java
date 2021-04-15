package floorSubsystem;

/**
 * FileInputException occurs when an exception occurs in file reading occurs.
 * 
 * @author Ashton Mohns
 *
 */
public class FileInputException extends Exception {
	private static final long serialVersionUID = 3037546743217835057L;

	/**
	 * Default Constructor
	 * @param msg to be passed
	 */
	public FileInputException(String msg) {
		super(msg);
	}
	
	/**
	 * Overloaded constructor
	 * 
	 * @param msg to be passed
	 * @param t exception
	 */
	public FileInputException(String msg, Throwable t) {
		super(msg, t);
	}
}
