package schedulerSubsystem;

import java.util.HashMap;
import java.util.Map;
import sharedObjects.Constants;
import sharedObjects.Direction;
import sharedObjects.Pair;

/**
 * Store all location data for each elevator for scheduler use.
 * 
 * @author Ashton Mohns
 *
 */
public class ElevatorData {
	private static ElevatorData instance;
	private HashMap<Integer, Pair<Integer, Direction>> elevatorsData;
	
	/**
	 * Default constructor initializing instance variables
	 */
	private ElevatorData() {
		elevatorsData = new HashMap<>();
		for(int i =1;i<=Constants.elevator;i++) {
			elevatorsData.put(i, new Pair<>(1, Direction.STATIONARY));
		}
	}
	
	/**
	 * Singleton constructor. There should only ever be a single instance of this class.
	 * @return instance of the ElevatorData
	 */
	public static synchronized ElevatorData getInstance() {
		if(instance == null) {
			instance = new ElevatorData();
		}
		return instance;
	}
	
	/**
	 * Add a new elevator to the map
	 * @param elevatorId id of new elevator
	 * @return false if elevatorId is duplicated
	 */
	public synchronized boolean addElevator(Integer elevatorId) {
		if(elevatorsData.containsKey(elevatorId)) {
			return false;
		}
		elevatorsData.put(elevatorId, new Pair<Integer, Direction>(1, Direction.STATIONARY));
		return true;
	}
	
	/**
	 * Get data for a specific elevator
	 * @param elevatorId id of the elevator
	 * @return Pair of data for specific elevator
	 */
	public synchronized Pair<Integer, Direction> getElevatorData(Integer elevatorId) {
		return this.elevatorsData.get(elevatorId).clone();
	}
	
	/**
	 * Clone the elevator data to ensure that the locations cannot be updated by reference
	 * @return a cloned map
	 */
	public synchronized HashMap<Integer, Pair<Integer, Direction>> getElevatorsData() {
		HashMap<Integer, Pair<Integer, Direction>> clonedData = new HashMap<>();
		
		for(Map.Entry<Integer, Pair<Integer, Direction>> data : elevatorsData.entrySet()) {
			clonedData.put(data.getKey(), data.getValue().clone());
		}
		
		return clonedData;
	}
	
	/**
	 * Update the elevator location for a given elevator
	 *
	 * @param elevatorId id of the elevator
	 * @param floor to be updated elevator location
	 * @param direction to be updated
	 */
	public synchronized void updateElevatorLocation(Integer elevatorId, Integer floor, Direction direction) {
		Pair<Integer, Direction> location = this.elevatorsData.get(elevatorId);
		location.setT(floor);
		location.setV(direction);
	}
}
