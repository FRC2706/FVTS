package ca.team2706.fvts.main;

import java.io.File;
import java.util.List;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.params.VisionParams;

public class ConfigTester {
	public static void main(String[] args) {
		System.out.println("FVTS Configuration Tester "+Constants.VERSION_STRING+" developed by "+Constants.AUTHOR);
		Main.visionParamsFile = new File("visionParams.properties");
		List<VisionParams> params = Utils.loadVisionParams();
		
	}
}
