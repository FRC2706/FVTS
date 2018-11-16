package ca.team2706.vision.trackerboxreloaded;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.opencv.core.Mat;

public class MainTest {

	@Test
	public void setFrame() {
		try {
			
			Mat frame = new Mat();
			
			Main.setFrame(frame);
			
			if(!(Main.frame == frame)) {
				fail();
			}
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void initNetworkTables() {
		try {
			
			
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}

}
