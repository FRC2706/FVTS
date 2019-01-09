package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;

import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;

public class Bundle {
	private BufferedImage raw,binMask,output;
	private int timestamp;
	private VisionParams params;

	public Bundle(BufferedImage raw, BufferedImage binMask, BufferedImage output, int timestamp, VisionParams params) {
		this.raw = raw;
		this.binMask = binMask;
		this.output = output;
		this.timestamp = timestamp;
		this.params = params;
	}

	public BufferedImage getBinMask() {
		return binMask;
	}

	public BufferedImage getOutput() {
		return output;
	}

	public BufferedImage getRaw() {
		return raw;
	}

	public int getTimeStamp(){
		return timestamp;
	}

	public VisionParams getParams() {
		return params;
	}
	
}
