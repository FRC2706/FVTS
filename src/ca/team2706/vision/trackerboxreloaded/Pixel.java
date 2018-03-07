package ca.team2706.vision.trackerboxreloaded;

import java.awt.Color;

public class Pixel {
	private int x,y;
	private Color color;
	public Pixel(int x, int y, Color color){
		this.x = x;
		this.y = y;
		this.color = color;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Color getColor() {
		return color;
	}
	
}
