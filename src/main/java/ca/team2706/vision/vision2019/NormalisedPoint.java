package ca.team2706.vision.vision2019;

/**
 * A class to hold a x and y position
 */
public class NormalisedPoint {
	
	/**
	 * The X
	 */
	private int x;
	/**
	 * The Y
	 */
	private int y;
	
	/**
	 * Creates a Normalised Point
	 * @param x
	 * @param y
	 */
	public NormalisedPoint(int x, int y) {
		//Sets the x to the x
		this.x = x;
		//Sets the y to the y
		this.y = y;
	}
	/**
	 *Gets the X 
	 */
	public int getX() {
		return x;
	}
	/**
	 * Gets the Y
	 * @return
	 */
	public int getY() {
		return y;
	}
	
}
