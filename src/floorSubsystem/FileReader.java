package floorSubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sharedObjects.ElevatorRequest;

/**
 * This class will read an input file and convert the data into a list of ElevatorRequests.
 * 
 * @author Ashton Mohns
 *
 */
public class FileReader {
	private final String FILENAME;
	
	/**
	 * Create a new FileReader to read from a file with given filename.
	 * Default constructor initializing instance variables
	 * @param filename input file
	 */
	public FileReader(String filename) {
		this.FILENAME = filename.endsWith(".txt") ? filename : filename + ".txt";
	}
	
	/**
	 * Read all contents of file and convert into ElevatorRequests.
	 * @return a list of requests
	 * @throws FileInputException error while reading file
	 */
	public List<ElevatorRequest> readFromFile() throws FileInputException {
		File file = new File(FILENAME);
		try(Scanner scanner = new Scanner(file)) {
			List<ElevatorRequest> inputs = new ArrayList<>();
			while(scanner.hasNextLine()) {
				inputs.add(ElevatorRequest.parse(scanner.nextLine()));
			}
			
			return inputs;
			
		} catch (FileNotFoundException e) {
			throw new FileInputException("Could not find input file", e);
		}
	}
}
