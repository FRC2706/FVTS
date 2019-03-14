package ca.team2706.vision.vision2019;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.team2706.vision.vision2019.NormalisedPoint;

public class NormalisedPointTest {

	@SuppressWarnings("unused")
	@Test
	public void NormalisedPoint() {
		try {
			
			NormalisedPoint point = new NormalisedPoint(1,1);
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void getX() {
		try {
			
			int x = 1;
			
			NormalisedPoint point = new NormalisedPoint(x,0);
			
			if(point.getX() != x) {
				fail();
			}
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void getY() {
		try {
			
			int y = 1;
			
			NormalisedPoint point = new NormalisedPoint(0,y);
			
			if(point.getY() != y) {
				fail();
			}
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
}
