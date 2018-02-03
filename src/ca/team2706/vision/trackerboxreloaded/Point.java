package ca.team2706.vision.trackerboxreloaded;

public class Point {
	
	private int x;
	private int y;
	private FrameData frameData;
	public Point(int x, int y, FrameData frameData) {
		this.x = x;
		this.y = y;
		this.frameData = frameData;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public FrameData getFrameData() {
		return frameData;
	}
	
}
