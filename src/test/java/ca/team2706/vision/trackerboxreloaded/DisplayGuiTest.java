package ca.team2706.vision.trackerboxreloaded;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

public class DisplayGuiTest {

	@SuppressWarnings("unused")
	@Test
	public void DisplayGui() {
		try {
			try {
				DisplayGui gui = new DisplayGui(null,"Test",false);
				fail();
			}catch(Exception e) {
				
			}
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			DisplayGui gui = new DisplayGui(image,"test",false);
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
				DisplayGui gui = new DisplayGui(0,0,"Test",false);
			}catch(Exception e) {
				fail(e.getMessage());
			}
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			DisplayGui gui = new DisplayGui(image,"test",false);
			gui.setVisible(false);
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void updateImage() {
		try {
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			
			DisplayGui gui = new DisplayGui(image,"test",false);
			
			gui.updateImage(image);
			
			gui.setVisible(false);
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	

}
