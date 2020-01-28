package ca.team2706.mergevision.core;

import org.opencv.core.Point;

public class OrderedPoint implements Comparable<OrderedPoint>{

	private double type;
	private Point p;
	
	public Point getP() {
		return p;
	}

	public double getType() {
		return type;
	}

	public OrderedPoint(Point p, double type) {
		super();
		this.p = p;
		this.type = type;
	}
	
	@Override
	public int compareTo(OrderedPoint o) {
		
		return Double.compare(type, o.type);
		
	}
	
}
