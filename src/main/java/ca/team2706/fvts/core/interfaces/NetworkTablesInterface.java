package ca.team2706.fvts.core.interfaces;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.NetworkTablesManager;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.main.Main;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesInterface extends AbstractInterface {

	public NetworkTablesInterface() {
		super("networktables");
	}
	/**
	 * Turns all the vision data into packets that kno da wae to get to the robo rio
	 * :]
	 *
	 * @param data
	 */
	@Override
	public void publishData(VisionData data, MainThread thread) {
		NetworkTable visionTable = NetworkTablesManager.tables.get(data.params.getByName("name").getValue());
		// Sends the data
		// Puts the fps into the vision table
		visionTable.putNumber("fps", data.fps);
		// Puts the number of targets found into the vision table
		visionTable.putNumber("numTargetsFound", data.targetsFound.size());

		// If there is a target
		if (data.preferredTarget != null) {
			// Put the normalized x into the vision table
			visionTable.putNumber("ctrX", data.preferredTarget.xCentreNorm);
			// Puts the normalized area into the vision table
			visionTable.putNumber("area", data.preferredTarget.areaNorm);

			visionTable.putNumber("angle", data.preferredTarget.xCentreNorm * 45);
			
			visionTable.putNumber("distance", data.preferredTarget.distance);
		}
	}
	@Override
	public List<AttributeOptions> getOptions() {
		return new ArrayList<AttributeOptions>();
	}
	private boolean setup = false;

	/**
	 * Initilizes the Network Tables WARNING! Change 127.0.0.1 to the robot ip
	 * before it is on master or it will not be fun :)
	 */
	public void init(MainThread thread) {
		if(setup)
			return;
		// Tells the NetworkTable class that this is a client
		NetworkTable.setClientMode();
		// Sets the interval for updating NetworkTables
		NetworkTable.setUpdateRate(0.02);
		// Sets the vision table to the "vision" table that is in NetworkTables
		Main.loggingTable = NetworkTable.getTable("logging-level");

		boolean use_GUI = true;

		// If on Linux don't use guis
		if (System.getProperty("os.arch").toLowerCase().indexOf("arm") != -1) {
			use_GUI = false;
		}

		if (!use_GUI && Main.serverIp.equals("")) {

			// Sets the team number
			NetworkTable.setTeam(2706);
			// Enables DSClient
			NetworkTable.setDSClientEnabled(true);

		} else {

			if (Main.serverIp.equals("")) {
				Main.serverIp = "localhost";
			}

			// Sets the IP adress to connect to
			NetworkTable.setIPAddress(Main.serverIp);

		}

		// Initilizes NetworkTables
		NetworkTable.initialize();
		setup = true;
	}

}
