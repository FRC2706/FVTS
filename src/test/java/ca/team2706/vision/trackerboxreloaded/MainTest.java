package ca.team2706.vision.trackerboxreloaded;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionData.Target;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class MainTest {

	static {
		// Loads OpenCV
		System.loadLibrary("opencv_java310");
	}
	
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
			
			Main.initNetworkTables();
			
			NetworkTable.shutdown();
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void loadVisionParams(){
		try {
			
				Main.loadVisionParams();
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void saveVisionParams(){
		try {
			
				Main.saveVisionParams();
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void sendVisionDataOverNetworkTables() {
		try {
			
			Main.initNetworkTables();
			
			VisionData data = new VisionData();
			data.fps = 0;
			data.targetsFound = new ArrayList<Target>();
			
			Main.sendVisionDataOverNetworkTables(data);
			
			NetworkTable.shutdown();
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	/*
	@Test
	public void matToBufferedImage() {
		try {
			
			Mat mat = new Mat(10,10,CvType.CV_8U);
			BufferedImage image = Main.matToBufferedImage(mat);
			if(image == null) {
				fail();
			}
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void bufferedImageToMat() {
		try {
			
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_3BYTE_BGR);
			
			Mat mat = Main.bufferedImageToMat(image);
			
			if(mat == null) {
				fail();
			}
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void imgDump() {
		try {
			
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			
			Main.imgDump(image, "test", Main.timestamp);
			
		}catch(FileNotFoundException e) {
			return;
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void main() {
		try {
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Main.b = false;
				}
				
			}).start();
			
			Main.main(null);
			Main.b = true;
			
			NetworkTable.shutdown();
			
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	*/
	@Test
	public void hideMiddle() {
		try {
			
			Main.showMiddle = true;
			
			Main.hideMiddle();
			if(Main.showMiddle) {
				fail();
			}
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void showMiddle() {
		try {
			
			Main.showMiddle = false;
			
			Main.showMiddle();
			if(!Main.showMiddle) {
				fail();
			}
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void forceProcess() {
		try {
			
			Mat frame = new Mat(10,10,CvType.CV_8U);
			
			Main.forceProcess(frame);
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}

}
