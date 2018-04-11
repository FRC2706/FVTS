package ca.team2706.vision.trackerboxreloaded;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.opencv.core.Mat;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;

public class AutoCallibrator implements ActionListener {
	//Hue range for setting min and max hue
	private static final int RANGE = 30;
	//Start button
	private JButton btnStart;
	//The frame
	private JFrame frame;

	//Start it
	public AutoCallibrator() {
		//Puts a red dot in the middle of the raw image window
		Main.showMiddle = true;
		//Open the window
		frame = new JFrame("Auto Callibration");
		//set the size
		frame.setSize(200, 60);
		//set the layout
		frame.getContentPane().setLayout(null);

		//Initilize the start button
		btnStart = new JButton("Start");
		//set the size
		btnStart.setBounds(0, 0, 184, 23);
		//register action listeners
		btnStart.addActionListener(this);
		//add it to the window
		frame.getContentPane().add(btnStart);

		//Make the window visible
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//if the start button is pressed
		if (e.getSource() == btnStart) {
			//clear the red dot
			Main.showMiddle = false;
			try {
				//Sleep for a sec
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//Set hard coded saturation values
			Main.selector.sMinSat.setValue(100);
			Main.selector.sMaxSat.setValue(255);
			//Set hard coded value values
			Main.selector.sMinVal.setValue(0);
			Main.selector.sMaxVal.setValue(255);
			//Get all the yellow pixels
			List<Pixel> touching = getTouching(Main.currentImage);
			//get stuff for minimum area
			String[] decimals = String.valueOf(
					(((double) touching.size() / (double) (Main.visionParams.width * Main.visionParams.height)) * 10)
							/ 10.0)
					.split("");
			String decimal = "";
			for (int i = 0; i < decimals.length && i < 4; i++) {
				decimal += decimals[i];
			}
			Main.selector.minArea.setText(decimal);
			//init a list for HSV
			float[] hsv = new float[3];
			//Init middle x and y
			int middleX, middleY;
			//Set middle x and y to the middle of the image
			middleX = Main.currentImage.getWidth() / 2;
			middleY = Main.currentImage.getHeight() / 2;
			//Gets the color of the middle pixel
			Color color = new Color(Main.currentImage.getRGB(middleX, middleY));
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
			//sets minimum and maximum hue to the hue of that pixel + or - the RANGE
			Main.selector.sMinHue.setValue((int) (hsv[0] * 255 - RANGE));
			Main.selector.sMaxHue.setValue((int) (hsv[0] * 255 + RANGE));
			//Tells vision to start processing again
			Main.process = false;
			//Gets the current frame
			Mat frame = Main.getFrame();
			//init the success boolean
			boolean success = false;
			for (int i = 0; i < 255; i++) {
				//Checks the min values and finds the one that is just right
				System.out.println("Checking");
				Main.selector.sMinVal.setValue(Main.selector.sMinVal.getValue() + 1);
				VisionData data = Main.forceProcess(frame);
				//if success
				if (data.targetsFound.size() == 1 && data.preferredTarget.xCentreNorm > -0.1
						&& data.preferredTarget.xCentreNorm < 0.1) {
					success = true;
					for (int i1 = 0; i1 < 255; i1++) {
						System.out.println("Checking");
						Main.selector.sMaxVal.setValue(Main.selector.sMaxVal.getValue() - 1);
						VisionData data1 = Main.forceProcess(frame);
						if (data1.targetsFound.size() == 1 && data1.preferredTarget.xCentreNorm > -0.1
								&& data1.preferredTarget.xCentreNorm < 0.1) {
							continue;
						} else {
							Main.selector.sMaxVal.setValue(Main.selector.sMaxVal.getValue() + 1);
						}
					}
					break;
				}
			}
			//if not success
			if (!success) {
				Main.selector.sMinVal.setValue(0);
				for (int i = 0; i < 255; i++) {
					System.out.println("Checking");
					Main.selector.sMaxVal.setValue(Main.selector.sMaxVal.getValue() - 1);
					VisionData data = Main.forceProcess(frame);
					if (data.targetsFound.size() == 1 && data.preferredTarget.xCentreNorm > -0.1
							&& data.preferredTarget.xCentreNorm < 0.1) {
						
						success = true;
						for (int i1 = 0; i1 < 255; i1++) {
							System.out.println("Checking");
							Main.selector.sMinVal.setValue(Main.selector.sMinVal.getValue() + 1);
							VisionData data1 = Main.forceProcess(frame);
							if (data1.targetsFound.size() == 1 && data1.preferredTarget.xCentreNorm > -0.1
									&& data1.preferredTarget.xCentreNorm < 0.1) {
								continue;
							} else {
								Main.selector.sMaxVal.setValue(Main.selector.sMaxVal.getValue() + 1);
							}
						}
						break;
					}
				}
				if (!success) {
					System.out.println("Callibration failed! Manual callibration required");
					// Main.loadVisionParams();
				}else{
					System.out.println("Callibrated!");
				}
			}else{
				System.out.println("Callibrated!");
			}
			this.frame.setVisible(false);
			Main.process = true;
		}
	}

	private List<Pixel> getTouching(BufferedImage image) {
		List<Pixel> matches = new ArrayList<Pixel>();
		int middleX, middleY;
		middleX = image.getWidth() / 2;
		middleY = image.getHeight() / 2;
		Color color = new Color(image.getRGB(middleX, middleY));
		float[] vals = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), vals);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				Color color2 = new Color(image.getRGB(x, y));
				float[] vals2 = new float[3];
				Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), vals2);
				if (vals2[0] * 255 > vals[0] * 255 - RANGE && vals2[0] * 255 < vals[0] * 255 + RANGE) {
					if (vals2[1] * 255 > vals[1] * 255 - RANGE && vals2[1] * 255 < vals[1] * 255 + RANGE) {
						if (vals2[2] * 255 > vals[2] * 255 - RANGE && vals2[2] * 255 < vals[2] * 255 + RANGE) {
							matches.add(new Pixel(x, y, color2));
						}
					}
				}
			}
		}
		return matches;
	}
}
