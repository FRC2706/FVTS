package ca.team2706.vision.vision2019;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import ca.team2706.vision.vision2019.Main.VisionData;
import ca.team2706.vision.vision2019.Main.VisionParams;

public class MainThread extends Thread {

	public VisionParams visionParams;

	public MainThread(VisionParams params) {
		this.visionParams = params;
	}

	public Mat frame;
	public double current_time_seconds;
	public boolean useCamera = true;
	public static int timestamp = 0;
	public double lastDist = 0;

	@Override
	public void run() {

		// Initializes a Matrix to hold the frame

		frame = new Mat();

		// Whether to use a camera, or load an image file from disk.
		if (visionParams.cameraSelect == -1) {
			useCamera = false;
		}

		if (useCamera) {
			try {
				CameraServer.initCamera(visionParams.cameraSelect);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// load the image from file.
			try {
				frame = Main.bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
			} catch (IOException e) {
				e.printStackTrace();
				frame = new Mat();
			}
		}
		// The window to display the raw image
		DisplayGui guiRawImg = null;
		// The window to display the processed image
		DisplayGui guiProcessedImg = null;
		// Wether to open the guis
		boolean use_GUI = true;
		// If on Linux don't use guis
		if (System.getProperty("os.name").toLowerCase().indexOf("raspbian") != -1) {
			use_GUI = false;
		}

		if (useCamera) {

			frame = CameraServer.getFrame(visionParams.cameraSelect);

		} else {
			try {
				frame = Main.bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		// Set up the GUI display windows
		if (use_GUI) {
			try {
				// Initilizes the window to display the raw image
				guiRawImg = new DisplayGui(Main.matToBufferedImage(frame), "Raw-" + visionParams.name, true);
				// Initilizes the window to display the processed image
				guiProcessedImg = new DisplayGui(Main.matToBufferedImage(frame), "Processed-" + visionParams.name,
						true);
			} catch (IOException e) {
				// means mat2BufferedImage broke
				// non-fatal error, let the program continue
			}
		}

		// Main video processing loop
		while (true) {
			try {
				if (useCamera) {
					// Read the frame from the camera, if it fails try again
					frame = CameraServer.getFrame(visionParams.cameraSelect);
				} // else use the image from disk that we loaded above
				if (use_GUI) {
					// Resize the frame
					Imgproc.resize(frame, frame, visionParams.sz);
				}
				// Process the frame!
				// Log when the pipeline starts
				long pipelineStart = System.nanoTime();
				// Process the frame
				VisionData visionData = Pipeline.process(frame, visionParams, use_GUI);
				// Log when the pipeline stops
				long pipelineEnd = System.nanoTime();
				// Selects the prefered target
				Pipeline.selectPreferredTarget(visionData, visionParams, visionParams.group == 1 ? true : false);
				// Creates the raw output image object
				Mat rawOutputImg;
				if (use_GUI) {
					// If use gui then draw the prefered target
					// Sets the raw image to the frame
					if(useCamera) {
						rawOutputImg = frame;
					}else {
						rawOutputImg = frame.clone();
					}
					// Draws the preffered target
					Pipeline.drawPreferredTarget(rawOutputImg, visionData);
				} else {
					// Sets the raw image to the frame
					rawOutputImg = frame;
				}

				if (visionData.preferredTarget != null)
					lastDist = visionData.preferredTarget.distance;

				// Sends the data to the vision table
				Main.sendVisionDataOverNetworkTables(visionData);

				// display the processed frame in the GUI
				if (use_GUI) {
					try {
						// May throw a NullPointerException if initializing
						// the window failed
						BufferedImage raw = Main.matToBufferedImage(rawOutputImg);

						guiRawImg.updateImage(raw);
						guiProcessedImg.updateImage(Main.matToBufferedImage(visionData.binMask));
					} catch (IOException e) {
						// means mat2BufferedImage broke
						// non-fatal error, let the program continue
						e.printStackTrace();
						continue;
					} catch (NullPointerException e) {
						e.printStackTrace();
						System.out.println("Window closed");
						Runtime.getRuntime().halt(0);
					} catch (Exception e) {
						// just in case
						e.printStackTrace();
						continue;
					}
				}
				if (useCamera) {
					// log images to file once every seconds_between_img_dumps
					double elapsedTime = ((double) System.currentTimeMillis() / 1000) - current_time_seconds;
					// If the elapsed time is more that the seconds between image
					// dumps
					// then dump images asynchronously
					if (elapsedTime >= visionParams.secondsBetweenImageDumps && visionParams.secondsBetweenImageDumps != -1) {
						// Sets the current number of seconds
						current_time_seconds = (((double) System.currentTimeMillis()) / 1000);
						try {
							Bundle b = new Bundle(Main.matToBufferedImage(frame),
									Main.matToBufferedImage(visionData.binMask), Main.matToBufferedImage(rawOutputImg),
									timestamp, visionParams);
							ImageDumpScheduler.schedule(b);
							timestamp++;
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
					}
				}
				// Display the frame rate onto the console
				double pipelineTime = (((double) (pipelineEnd - pipelineStart)) / Pipeline.NANOSECONDS_PER_SECOND)
						* 1000;
				System.out.printf("Vision FPS: %3.2f, pipeline took: %3.2f ms\n", visionData.fps, pipelineTime);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public void updateParams(VisionParams params) {

		if (visionParams.cameraSelect != params.cameraSelect) {
			useCamera = true;
			if (visionParams.cameraSelect == -1) {
				useCamera = false;
			}

			if (useCamera) {
				// Initilizes the camera
				try {
					CameraServer.initCamera(visionParams.cameraSelect);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				// load the image from file.
				try {
					frame = Main.bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
				} catch (IOException e) {
					e.printStackTrace();
					frame = new Mat();
				}
			}
		}

		this.visionParams = params;
	}

	public VisionData forceProcess() {

		VisionData visionData = Pipeline.process(frame, visionParams, false);

		Pipeline.selectPreferredTarget(visionData, visionParams, visionParams.group == 1 ? true : false);

		return visionData;

	}

}
