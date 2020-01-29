package ca.team2706.fvts.main;

import java.io.IOException;
import java.util.Scanner;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.DisplayGui;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.VisionCameraServer;

public class CameraViewer {
	public static void main(String[] args) {
		System.out.println("FVTS CameraViewer " + Constants.VERSION_STRING + " developed by " + Constants.AUTHOR);
		System.loadLibrary(Constants.OPENCV_LIBRARY);
		int cameraNum = -1;
		if (args.length > 0) {
			try {
				cameraNum = Integer.valueOf(args[0]);
				if (cameraNum < 0)
					throw new Exception();
			} catch (Exception e) {
				System.err.println("The only argument should a camera number that is greater or equal to 0!");
				System.exit(1);
			}
		} else {
			System.out.print("Camera number: ");
			Scanner in = new Scanner(System.in);
			while (cameraNum < 0) {
				try {
					cameraNum = Integer.valueOf(in.nextLine());
				} catch (Exception e) {
					System.err.println("The camera number must be an integer greater than or equal to 0");
				}
			}
			in.close();
		}
		System.out.println("Initializing camera!");
		VisionCameraServer.startServer();
		try {
			VisionCameraServer.initCamera("usb", cameraNum + "");
		} catch (Exception e) {
			System.err.println("Failed to open camera #" + cameraNum);
			e.printStackTrace();
			System.exit(2);
		}
		System.out.println("Successfully initialized camera");

		Mat m = VisionCameraServer.getFrame("usb", "" + cameraNum);

		DisplayGui window = null;
		try {
			window = new DisplayGui(Utils.matToBufferedImage(m), "CameraViewer", true);
		} catch (IOException e) {
			System.err.println("Failed to read image from the camera!");
			e.printStackTrace();
			System.exit(3);
		}

		m.release();

		while (true) {
			m = VisionCameraServer.getFrame("usb", "" + cameraNum);
			if(m.empty())
				// Image is empty
				continue;
			try {
				window.updateImage(Utils.matToBufferedImage(m));
			} catch (IOException e) {
				System.err.println("Failed to read image from the camera!");
				e.printStackTrace();
				System.exit(3);
			}
			m.release();
		}

	}
}
