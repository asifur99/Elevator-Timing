package sharedObjects;

/**
 * All possible elevator directions.
 *
 * @author Ashton Mohns, Jyotsna Mahesh
 */
public enum Direction {
	STATIONARY((byte) 0), UP((byte) 1), DOWN((byte) 2), STOP((byte) 3);
	private byte value;
	
	/**
	 * Default constructor initializing instance variables
	 * @param value to be passed in as a parameter
	 */
	private Direction(byte value) {
		this.value = value;
	}
	
	/**
	 * Value is stored as a byte rather than int to minimize data packet size
	 * @return value
	 */
	public byte getValue() {
		return this.value;
	}
	
	/**
	 * Convert a byte into a direction
	 *
	 * @param value that is passed in as a byte
	 * @return the direction
	 */
	public static Direction getDirection(byte value) {
		for (Direction dir : values()) {
			if (dir.getValue() == value) {
				return dir;
			}
		}

		return null;
	}
}
