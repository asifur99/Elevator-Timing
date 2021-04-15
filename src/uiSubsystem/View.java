package uiSubsystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import sharedObjects.Direction;
import sharedObjects.Error;

/**
 * Graphical User Interface (GUI) for the ElevatorTiming project
 * @author Md Aiman Sharif
 *
 */
public class View implements FrontEndInterface{
	private JFrame frame;
	private JComponent [][] labelArr;
	private JPanel panel;
	private Border border;
	
	/**
	 * Default constructor initializing instance variables
	 */
	public View() {
		this.frame = new JFrame("ELEVATOR TIMING SYSTEM");
		this.panel = new JPanel(new GridLayout(3, 4, 20, 0));
		this.border = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.labelArr = new JComponent[3][4];
		
		// TriangleItem JLabels Up
		for(int i = 0; i < labelArr[0].length; i++) {
			labelArr[0][i] = new TriangleItem(true);
			labelArr[0][i].setSize(50, 50);
			((JLabel) labelArr[0][i]).setHorizontalAlignment(SwingConstants.CENTER);
			
			panel.add(labelArr[0][i]);
		}
		
		// add the 3 information to infoPanel
		// Elevators all start stationary and on floor 1.
		for(int i = 0; i < labelArr[1].length; i++) { 
			labelArr[1][i] = new JPanel(new GridLayout(3, 1));
			labelArr[1][i].add(new JLabel("Elevator " + (i + 1)));
			JLabel floorLabel = new JLabel("Floor " + 1);
			floorLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
			labelArr[1][i].add(floorLabel);
			labelArr[1][i].add(new JLabel("Status: STATIONARY"));
			
			labelArr[1][i].setBorder(border);
			
			panel.add(labelArr[1][i]);
		}
		
		// TriangleItems JLabels Down
		for(int i = 0; i < labelArr[0].length; i++) {
			labelArr[2][i] = new TriangleItem(false);
			((JLabel) labelArr[2][i]).setHorizontalAlignment(SwingConstants.CENTER);
			panel.add(labelArr[2][i]);
		}
		
		frame.add(panel);
		frame.setSize(700, 350);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Method to update the elevator location
	 */
	@Override
	public void updateElevatorLocation(int elevatorId, int floor, Direction direction) {
		// Reset the direction initially
		resetDirectionLamp(elevatorId);
		
		((JLabel)labelArr[1][elevatorId - 1].getComponent(1)).setText("Floor: " + floor);
		
		if(direction == Direction.UP) {
			labelArr[0][elevatorId - 1].setBackground(Color.YELLOW);
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: RUNNING");
		} else if(direction == Direction.DOWN) {
			labelArr[2][elevatorId - 1].setBackground(Color.YELLOW);
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: RUNNING");
		} else if(direction == Direction.STATIONARY) {
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: STATIONARY");
		}
	}

	/**
	 * Method to update the elevator direction
	 */
	@Override
	public void updateElevatorDirection(int elevatorId, Direction direction) {
		resetDirectionLamp(elevatorId);
		
		if(direction == Direction.UP) {
			labelArr[0][elevatorId - 1].setBackground(Color.YELLOW);
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: RUNNING");
		} else if(direction == Direction.DOWN) {
			labelArr[2][elevatorId - 1].setBackground(Color.YELLOW);
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: RUNNING");
		} else if(direction == Direction.STATIONARY) {
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: STATIONARY");
		}
	}

	/**
	 * Method to update the status of the elevator door
	 */
	@Override
	public void updateElevatorDoorsOpen(int elevatorId, boolean stopped) {
		// stopped == true -> DOORS OPENED
		if(stopped) {
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: DOORS OPENED");
		} else {
			// stopped == false -> RUNNING
			((JLabel)labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: RUNNING");
		}
	}

	/**
	 * Handle the Elevator error for that elevator
	 * and update the error status message
	 */
	@Override
	public void handleElevatorError(int elevatorId, Error error) {
		resetDirectionLamp(elevatorId);
		
		((JLabel) labelArr[1][elevatorId - 1].getComponent(2)).setText("Status: Error " + error);
		
		if(Error.ARRIVAL == error || Error.TIME == error) {
			labelArr[0][elevatorId - 1].setBackground(Color.RED);
			labelArr[2][elevatorId - 1].setBackground(Color.RED);
		} else if(Error.DOOR == error) {
			labelArr[0][elevatorId - 1].setBackground(Color.ORANGE);
			labelArr[2][elevatorId - 1].setBackground(Color.ORANGE);
		}
	}
	
	/**
	 * Method to reset lamp direction 
	 * @param elevatorId of the elevator 
	 */
	private void resetDirectionLamp(int elevatorId) {
		labelArr[0][elevatorId - 1].setBackground(Color.BLACK);
		labelArr[2][elevatorId - 1].setBackground(Color.BLACK);
	}
}
