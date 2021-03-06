package FloorSubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import Messages.ElevatorHardFaultMessage;
import Messages.ElevatorTransientFaultMessage;
import Messages.FloorDataMessage;
import Messages.Message;
import SharedResources.DirectionEnum;

/**
 * @author Jason Gao
 *
 *         A class for parsing floor data file input
 */
public class FloorDataParser {

	/**
	 * Opens a floor data file, parses each line into a byte array, then adds the
	 * byte array to the floor subsystem list of byte array messages
	 * 
	 * @param filename The floor data file name
	 */
	public void parseFile(String filename) {
		try {
			File floorDataFile = new File(filename);
			Scanner fileReader = new Scanner(floorDataFile);

			// iterate through the floor data file line by line
			while (fileReader.hasNextLine()) {
				String floorData = fileReader.nextLine(); // This looks like 14:05:15.0 2 Up 4
				List<String> lineAsArrayList = this.str2ArrayList(floorData, " "); // Looks like [14:05:15.0, 2, Up, 4]

				// get timestamp
				float timestamp = this.timestampToFloat(lineAsArrayList.get(0));

				String messageIdentifier = lineAsArrayList.get(1);

				Message fm = null;
				switch (messageIdentifier) {
				case "floor_message": {
					// get current floor
					int currFloor = Integer.parseInt(lineAsArrayList.get(2));

					// get elevator direction
					DirectionEnum direction = lineAsArrayList.get(3) == "Up" ? DirectionEnum.UP_DIRECTION
							: DirectionEnum.DOWN_DIRECTION;

					// get destination floor
					int destinationFloor = Integer.parseInt(lineAsArrayList.get(4));
					
					if (!fileReader.hasNextLine()) {
						fm = new FloorDataMessage(timestamp, currFloor, direction, destinationFloor, true);
					} else {
						fm = new FloorDataMessage(timestamp, currFloor, direction, destinationFloor, false);
					}
					
					break;
				}
				case "transient_fault": {
					int elevatorID = Integer.parseInt(lineAsArrayList.get(2));

					int timeOfFault = Integer.parseInt(lineAsArrayList.get(3));
					
					if (!fileReader.hasNextLine()) {
						fm = new ElevatorTransientFaultMessage(timeOfFault, elevatorID, timestamp, true);
					} else {
						fm = new ElevatorTransientFaultMessage(timeOfFault, elevatorID, timestamp, false);
					}

					break;
				}
				case "hard_fault": {
					int elevatorID = Integer.parseInt(lineAsArrayList.get(2));
					
					if (!fileReader.hasNextLine()) {
						fm = new ElevatorHardFaultMessage(elevatorID, timestamp, true);
					} else {
						fm = new ElevatorHardFaultMessage(elevatorID, timestamp, false);
					}

					
					break;
				}

				default:
					break;
				}

				FloorSystem.addFloorMessage(fm);

			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Converts a timestamp from 14:05:15.0 (hh:mm:ss.mmm) to its float value in
	 * seconds
	 * 
	 * @param timestamp The string timestamp in the format of hh:mm:ss.mmm
	 * @return The number of seconds represented by the timestamp as a float
	 */
	private float timestampToFloat(String timestamp) {
		List<String> timestampAsArrayList = this.str2ArrayList(timestamp, ":"); // looks like [14, 05, 15.0]
		float timestampFloat = 0;

		float hours = Float.parseFloat(timestampAsArrayList.get(0)) * 3600;
		float minutes = Float.parseFloat(timestampAsArrayList.get(1)) * 60;
		float seconds = Float.parseFloat(timestampAsArrayList.get(2));

		timestampFloat += hours + minutes + seconds;
		return timestampFloat;
	}

	/**
	 * Convert a String into an ArrayList of Strings
	 * 
	 * @param stringToConvert The String to convert
	 * @param delimeter       A delimeter to split the string on
	 * @return An ArrayList where each word in stringToConvert is an ArrayList
	 *         element
	 */
	private List<String> str2ArrayList(String stringToConvert, String delimeter) {
		String str[] = stringToConvert.split(delimeter);
		List<String> convertedArrayList = new ArrayList<String>();
		convertedArrayList = Arrays.asList(str);
		return convertedArrayList;
	}
}
