package ca.team2706.fvts.main;

import java.io.File;
import java.util.List;
import java.util.Map;

import ca.team2706.fvts.core.ConfigParser;
import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.params.VisionParams;

public class ConfigTester {
	public static void main(String[] args) throws Exception{
		System.out.println("FVTS Configuration Tester "+Constants.VERSION_STRING+" developed by "+Constants.AUTHOR);
		Main.visionParamsFile = new File("visionParams.properties");
		
		// Check if allowOverride is enabled
		Map<String, String> masterConfig = ConfigParser.getProperties(Main.MASTER_CONFIG_FILE, "config");
		
		boolean allowOverride = Boolean.valueOf(masterConfig.get("allowOverride"));
		if(allowOverride)
			System.out.println("Warn: Remote override is enabled!!!");
		
		// Check to make sure all the profiles are enabled
		Map<String, String> masterEnabled = ConfigParser.getProperties(Main.MASTER_CONFIG_FILE, "enabled");
		
		for(String s : masterEnabled.keySet()) {
			boolean b = Boolean.valueOf(masterEnabled.get(s));
			if(!b)
				System.out.println("Warn: Profile "+s+" is disabled!!!");
		}
		
		// Check all the vision parameters for obvious errors
		List<VisionParams> params = Utils.loadVisionParams();
		for(VisionParams p : params) {
			String name = p.getByName("name").getValue();
			if(p.getByName("type").getValue().equals("usb")) {
				if(p.getByName("identifier").getValueI() < 0)
					System.out.println("Warn: "+name+" is set to use an image as input!!!");
			}
			if(p.getByName("width").getValueI() > 160)
				System.out.println("Warn: "+name+" is set to use a resolution with a width of > 160!!!");
			if(p.getByName("height").getValueI() > 120)
				System.out.println("Warn: "+name+" is set to use a resolution with a height of > 120!!!");
			if(p.getByName("minHue").getValueI() < 0)
				System.out.println("ERR: "+name+" is using a minimum hue that is less than 0!!!!!");
			if(p.getByName("maxHue").getValueI() > 255)
				System.out.println("ERR: "+name+" is using a maximum hue that is more that 255!!!!!");
			if(p.getByName("minSaturation").getValueI() < 0)
				System.out.println("ERR: "+name+" is using a minimum saturation that is less than 0!!!!!");
			if(p.getByName("maxSaturation").getValueI() > 255)
				System.out.println("ERR: "+name+" is using a maximum saturation that is more than 255!!!!!");
			if(p.getByName("minValue").getValueI() < 0)
				System.out.println("ERR: "+name+" is using a minimum value that is less than 0!!!!!");
			if(p.getByName("maxValue").getValueI() > 255)
				System.out.println("ERR: "+name+" is using a maximum value that is more than 255!!!!!");
			if(p.getByName("erodeDilateIterations").getValueI() < 0)
				System.out.println("ERR: "+name+" is using an erode dilate iterations count that is negative!!!!!");
			if(p.getByName("erodeDilateIterations").getValueI() > 3)
				System.out.println("Warn: "+name+" is using an erode dilate iterations count that is more than 3 which may cause performance issues!!!");
		}
		
	}
}
