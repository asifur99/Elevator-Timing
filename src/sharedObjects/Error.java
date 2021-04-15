package sharedObjects;

/**
 * Possible Errors that we would encounter
 * 
 * @author Jyotsna Mahesh
 *
 */
public enum Error {
	NONE((byte) 0), ARRIVAL((byte) 1), TIME((byte) 2), DOOR((byte) 3);
	private byte value;
	
	/**
	 * Default constructor initializing instance variables
	 * @param value to be passed in as a parameter
	 */
	private Error(byte value) {
		this.value = value;
	}
	
	/**
	 * Value is stored as a byte rather than int to minimize data packet size.
	 * @return value
	 */
	public byte getValue() {
		return this.value;
	}
	
	/**
	 * Convert a byte into a error
	 *
	 * @param value that is passed in as a byte
	 * @return the error
	 */
	public static Error getError(byte value) {
		for(Error err : values()) {
			if(err.getValue() == value) {
				return err;
			}
		}
		
		return null;
	}
}
