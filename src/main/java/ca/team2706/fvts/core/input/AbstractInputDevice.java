package ca.team2706.fvts.core.input;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

public abstract class AbstractInputDevice {
	private String name;
	private boolean staticFrame = false;

	public boolean isStaticFrame() {
		return staticFrame;
	}

	public AbstractInputDevice(String name) {
		this.name = name;
	}
	public AbstractInputDevice(String name, boolean staticFrame) {
		this(name);
		this.staticFrame = staticFrame;
	}

	public String getName() {
		return name;
	}
	public abstract void init(String identifier) throws Exception;
	public abstract Mat getFrame(String identifier);
	
	public static List<AbstractInputDevice> inputs = null;
	public static AbstractInputDevice getByName(String name) {
		if(inputs == null) {
			inputs = new ArrayList<AbstractInputDevice>();
			inputs.add(new USBCameraInputDevice());
			inputs.add(new ImageDummyInputDevice());
			inputs.add(new VideoDummyInputDevice());
		}
		for(AbstractInputDevice input : inputs) {
			if(input.getName().equalsIgnoreCase(name))
				return input;
		}
		return null;
	}
}
