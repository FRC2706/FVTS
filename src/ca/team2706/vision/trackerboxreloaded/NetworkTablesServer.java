package ca.team2706.vision.trackerboxreloaded;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;

public class NetworkTablesServer {

	
	public static void main(String[] args) {
		//Inits stuff
		NetworkTableInstance instance = NetworkTableInstance.getDefault();
		instance.setUpdateRate(0.02);
		instance.startServer();
		NetworkTable visionTable = instance.getTable("vision");

		System.out.println("NetworkTables server started.\nOutputting table /vision");

		while (true) {
			boolean somethingPrinted = false;

			NetworkTableValue fps = visionTable.getEntry("fps").getValue();
			if (fps.isDouble()){
				System.out.print("fps: "+fps.getDouble()+", ");
				somethingPrinted = true;
			}

			NetworkTableValue ctrX = visionTable.getEntry("ctrX").getValue();
			if (ctrX.isDouble()) {
				System.out.println("ctrX: "+ctrX.getDouble()+", ");
				somethingPrinted = true;
			}

			NetworkTableValue numTargetFound = visionTable.getEntry("numTargetsFound").getValue();
			if (ctrX.isDouble()) {
				System.out.print("numTargetsFound: "+numTargetFound.getDouble()+", ");
				somethingPrinted = true;
			}

			// print a newline
			if (somethingPrinted)
				System.out.println();

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
