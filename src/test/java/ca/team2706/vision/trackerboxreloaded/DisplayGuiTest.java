package ca.team2706.vision.trackerboxreloaded;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

public class DisplayGuiTest {

	/*@SuppressWarnings("unused")
	@Test
	public void DisplayGui() {
		try {
			try {
				DisplayGui gui = new DisplayGui(null,"Test");
				fail();
			}catch(Exception e) {
				
			}
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			DisplayGui gui = new DisplayGui(image,"test");
			gui.setVisible(false);
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void DisplayGui2() {
		try {
			try {
				DisplayGui gui = new DisplayGui(0,0,"Test");
			}catch(Exception e) {
				fail(e.getMessage());
			}
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			DisplayGui gui = new DisplayGui(image,"test");
			gui.setVisible(false);
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void updateImage() {
		try {
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			
			DisplayGui gui = new DisplayGui(image,"test");
			
			gui.updateImage(image);
			
			gui.setVisible(false);
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void run() {
		try {
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			
			DisplayGui test = new DisplayGui(image,"test");
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					test.b = false;
				}
				
			}).start();
			
			test.run();
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void render() {
		try {
			
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			
			DisplayGui test = new DisplayGui(image,"test");
			
			test.render();
			
			test.b = false;
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	*/

}
