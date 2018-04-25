package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;

public class Bundle {
	private BufferedImage raw,binMask,output;
	private int timestamp;

	public Bundle(BufferedImage raw, BufferedImage binMask, BufferedImage output, int timestamp) {
		this.raw = raw;
		this.binMask = binMask;
		this.output = output;
		this.timestamp = timestamp;
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
}
