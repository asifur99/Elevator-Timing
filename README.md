## SYSC 3303 Term Project - L4 Group 6

### Instruction to Import the Project in Eclipse
1. Import the project archive file into Eclipse
2. From Eclipse
    * Select "File" 
    * Select "Import" 
    * Select "General"
    * Select "Projects from File System or Archive File" under "General"
    * Select "Archive..."
    * Locate "L4G6_Milestone5.zip" as Import Source
    * Click "Finish"

3. From within the project "ElevatorTiming"
    Before starting the code running, if you wish to change the times for elevators to move and/or doors to open, you can change the values in the sharedObjects/Constants.java file. Both values are time in milliseconds. Please do not reduce too close to 0, as there are potential dropped packets in this case.  If you encounter any errors while running the code, please ‘clean’ and ‘refresh’ the Project. In order to run the program, you can follow one of below listed set of steps:

    3.1	The main files for each subsystem can be run individually and have the outputs be shown on separate consoles.
        Run the files in this order: 
            * Run the UserInterfaceMain.java file from the main package.
            * Run the SchedulerMain.java from the main package.
            * Run the MainEM.java file from the main package.
            * Run the MainFM.java file from the main package.

    3.2	The main files can be run simultaneously with all their outputs being shown on the same console.
        Run the Main.java file from the main package.

### File Explanations
* **In elevatorSubsystem Folder:**
    - *Elevator.java*
        - Takes the desired floor request from the box and moves the elevator to the requested floor and then to the destination floor while updating its location as its moving.
    - *ElevatorThread2*
        - Takes the information from thread2 and makes sure the input is valid and sends success or failure to thread2. It then processes the data and send to the box for elevator to pick up the information.
    - *ElevatorThread3*
        - Takes the information from box of elevator requests and then send it to thread3 to update the mapping of the elevator locations and receives response from thread3.
    - *MainEM*
        - It has instances of the SchedulerElevatorBox, DoorBox, FailedJob, Elevators(3),ElevatorThread2, ElevatorThread2, ElevatorThread3, ElevatorThread4 for independent running of the threads.
    - *RequestHandler.java*
        - Takes in the request to hold the data in an Object enabling the transfer of information via packets or thread possible for further processing.
    - *DoorBox.java*
        - It holds a hashset to store the elevatorID that way the elevator can use it to close and open doors.
    - *ElevatorMotor.java*
        - It controls the movement of the elevator.
    - *ElevatorThread4.java*
        - Takes information from DoorIntermediateHost and puts in the elevatorID in the DoorBox for the elevator to access and send success/failure to DoorIntermediateHost.
    - *ElevatorThread1.java*
        - Thread is responsible for resending jobs that can no longer be completed by assigned elevator to scheduler system.
    - *FailedJob.java*
        - This box is where any elevator that failed to process all their jobs before stopping will submit them so that they can be reprocessed by the scheduler.

* **In floorSubsystem Folder:**
    - *ElevatorRequestParseException.java*
        - It checks if the request by the elevator is valid or not.
    - *FileInputException.java*
        - It checks if the file read has valid data or not.
    - *FileReader.java*
        - This class reads the data from the file and covert it to a list. 
    - *FileLoader.java*
        - It reads the requests from the input file and sends it to the SenderFM for further processing.
    - *ReceiverFM.java*
        - Receives information from Thread3 and puts it in the box for floor to access it.
    - *SenderFM.java*
        - Sends the parsed information to thread2 so the elevator subsystem can use it.
    - *MainFM.java*
        - It has instances of the FloorRequestBox, Floors(1), SenderFM, ReceiveFM, FloorResponse for independent running of the threads.
    - *FloorResponse.java*
        - It receives the door open responses from the scheduler.
    - *FloorDoorCloseHandler.java*
        - It sends the elevatorID to DoorIntermediateHost to send force open/close resposne to DoorBox via ElevatorThread4 when an error occurs.
    
* **In schedulerSubsystem Folder:**
    - *ElevatorData.java*
        - It takes keeps a track of all the elevators information.
    - *ElevatorServer.java*
        - This is a thread which will constantly listen on port 5014 for any information that was sent from an Elevator containing an updated location information.
    - *Thread1.java*
        - This is a thread which will constantly listen on port *** for any information that was sent from an Floor containing the packet of information.
    - *Thread2.java*
        - Takes requests received from the RequestQueue and sends to the ElevatorManager. It determines the requests to send based on priority.
    - *DoorIntermediateHost.java*
        - Parses the data received from FloorDoorCloseHandler and after validating we send it to ElevatorThread4 to send to the boxthat way appropriate actions will take place.

* **In sharedObjects Folder:**
    - *ElevatorRequest.java*
        - Creates data from input string provided from the file.
    - *FloorRequestBox.java*
        - It works as a mediator between floor and the floor manager.
    - *SchedulerElevatorBox.java*
        - It holds information that is accesses by Elevator, ElevatorThread2 and ElevatorThread3.
    - *SchedulerFloorRequestBox.java*
        - It hold the updates in a queue and then send the updates to Floor Manager who then sends that to the Elevator location maps in pair.
    - *Pair.java*
        - It stores the elevator details so it can be stored in the ElevatorServer
    - *Constants.java*
        - This class contains constants required across the different classes.
    - *Direction.java*
        - This class contains all possible elevator directions.
    - *Error.java*
        - This class contains all possible elevator errors.

* **In uiSubsystem Folder:**
    - *FrontEndInterface.java*
        - An interface to provide abstraction to the ui
    - *TriangleItem.java*
        - Compnenet for the up and down arrow in ui
    - *UserInterfaceEndpoint.java*
        - Accepts request from Backend to update the ui through a UDP packet.
    - *View.java*
        - UI view

### UML Class, Sequence, State Machine and Timing Diagrams
#### - elevatorSubsystem_UML_Iteration5.jpg
#### - floorSubsystem_UML_iteration5.jpg
#### - schedulerSubsystem_UML_Iteration5.jpg
#### - SequenceDiagram_Iteration5.png
#### - Timing_Diagram_for_Scheduler_Threads_Iteration5.jpg
#### - SchedulerSS_StateDiagram_Iteration5.png

### Additional Documentation
#### - SYSC3303-FinalProjectDemo-L4G6.mp4 : **Project Demo Video**
#### - SYSC3303-FinalProjectReport-L4G6.pdf : **Project Report**

### Input.txt explanation
_*Eg data*:_ "14:05:15.000 2 Up 4 DOOR 3"

|     Time     |  RequestedFloor  |  Direction  |  DestinationFloor  |  ErrorType  |  ErrorFloor  |
|--------------|:----------------:|:-----------:|:------------------:|:-----------:|:------------:|
| 14:05:15.000 |              2   | Up          |             4      |        DOOR |            3 |

### Breakdown of Responsibilities

## Iteration 1
- Ashton Mohns: 
    - Floor Subsystem and their related classes
- Edmond Chow: 
    - Scheduler and Elevator Subsystem and their related classes
- Jyotsna Mahesh: 
    - UML and Sequence Documentation
- Asifur Rahman: 
    - UML, README and Testing
- Md Aiman Sharif:  
    - Testing framework

## Iteration 2
- Ashton Mohns: 
    - Thread3 Class Creation
    - Map Class Creation
- Edmond Chow: 
    - Thread2 Class Creation
    - Map Class Creation
    - ReqBox implementation for Schedular Subsystem
- Jyotsna Mahesh: 
    - Floor Subsystem Creation
    - Thread1 Class Creation
    - ReqBox implementation for Floor Subsystem
- Asifur Rahman: 
    - ElevatorSubSystem Creation
    - Updating README file
- Md Aiman Sharif:  
    - Drawing UML Class Diagram
    - JUnit Testing

## Iteration 3
- Ashton Mohns: 
    - Drawing Sequence Diagram 
    - Update and refactor code for Floor subsystem
- Edmond Chow: 
    - JUnit testing 
    - Update threads and code for Elevator subsystem
- Jyotsna Mahesh: 
    - Remodeling Floor Subsystem 
    - Drawing State Machines Diagram
- Asifur Rahman: 
    - Remodeling of the ElevatorSubSystem
    - Updating README file
- Md Aiman Sharif:  
    - Drawing UML Class Diagram
    - Updating Javadoc 
    - Updating Floor Manager’s Implementation

## Iteration 4
- Ashton Mohns:
    - Implementing an intermediate host for forwarding door closes from floor to elevator subsystem
    - Updating tests with the new request packets
    - Helping with state diagrams
- Edmond Chow: 
    - Debug testing
    - Elevator Rebuild, Elevator Fault Handling
    - Debug setting for console output
- Jyotsna Mahesh: 
    - Upgrade file reader and Direction Enum 
    - Create Error Enum
    - Update Print statements
    - Documentation (sequence and state diagrams)
- Asifur Rahman: 
    - Implementing ElevatorThread4 class for handling door close/open functionality
    - Upgrading ElevatorBox for error handling
    - Updating README file
    - Modifying RequestHandler to meet the current criteria
- Md Aiman Sharif:  
    - Floor door handling
    - Updating packets sent by the floor subsystem to handle errors
    - Updating Class diagrams

## Iteration 5
- Ashton Mohns:
    - GUI
    - Acceptance testing
    - Reflections
- Edmond Chow: 
    - Documentation
    - Integration Testing
    - Refactor code, remove bugs
    - Error handling unit tests
- Jyotsna Mahesh:
    - Created more Test Files
    - Diagrams: Sequence, State Machine and timing
    - Worked on the Report
- Asifur Rahman: 
    - Creating and maintaining the report
	- Diagrams: UML, Sequence
- Md Aiman Sharif:
    - Designed the GUI- code
    - Implemented GUI endpoints
    - User Interface testing
    - Worked on report

# Members:
1. Ashton Mohns: 101074479
2. Edmond Chow: 100883365
3. Jyotsna Mahesh: 101084851
4. Asifur Rahman: 101069183
5. Md Aiman Sharif: 101062765
