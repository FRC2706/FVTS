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
	private static final int RANGE = 10;
	private JButton btnStart;
	private JFrame frame;
	public AutoCallibrator() {
		Main.showMiddle = true;
		frame = new JFrame("Auto Callibration");
		frame.setSize(200, 60);
		frame.getContentPane().setLayout(null);

		btnStart = new JButton("Start");
		btnStart.setBounds(0, 0, 184, 23);
		btnStart.addActionListener(this);
		frame.getContentPane().add(btnStart);

		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			Main.showMiddle = false;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Main.visionParams.minSaturation = 150;
			Main.visionParams.maxSaturation = 255;
			Main.visionParams.minValue = 0;
			Main.visionParams.maxValue = 255;
			List<Pixel> touching = getTouching(Main.currentImage);
			Main.visionParams.minArea = touching.size();
			float[] hsv = new float[3];
			int middleX, middleY;
			middleX = Main.currentImage.getWidth() / 2;
			middleY = Main.currentImage.getHeight() / 2;
			Color color = new Color(Main.currentImage.getRGB(middleX, middleY));
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
			Main.visionParams.minHue = (int) (hsv[0] - 10);
			Main.visionParams.maxHue = (int) (hsv[0] + 10);
			Main.process = false;
			Mat frame = Main.getFrame();
			boolean success = false;
			for (int i = 0; i < 255; i++) {
				System.out.println("Checking");
				Main.selector.sMinVal.setValue(Main.selector.sMinVal.getValue()+1);
				VisionData data = Main.forceProcess(frame);
				if(data.targetsFound.size() == 1 && data.preferredTarget.xCentreNorm > -0.1 && data.preferredTarget.xCentreNorm < 0.1){
					System.out.println("Callibrated!");
					success = true;
					break;
				}
			}
			if(!success){
				System.out.println("Callibration failed! Manual callibration required");
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
				if (vals2[0] > vals[0] - RANGE && vals2[0] < vals[0] + RANGE) {
					if (vals2[1] > vals[1] - RANGE && vals2[1] < vals[1] + RANGE) {
						if (vals2[2] > vals[2] - RANGE && vals2[2] < vals[2] + RANGE) {
							matches.add(new Pixel(x, y, color2));
						}
					}
				}
			}
		}
		return matches;
	}
}
